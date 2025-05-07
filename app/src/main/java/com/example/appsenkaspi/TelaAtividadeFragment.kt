package com.example.appsenkaspi


import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appsenkaspi.databinding.FragmentTelaAtividadeBinding

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TelaAtividadeFragment : Fragment() {

    private var _binding: FragmentTelaAtividadeBinding? = null
    private val binding get() = _binding!!
    private val atividadeViewModel: AtividadeViewModel by activityViewModels()
    private val checklistViewModel: ChecklistViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val requisicaoViewModel: NotificacaoViewModel by activityViewModels()
    private var atividadeId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTelaAtividadeBinding.inflate(inflater, container, false)
        atividadeId = arguments?.getInt("atividadeId") ?: -1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)


        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {
                Cargo.APOIO -> {
                    binding.cardConfirmarAtividade.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.VISIBLE
                    binding.btnEditar.visibility = View.VISIBLE
                    binding.btnDeletar.visibility = View.GONE
                }
                Cargo.COORDENADOR -> {
                    binding.cardConfirmarAtividade.visibility = View.VISIBLE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                    binding.btnDeletar.visibility = View.VISIBLE
                    binding.btnEditar.visibility = View.VISIBLE
                }
                else -> {
                    binding.cardConfirmarAtividade.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                    binding.btnEditar.visibility = View.GONE
                    binding.btnDeletar.visibility = View.GONE
                }
            }
        }

        if (atividadeId == -1) {
            Toast.makeText(requireContext(), "Erro: atividade inválida.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        atividadeViewModel.getAtividadeComFuncionariosById(atividadeId).observe(viewLifecycleOwner) { atividadeComFuncionarios ->
            if (atividadeComFuncionarios != null) {
                preencherCampos(atividadeComFuncionarios)
            } else {
                Toast.makeText(requireContext(), "Atividade não encontrada!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        val checklistAdapter = ChecklistAdapter(
            itens = emptyList(),
            onItemCheckedChanged = { itemAtualizado, _ -> checklistViewModel.atualizar(itemAtualizado) },
            onDeleteItem = { item -> checklistViewModel.deletar(item) }
        )

        binding.recyclerChecklist.apply {
            adapter = checklistAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        }

        checklistViewModel.getChecklist(atividadeId).observe(viewLifecycleOwner) { itens ->
            checklistAdapter.atualizarLista(itens)
        }

        binding.btnEditar.setOnClickListener {
            val args = Bundle().apply { putInt("atividadeId", atividadeId) }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, EditarAtividadeFragment::class.java, args)
                .addToBackStack(null)
                .commit()
        }

        binding.cardConfirmarAtividade.setOnClickListener {
            atividadeViewModel.getAtividadeById(atividadeId).observe(viewLifecycleOwner) { atividade ->
                if (atividade != null) {
                    val atividadeAtualizada = atividade.copy(status = StatusAtividade.CONCLUIDA)
                    atividadeViewModel.atualizar(atividadeAtualizada)

                    // ✅ Atualiza o status da ação vinculada
                    acaoViewModel.atualizarStatusAcaoAutomaticamente(atividade.acaoId)

                    Toast.makeText(requireContext(), "Atividade marcada como concluída!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }


        binding.btnDeletar.setOnClickListener {
            mostrarDialogoConfirmacao()
        }

        binding.cardPedirConfirmacao.setOnClickListener {
            atividadeViewModel.getAtividadeComFuncionariosById(atividadeId).observe(viewLifecycleOwner) { atividade ->
                val funcionario = funcionarioViewModel.funcionarioLogado.value ?: return@observe

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val acao = AppDatabase.getDatabase(requireContext()).acaoDao().getAcaoPorIdDireto(atividade.atividade.acaoId)
                    val nomePilar = acao?.pilarId?.let {
                        AppDatabase.getDatabase(requireContext()).pilarDao().getNomePilarPorId(it)
                    } ?: "Pilar não identificado"

                    val atividadeJson = AtividadeJson(
                        id = atividade.atividade.id,
                        nome = atividade.atividade.nome,
                        descricao = atividade.atividade.descricao,
                        dataInicio = atividade.atividade.dataInicio,
                        dataPrazo = atividade.atividade.dataPrazo,
                        status = StatusAtividade.CONCLUIDA,
                        prioridade = atividade.atividade.prioridade,
                        criadoPor = atividade.atividade.criadoPor,
                        dataCriacao = atividade.atividade.dataCriacao,
                        acaoId = atividade.atividade.acaoId,
                        nomePilar = nomePilar,
                        responsaveis = atividade.funcionarios.map { it.id }
                    )

                    val json = Gson().toJson(atividadeJson)

                    val requisicao = RequisicaoEntity(
                        tipo = TipoRequisicao.COMPLETAR_ATIVIDADE,
                        atividadeJson = json,
                        status = StatusRequisicao.PENDENTE,
                        solicitanteId = funcionario.id,
                        dataSolicitacao = Date()
                    )

                    requisicaoViewModel.inserirRequisicao(requisicao)

                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Requisição de conclusão enviada para aprovação!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    private fun preencherCampos(atividade: AtividadeComFuncionarios) {
        binding.tituloAtividade.text = atividade.atividade.nome
        binding.descricaoAtividade.text = atividade.atividade.descricao

        val prioridade = atividade.atividade.prioridade
        binding.textPrioridade.text = when (prioridade) {
            PrioridadeAtividade.ALTA -> "Prioridade Alta"
            PrioridadeAtividade.MEDIA -> "Prioridade Média"
            PrioridadeAtividade.BAIXA -> "Prioridade Baixa"
        }

        val corPrioridade = when (prioridade) {
            PrioridadeAtividade.ALTA -> Color.parseColor("#E74C3C")
            PrioridadeAtividade.MEDIA -> Color.parseColor("#F1C40F")
            PrioridadeAtividade.BAIXA -> Color.parseColor("#2ECC40")
        }

        binding.layoutPrioridade.backgroundTintList = ColorStateList.valueOf(corPrioridade)

        val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        binding.textDataInicio.text = "Data de início da atividade ${sdf.format(atividade.atividade.dataInicio)}"
        binding.textDataPrazo.text = "Essa atividade deve ser finalizada até ${sdf.format(atividade.atividade.dataPrazo)}"

        binding.containerResponsaveis.removeAllViews()
        val dimensao = resources.getDimensionPixelSize(R.dimen.tamanho_foto_responsavel)
        atividade.funcionarios.forEach { funcionario ->
            val foto = de.hdodenhof.circleimageview.CircleImageView(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(dimensao, dimensao).apply { marginEnd = 12 }
                borderWidth = 2
                borderColor = Color.WHITE
            }
            Glide.with(this).load(funcionario.fotoPerfil).placeholder(R.drawable.ic_person).into(foto)
            binding.containerResponsaveis.addView(foto)
        }
    }

    private fun mostrarDialogoConfirmacao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmação")
            .setMessage("Você tem certeza de que deseja deletar a atividade \"${binding.tituloAtividade.text}\"?")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Confirmar") { _, _ ->
                atividadeViewModel.getAtividadeById(atividadeId).observe(viewLifecycleOwner) { atividade ->
                    if (atividade != null) {
                        atividadeViewModel.deletarAtividadePorId(atividadeId)
                        acaoViewModel.atualizarStatusAcaoAutomaticamente(atividade.acaoId)
                    }
                }
                Toast.makeText(requireContext(), "Atividade deletada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.RED)
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
