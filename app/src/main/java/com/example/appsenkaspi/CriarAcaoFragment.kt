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
import com.example.appsenkaspi.databinding.FragmentCriarAcaoBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.activityViewModels

class CriarAcaoFragment : Fragment() {

  private var _binding: FragmentCriarAcaoBinding? = null
  private val binding get() = _binding!!

  private val acaoViewModel: AcaoViewModel by activityViewModels()
  private val acaoFuncionarioViewModel: AcaoFuncionarioViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val requisicaoViewModel: NotificacaoViewModel by activityViewModels()

  private var dataPrazoSelecionada: Date? = null
  private val calendario = Calendar.getInstance()
  private var subpilarId: Int? = null
  private var pilarId: Int? = null
  private val funcionariosSelecionados = mutableListOf<FuncionarioEntity>()
  private lateinit var adapterSelecionados: FuncionarioSelecionadoAdapter
  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

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

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      funcionario?.let {
        configurarNotificacaoBadge(
          rootView = view,
          lifecycleOwner = viewLifecycleOwner,
          fragmentManager = parentFragmentManager,
          funcionarioId = it.id,
          cargo = it.cargo,
          viewModel = notificacaoViewModel
        )
      }
    }

    // Recuperar IDs passados por argumento
    pilarId = arguments?.getInt("pilarId")?.takeIf { it > 0 }
    subpilarId = arguments?.getInt("subpilarId")?.takeIf { it > 0 }

    if (pilarId == null && subpilarId == null) {
      Toast.makeText(requireContext(), "Erro: Pilar ou Subpilar não informado!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
      return
    }

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      when (funcionario?.cargo) {
        Cargo.APOIO -> {
          binding.buttonConfirmacaoAcao.visibility = View.GONE
          binding.buttonPedirConfirmacaoAcao.visibility = View.VISIBLE
        }
        Cargo.COORDENADOR -> {
          binding.buttonConfirmacaoAcao.visibility = View.VISIBLE
          binding.buttonPedirConfirmacaoAcao.visibility = View.GONE
        }
        else -> {
          binding.buttonConfirmacaoAcao.visibility = View.GONE
          binding.buttonPedirConfirmacaoAcao.visibility = View.GONE
        }
      }
    }

    adapterSelecionados = FuncionarioSelecionadoAdapter(funcionariosSelecionados)
    binding.recyclerViewFuncionariosSelecionados.layoutManager =
      LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    binding.recyclerViewFuncionariosSelecionados.adapter = adapterSelecionados

    binding.buttonPickDateAcao.setOnClickListener { abrirDatePicker() }
    binding.iconSelecionarFuncionario.setOnClickListener {
      SelecionarResponsavelDialogFragment().show(childFragmentManager, "SelecionarFuncionariosDialog")
    }

    binding.buttonPedirConfirmacaoAcao.setOnClickListener {
      val nome = binding.inputNomeAcao.text.toString().trim()
      val descricao = binding.inputDescricaoAcao.text.toString().trim()
      val funcionarioLogado = funcionarioViewModel.funcionarioLogado.value

      if (nome.isEmpty() || dataPrazoSelecionada == null || funcionariosSelecionados.isEmpty() || funcionarioLogado == null) {
        Toast.makeText(requireContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      viewLifecycleOwner.lifecycleScope.launch {
        if (!validarPrazoAcao()) return@launch
        val nomeEstrutura = when {
          subpilarId != null -> acaoViewModel.buscarNomeSubpilarPorId(subpilarId!!) ?: "Subpilar"
          pilarId != null -> acaoViewModel.buscarPilarPorId(pilarId!!)?.nome ?: "Pilar"
          else -> "Estrutura não identificada"
        }

        val acaoJson = AcaoJson(
          nome = nome,
          descricao = descricao,
          dataPrazo = dataPrazoSelecionada!!,
          dataInicio = Date(),
          criadoPor = funcionarioLogado.id,
          status = StatusAcao.PLANEJADA,
          dataCriacao = Date(),
          pilarId = if (subpilarId == null) pilarId else null,
          subpilarId = subpilarId,
          nomePilar = nomeEstrutura,
          responsaveis = funcionariosSelecionados.map { it.id }
        )

        val json = Gson().toJson(acaoJson)

        val requisicao = RequisicaoEntity(
          tipo = TipoRequisicao.CRIAR_ACAO,
          acaoJson = json,
          status = StatusRequisicao.PENDENTE,
          solicitanteId = funcionarioLogado.id,
          dataSolicitacao = Date()
        )

        AppDatabase.getDatabase(requireContext()).requisicaoDao().inserir(requisicao)

        Toast.makeText(requireContext(), "Requisição de ação enviada para aprovação!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
      }
    }

    binding.buttonConfirmacaoAcao.setOnClickListener {
      viewLifecycleOwner.lifecycleScope.launch {
        if (!validarPrazoAcao()) return@launch
        confirmarCriacaoAcao()
      }
    }


    childFragmentManager.setFragmentResultListener("funcionariosSelecionados", viewLifecycleOwner) { _, bundle ->
      val lista = bundle.getParcelableArrayList<FuncionarioEntity>("listaFuncionarios") ?: arrayListOf()
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
      Toast.makeText(requireContext(), "Selecione pelo menos um funcionário!", Toast.LENGTH_SHORT).show()
      return
    }

    viewLifecycleOwner.lifecycleScope.launch {
      try {
        val idNovaAcao = acaoViewModel.criarAcaoSegura(
          AcaoEntity(
            nome = nome,
            descricao = descricao,
            dataInicio = Date(),
            dataPrazo = dataPrazoSelecionada!!,
            pilarId = if (subpilarId == null) pilarId else null,
            subpilarId = subpilarId,
            status = StatusAcao.PLANEJADA,
            criadoPor = funcionarioLogado.id,
            dataCriacao = Date()
          )
        )

        funcionariosSelecionados.forEach { func ->
          acaoFuncionarioViewModel.inserir(
            AcaoFuncionarioEntity(
              acaoId = idNovaAcao,
              funcionarioId = func.id
            )
          )
        }

        Toast.makeText(requireContext(), "Ação criada com sucesso!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
      } catch (e: IllegalArgumentException) {
        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
      }
    }
  }
  private suspend fun validarPrazoAcao(): Boolean {
    if (dataPrazoSelecionada == null) {
      binding.buttonPickDateAcao.error = "Selecione uma data de prazo"
      return false
    }

    val dataLimite: Date? = when {
      subpilarId != null -> acaoViewModel.buscarSubpilarPorId(subpilarId!!)?.dataPrazo
      pilarId != null -> acaoViewModel.buscarPilarPorId(pilarId!!)?.dataPrazo
      else -> null
    }

    dataLimite?.let {
      val selecionadaTruncada = truncarData(dataPrazoSelecionada!!)
      val limiteTruncado = truncarData(it)

      if (selecionadaTruncada.after(limiteTruncado)) {
        val nomeEstrutura = if (subpilarId != null) "subpilar" else "pilar"
        val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(limiteTruncado)
        Toast.makeText(
          requireContext(),
          "A data da ação não pode ultrapassar o prazo do $nomeEstrutura ($dataFormatada).",
          Toast.LENGTH_LONG
        ).show()
        return false
      }
    }

    return true
  }

  private fun truncarData(data: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = data
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
  }



  companion object {
    fun newInstance(pilarId: Int? = null, subpilarId: Int? = null): CriarAcaoFragment {
      return CriarAcaoFragment().apply {
        arguments = Bundle().apply {
          pilarId?.let { putInt("pilarId", it) }
          subpilarId?.let { putInt("subpilarId", it) }
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
