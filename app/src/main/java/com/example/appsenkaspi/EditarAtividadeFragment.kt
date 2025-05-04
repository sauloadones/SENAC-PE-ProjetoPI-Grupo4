package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appsenkaspi.Converters.Cargo
import com.example.appsenkaspi.Converters.PrioridadeAtividade
import com.example.appsenkaspi.databinding.FragmentEditarAtividadeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditarAtividadeFragment : Fragment() {

    private var _binding: FragmentEditarAtividadeBinding? = null
    private val binding get() = _binding!!

    private val atividadeViewModel: AtividadeViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels() // ✅ Adicionado

    private var atividadeId: Int = -1
    private lateinit var atividadeOriginal: AtividadeEntity
    private var dataInicio: Date? = null
    private var dataFim: Date? = null
    private var prioridadeSelecionada: PrioridadeAtividade? = null
    private val funcionariosSelecionados = mutableListOf<FuncionarioEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarAtividadeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {

                Cargo.COORDENADOR -> {
                    binding.botaoConfirmarAtividade.visibility = View.VISIBLE
                    binding.botaoPedirConfirmarAtividade.visibility = View.GONE
                }

                Cargo.GESTOR -> {
                    binding.botaoConfirmarAtividade.visibility = View.GONE
                    binding.botaoPedirConfirmarAtividade.visibility = View.VISIBLE
                }

                else -> {
                    binding.botaoConfirmarAtividade.visibility = View.GONE
                    binding.botaoPedirConfirmarAtividade.visibility = View.GONE
                }
            }
        }


        atividadeId = arguments?.getInt("atividadeId") ?: -1
        if (atividadeId == -1) {
            Toast.makeText(requireContext(), "Erro: atividade inválida!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }


        atividadeViewModel.getAtividadeComFuncionariosById(atividadeId).observe(viewLifecycleOwner) { atividadeComFuncionarios ->
            if (atividadeComFuncionarios != null) {
                atividadeOriginal = atividadeComFuncionarios.atividade
                funcionariosSelecionados.clear()
                funcionariosSelecionados.addAll(atividadeComFuncionarios.funcionarios)
                preencherCampos(atividadeComFuncionarios)
            } else {
                Toast.makeText(requireContext(), "Atividade não encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        binding.areaPrioridade.setOnClickListener {
            EscolherPrioridadeDialogFragment().show(parentFragmentManager, "EscolherPrioridade")
        }

        binding.areaResponsaveis.setOnClickListener {
            SelecionarResponsavelDialogFragment().show(parentFragmentManager, "EscolherResponsaveis")
        }

        binding.textDataInicio.setOnClickListener { abrirDatePicker(true) }
        binding.textDataFim.setOnClickListener { abrirDatePicker(false) }

        binding.botaoConfirmarAtividade.setOnClickListener {
            salvarAlteracoes()
        }

        parentFragmentManager.setFragmentResultListener("prioridadeSelecionada", viewLifecycleOwner) { _, bundle ->
            val valor = bundle.getString("valor")
            prioridadeSelecionada = PrioridadeAtividade.values().find { it.name == valor }
            atualizarVisualPrioridade()
        }

        parentFragmentManager.setFragmentResultListener("funcionariosSelecionados", viewLifecycleOwner) { _, bundle ->
            val selecionados = bundle.getParcelableArrayList<FuncionarioEntity>("listaFuncionarios") ?: return@setFragmentResultListener
            funcionariosSelecionados.clear()
            funcionariosSelecionados.addAll(selecionados)
            exibirFotosSelecionadas(funcionariosSelecionados)
        }
    }

    private fun preencherCampos(atividade: AtividadeComFuncionarios) {
        binding.inputNomeAtividade.setText(atividade.atividade.nome)
        binding.inputDescricao.setText(atividade.atividade.descricao)

        dataInicio = atividade.atividade.dataInicio
        dataFim = atividade.atividade.dataPrazo

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        binding.textDataInicio.text = "Data de início: ${sdf.format(dataInicio!!)}"
        binding.textDataFim.text = "Data de término: ${sdf.format(dataFim!!)}"

        prioridadeSelecionada = atividade.atividade.prioridade
        atualizarVisualPrioridade()
        exibirFotosSelecionadas(atividade.funcionarios)
    }

    private fun atualizarVisualPrioridade() {
        val corFundo = when (prioridadeSelecionada) {
            PrioridadeAtividade.BAIXA -> 0xFF2ECC40.toInt()
            PrioridadeAtividade.MEDIA -> 0xFFF1C40F.toInt()
            PrioridadeAtividade.ALTA -> 0xFFE74C3C.toInt()
            null -> 0xFFAAAAAA.toInt()
        }

        val corTexto = when (prioridadeSelecionada) {
            PrioridadeAtividade.MEDIA -> 0xFF000000.toInt()
            else -> 0xFFFFFFFF.toInt()
        }

        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(corFundo)
        }

        binding.textPrioridade.apply {
            background = bg
            setTextColor(corTexto)
            text = when (prioridadeSelecionada) {
                PrioridadeAtividade.BAIXA -> "Prioridade Baixa"
                PrioridadeAtividade.MEDIA -> "Prioridade Média"
                PrioridadeAtividade.ALTA -> "Prioridade Alta"
                null -> "Prioridade"
            }
            setPadding(32, 16, 32, 16)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }

    private fun abrirDatePicker(isInicio: Boolean) {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendario.set(year, month, day)
                val data = calendario.time
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                if (isInicio) {
                    dataInicio = data
                    binding.textDataInicio.text = "Data de início: ${format.format(data)}"
                } else {
                    dataFim = data
                    binding.textDataFim.text = "Data de término: ${format.format(data)}"
                }
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun exibirFotosSelecionadas(lista: List<FuncionarioEntity>) {
        val container = binding.containerFotosResponsaveis
        container.removeAllViews()

        val dimensao = resources.getDimensionPixelSize(R.dimen.tamanho_foto_responsavel)

        lista.forEach { funcionario ->
            val imageView = de.hdodenhof.circleimageview.CircleImageView(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(dimensao, dimensao).apply {
                    marginEnd = 16
                }
                borderWidth = 2
                borderColor = ContextCompat.getColor(context, android.R.color.white)

                Glide.with(this)
                    .load(funcionario.fotoPerfil)
                    .placeholder(R.drawable.ic_person)
                    .into(this)
            }

            container.addView(imageView)
        }
    }

    private fun salvarAlteracoes() {
        val nome = binding.inputNomeAtividade.text.toString().trim()
        val descricao = binding.inputDescricao.text.toString().trim()

        if (nome.isEmpty() || descricao.isEmpty() || dataInicio == null || dataFim == null || prioridadeSelecionada == null || funcionariosSelecionados.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val atividadeAtualizada = atividadeOriginal.copy(
            nome = nome,
            descricao = descricao,
            dataInicio = dataInicio!!,
            dataPrazo = dataFim!!,
            prioridade = prioridadeSelecionada!!,
            funcionarioId = funcionariosSelecionados.first().id
        )

        viewLifecycleOwner.lifecycleScope.launch {
            atividadeViewModel.atualizar(atividadeAtualizada)
            atividadeViewModel.deletarRelacoesPorAtividade(atividadeId)
            funcionariosSelecionados.forEach { funcionario ->
                val relacao = AtividadeFuncionarioEntity(
                    atividadeId = atividadeId,
                    funcionarioId = funcionario.id
                )
                atividadeViewModel.inserirRelacaoFuncionario(relacao)
            }

            // ✅ Atualiza o status da ação automaticamente após edição
            acaoViewModel.atualizarStatusAcaoAutomaticamente(atividadeAtualizada.acaoId)
        }

        Toast.makeText(requireContext(), "Atividade atualizada com sucesso!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
