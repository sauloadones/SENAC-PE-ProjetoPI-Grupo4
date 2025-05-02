package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.Converters.StatusAcao
import com.example.appsenkaspi.databinding.FragmentCriarAcaoBinding
import com.example.appsenkaspi.utils.configurarBotaoVoltar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CriarAcaoFragment : Fragment() {

    private var _binding: FragmentCriarAcaoBinding? = null
    private val binding get() = _binding!!

    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val acaoFuncionarioViewModel: AcaoFuncionarioViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

    private var dataPrazoSelecionada: Date? = null
    private val calendario = Calendar.getInstance()

    private var pilarId: Int = -1
    private val funcionariosSelecionados = mutableListOf<FuncionarioEntity>()
    private lateinit var adapterSelecionados: FuncionarioSelecionadoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarAcaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)

        // configura RecyclerView
        adapterSelecionados = FuncionarioSelecionadoAdapter(funcionariosSelecionados)
        binding.recyclerViewFuncionariosSelecionados.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewFuncionariosSelecionados.adapter = adapterSelecionados

        // listeners de clique
        binding.buttonPickDateAcao.setOnClickListener { abrirDatePicker() }
        binding.iconSelecionarFuncionario
            .setOnClickListener {
                SelecionarFuncionariosDialogFragment()
                    .show(childFragmentManager, "SelecionarFuncionariosDialog")
            }
        binding.buttonConfirmacaoAcao.setOnClickListener { confirmarCriacaoAcao() }

        // obtém o pilarId passado nas args
        pilarId = arguments?.getInt("pilarId") ?: -1
        if (pilarId == -1) {
            Toast.makeText(requireContext(), "Erro: Pilar inválido!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack() // volta uma tela (semântica)
            return
        }

        // **OUVINTE** para o resultado de seleção de funcionários
        childFragmentManager.setFragmentResultListener(
            "funcionariosSelecionados",
            viewLifecycleOwner
        ) { _, bundle ->
            val lista = bundle.getParcelableArrayList<FuncionarioEntity>(
                "listaFuncionarios"
            ) ?: arrayListOf()
            funcionariosSelecionados.clear()
            funcionariosSelecionados.addAll(lista.filterNotNull())
            adapterSelecionados.notifyDataSetChanged()
        }
    }

    private fun abrirDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, ano, mes, dia ->
                calendario.set(ano, mes, dia)
                dataPrazoSelecionada = calendario.time
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.buttonPickDateAcao.text = fmt.format(dataPrazoSelecionada!!)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun abrirDialogSelecionarFuncionarios() {
        // **AGORA** usa childFragmentManager
        SelecionarFuncionariosDialogFragment()
            .show(childFragmentManager, "SelecionarFuncionariosDialog")
    }

    private fun confirmarCriacaoAcao() {
        val nome = binding.inputNomeAcao.text.toString().trim()
        val descricao = binding.inputDescricaoAcao.text.toString().trim()
        val funcionarioLogado = funcionarioViewModel.funcionarioLogado.value
        if (funcionarioLogado == null) {
            Toast.makeText(context, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }


        if (nome.isEmpty()) {
            binding.inputNomeAcao.error = "Digite o nome da ação"
            return
        }
        if (dataPrazoSelecionada == null) {
            binding.buttonPickDateAcao.error = "Selecione uma data de prazo"
            return
        }
        if (funcionariosSelecionados.isEmpty()) {
            Toast.makeText(requireContext(),
                "Selecione pelo menos um funcionário!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // insere a ação e obtém o ID
            val idNovaAcao = acaoViewModel.inserirRetornandoId(
                AcaoEntity(
                    nome       = nome,
                    descricao  = descricao,
                    dataInicio = Date(),
                    dataPrazo  = dataPrazoSelecionada!!,
                    pilarId    = pilarId,
                    status      = StatusAcao.PLANEJADA,
                    criadoPor   = funcionarioLogado.id, // <- pegue o ID real do usuário logado
                    dataCriacao = Date()

                )
            )
            // relaciona cada funcionário à ação
            funcionariosSelecionados.forEach { func ->
                acaoFuncionarioViewModel.inserir(
                    AcaoFuncionarioEntity(
                        acaoId        = idNovaAcao,
                        funcionarioId = func.id
                    )
                )
            }
            Toast.makeText(requireContext(),
                "Ação criada com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()  // volta à TelaPilar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
