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
import com.bumptech.glide.Glide
import com.example.appsenkaspi.Converters.PrioridadeAtividade
import com.example.appsenkaspi.Converters.StatusAtividade
import com.example.appsenkaspi.databinding.FragmentTelaAtividadeBinding
import com.example.appsenkaspi.Converters.Cargo
import com.example.appsenkaspi.Converters.StatusRequisicao
import com.example.appsenkaspi.Converters.TipoRequisicao
import com.example.appsenkaspi.utils.configurarBotaoVoltar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TelaAtividadeFragment : Fragment() {

    private var _binding: FragmentTelaAtividadeBinding? = null
    private val binding get() = _binding!!
    private val atividadeViewModel: AtividadeViewModel by activityViewModels()
    private val checklistViewModel: ChecklistViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels() // ✅ Novo
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val requisicaoViewModel: RequisicaoViewModel by activityViewModels()
    private var atividadeId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
                    binding.btnEditar.visibility = View.GONE
                    binding.btnEditar.visibility = View.GONE
                }
                 Cargo.COORDENADOR -> {
                    binding.cardConfirmarAtividade.visibility = View.VISIBLE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                }
                Cargo.GESTOR -> {
                    binding.cardConfirmarAtividade.visibility = View.VISIBLE
                    binding.btnEditar.visibility = View.VISIBLE
                    binding.btnDeletar.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.GONE

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

        binding.btnDeletar.setOnClickListener {
            mostrarDialogoConfirmacao()
        }

        binding.btnAdicionarItemChecklist.setOnClickListener {
            val editText = EditText(requireContext()).apply {
                hint = "Digite o nome do item"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.LTGRAY)
                background = ContextCompat.getDrawable(context, R.drawable.bg_edittext_dialog)
                setPadding(32, 24, 32, 24)
            }

            val container = FrameLayout(requireContext()).apply {
                setPadding(48, 24, 48, 0)
                addView(editText)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Novo item do checklist")
                .setView(container)
                .setPositiveButton("Adicionar", null)
                .setNegativeButton("Cancelar", null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setTextColor(Color.parseColor("#3C82F6"))
                    setOnClickListener {
                        val texto = editText.text.toString().trim()
                        if (texto.isNotEmpty()) {
                            checklistViewModel.inserir(
                                ChecklistItemEntity(
                                    descricao = texto,
                                    atividadeId = atividadeId
                                )
                            )
                            dialog.dismiss()
                        } else {
                            editText.error = "Este campo não pode estar vazio"
                        }
                    }
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
            }

            dialog.show()
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

                        // ✅ Atualiza o status da ação após deletar a atividade
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
