package com.example.appsenkaspi.ui.atividade

import android.app.DatePickerDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appsenkaspi.data.local.enums.Cargo
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.viewmodel.FuncionarioViewModel
import com.example.appsenkaspi.viewmodel.NotificacaoViewModel
import com.example.appsenkaspi.data.local.enums.PrioridadeAtividade
import com.example.appsenkaspi.R
import com.example.appsenkaspi.data.local.entity.RequisicaoEntity
import com.example.appsenkaspi.ui.dialog.SelecionarResponsavelDialogFragment
import com.example.appsenkaspi.data.local.enums.StatusAtividade
import com.example.appsenkaspi.data.local.enums.StatusRequisicao
import com.example.appsenkaspi.data.local.enums.TipoRequisicao
import com.example.appsenkaspi.extensions.configurarBotaoVoltar
import com.example.appsenkaspi.extensions.configurarNotificacaoBadge
import com.example.appsenkaspi.data.local.database.AppDatabase
import com.example.appsenkaspi.data.local.entity.AtividadeEntity
import com.example.appsenkaspi.data.local.entity.AtividadeFuncionarioEntity
import com.example.appsenkaspi.databinding.FragmentCriarAtividadeBinding
import com.example.appsenkaspi.ui.dialog.EscolherPrioridadeDialogFragment
import com.example.appsenkaspi.viewmodel.AcaoViewModel
import com.example.appsenkaspi.viewmodel.AtividadeViewModel
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CriarAtividadeFragment : Fragment() {

  private var _binding: FragmentCriarAtividadeBinding? = null
  private val binding get() = _binding!!
  private var dataPrazoPilar: Date? = null
  private var dataPrazoAcao: Date? = null

  private val atividadeViewModel: AtividadeViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val acaoViewModel: AcaoViewModel by activityViewModels()

  private var dataInicio: Date? = null
  private var dataFim: Date? = null
  private var prioridadeSelecionada: PrioridadeAtividade? = null
  private val funcionariosSelecionados = mutableListOf<FuncionarioEntity>()
  private var acaoId: Int = -1

  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentCriarAtividadeBinding.inflate(inflater, container, false)
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

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      when (funcionario?.cargo) {
        Cargo.APOIO -> {
          binding.botaoConfirmarAtividade.visibility = View.GONE
          binding.botaoPedirConfirmacaoAtividade.visibility = View.VISIBLE
        }
        Cargo.COORDENADOR -> {
          binding.botaoConfirmarAtividade.visibility = View.VISIBLE
          binding.botaoPedirConfirmacaoAtividade.visibility = View.GONE
        }
        else -> {
          binding.botaoConfirmarAtividade.visibility = View.GONE
          binding.botaoPedirConfirmacaoAtividade.visibility = View.GONE
        }
      }
    }

    acaoId = arguments?.getInt("acaoId") ?: -1
    if (acaoId == -1) {
      Toast.makeText(requireContext(), "Erro: ação inválida!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
      return
    }
    viewLifecycleOwner.lifecycleScope.launch {
      val acao = withContext(Dispatchers.IO) {
          acaoViewModel.buscarAcaoPorId(acaoId)
      }
      dataPrazoAcao = acao?.dataPrazo
      Log.d("CriarAtividade", "Data prazo da ação carregada: $dataPrazoAcao")
    }


    parentFragmentManager.setFragmentResultListener("funcionariosSelecionados", viewLifecycleOwner) { _, bundle ->
      val selecionados = bundle.getParcelableArrayList<FuncionarioEntity>("listaFuncionarios") ?: return@setFragmentResultListener
      funcionariosSelecionados.clear()
      funcionariosSelecionados.addAll(selecionados)
      exibirFotosSelecionadas(funcionariosSelecionados)

      val nomes = selecionados.joinToString { it.nomeCompleto.split(" ")[0] }
      binding.textResponsaveis.text = nomes
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
      if (!validarDatasComAcao()) return@setOnClickListener
      confirmarCriacaoAtividade()
    }

    binding.botaoPedirConfirmacaoAtividade.setOnClickListener {
      val nome = binding.inputNomeAtividade.text.toString().trim()
      val descricao = binding.inputDescricao.text.toString().trim()
      val funcionarioCriador = funcionarioViewModel.funcionarioLogado.value
      if (!validarDatasComAcao()) return@setOnClickListener

      when {
        nome.isEmpty() -> {
          binding.inputNomeAtividade.error = "Nome obrigatório"
          return@setOnClickListener
        }
        dataInicio == null || dataFim == null -> {
          Toast.makeText(requireContext(), "Preencha as datas", Toast.LENGTH_SHORT).show()
          return@setOnClickListener
        }
        prioridadeSelecionada == null -> {
          Toast.makeText(requireContext(), "Selecione uma prioridade", Toast.LENGTH_SHORT).show()
          return@setOnClickListener
        }
        funcionariosSelecionados.isEmpty() -> {
          Toast.makeText(requireContext(), "Selecione ao menos um responsável", Toast.LENGTH_SHORT).show()
          return@setOnClickListener
        }
        funcionarioCriador == null -> {
          Toast.makeText(requireContext(), "Erro de autenticação!", Toast.LENGTH_SHORT).show()
          return@setOnClickListener
        }
      }

      lifecycleScope.launch(Dispatchers.IO) {
        val acao = acaoViewModel.buscarAcaoPorId(acaoId)
        val nomePilar = acao?.pilarId?.let {
          AppDatabase.Companion.getDatabase(requireContext()).pilarDao().getNomePilarPorId(it)
        } ?: "Pilar não identificado"

        val atividadeJson = AtividadeJson(
          nome = nome,
          descricao = descricao,
          dataInicio = dataInicio!!,
          dataPrazo = dataFim!!,
          status = StatusAtividade.PENDENTE,
          prioridade = prioridadeSelecionada!!,
          criadoPor = funcionarioCriador.id,
          acaoId = acaoId,
          nomePilar = nomePilar,
          dataCriacao = Date(),
          responsaveis = funcionariosSelecionados.map { it.id }
        )

        val json = Gson().toJson(atividadeJson)

        val requisicao = RequisicaoEntity(
            tipo = TipoRequisicao.CRIAR_ATIVIDADE,
            atividadeJson = json,
            status = StatusRequisicao.PENDENTE,
            solicitanteId = funcionarioCriador.id,
            dataSolicitacao = Date()
        )

        AppDatabase.Companion.getDatabase(requireContext()).requisicaoDao().inserir(requisicao)

        launch(Dispatchers.Main) {
          Toast.makeText(requireContext(), "Requisição de criação enviada para aprovação!", Toast.LENGTH_SHORT).show()
          parentFragmentManager.popBackStack()
        }
      }
    }

    setFragmentResultListener("prioridadeSelecionada") { _, bundle ->
      val valor = bundle.getString("valor")
      prioridadeSelecionada = PrioridadeAtividade.values().find { it.name == valor }

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
  }

  private fun abrirDatePicker(isInicio: Boolean) {
    val calendario = Calendar.getInstance()
    DatePickerDialog(
        requireContext(),
        { _, year, month, day ->
            calendario.set(year, month, day)
            val date = calendario.time
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            if (isInicio) {
                dataInicio = date
                binding.textDataInicio.text = "Data de início: ${format.format(date)}"
            } else {
                dataFim = date
                binding.textDataFim.text = "Data de término: ${format.format(date)}"
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
      val imageView = CircleImageView(requireContext()).apply {
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

  private fun confirmarCriacaoAtividade() {
    val nome = binding.inputNomeAtividade.text.toString().trim()
    val descricao = binding.inputDescricao.text.toString().trim()
    val funcionarioCriador = funcionarioViewModel.funcionarioLogado.value

    when {
      nome.isEmpty() -> {
        binding.inputNomeAtividade.error = "Nome obrigatório"
        return
      }
      dataInicio == null || dataFim == null -> {
        Toast.makeText(requireContext(), "Preencha as datas", Toast.LENGTH_SHORT).show()
        return
      }
      prioridadeSelecionada == null -> {
        Toast.makeText(requireContext(), "Selecione uma prioridade", Toast.LENGTH_SHORT).show()
        return
      }
      funcionariosSelecionados.isEmpty() -> {
        Toast.makeText(requireContext(), "Selecione ao menos um responsável", Toast.LENGTH_SHORT).show()
        return
      }
      funcionarioCriador == null -> {
        Toast.makeText(requireContext(), "Erro de autenticação!", Toast.LENGTH_SHORT).show()
        return
      }
    }

    val novaAtividade = AtividadeEntity(
        nome = nome,
        descricao = descricao,
        dataInicio = dataInicio!!,
        dataPrazo = dataFim!!,
        acaoId = acaoId,
        funcionarioId = funcionariosSelecionados.first().id,
        status = calcularStatusInicial(dataFim!!),
        prioridade = prioridadeSelecionada!!,
        criadoPor = funcionarioCriador.id,
        dataCriacao = Date()
    )

    viewLifecycleOwner.lifecycleScope.launch {
      val dao = AppDatabase.Companion.getDatabase(requireContext()).atividadeDao()
      val idAtividade = dao.inserirComRetorno(novaAtividade).toInt()
      novaAtividade.id = idAtividade

      funcionariosSelecionados.forEach { funcionario ->
        val relacao = AtividadeFuncionarioEntity(
            atividadeId = idAtividade,
            funcionarioId = funcionario.id
        )
        atividadeViewModel.inserirRelacaoFuncionario(relacao)
      }

      acaoViewModel.atualizarStatusAcaoAutomaticamente(acaoId)
      atividadeViewModel.checarPrazos()
      atividadeViewModel.checarAtividadesVencidas()

      Toast.makeText(requireContext(), "Atividade criada com sucesso!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
    }
  }

  private fun validarDatasComAcao(): Boolean {
    if (dataInicio == null || dataFim == null || dataPrazoAcao == null) {
      Toast.makeText(requireContext(), "Erro ao validar datas: valores nulos.", Toast.LENGTH_SHORT).show()
      return false
    }

    val inicio = truncarData(dataInicio!!)
    val fim = truncarData(dataFim!!)
    val prazoAcao = truncarData(dataPrazoAcao!!)

    if (inicio.after(prazoAcao)) {
      Toast.makeText(requireContext(), "A data de início deve ser antes do prazo da ação.", Toast.LENGTH_SHORT).show()
      return false
    }

    if (fim.before(inicio)) {
      Toast.makeText(requireContext(), "A data de término deve ser igual ou depois da data de início.", Toast.LENGTH_SHORT).show()
      return false
    }

    if (fim.after(prazoAcao)) {
      Toast.makeText(requireContext(), "A data de término deve ser no máximo até o prazo da ação.", Toast.LENGTH_SHORT).show()
      return false
    }

    return true
  }

  private fun truncarData(data: Date): Date {
    return Calendar.getInstance().apply {
      time = data
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.time
  }

  private fun calcularStatusInicial(dataPrazo: Date): StatusAtividade {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val hojeStr = formato.format(Date())
    val prazoStr = formato.format(dataPrazo)

    return if (prazoStr <= hojeStr) StatusAtividade.VENCIDA else StatusAtividade.PENDENTE
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
