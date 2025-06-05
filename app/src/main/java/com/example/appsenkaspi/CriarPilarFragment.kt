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
import com.example.appsenkaspi.databinding.FragmentCriarPilarBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.activityViewModels

class CriarPilarFragment : Fragment() {

  private var _binding: FragmentCriarPilarBinding? = null
  private val binding get() = _binding!!

  private val pilarViewModel: PilarViewModel by activityViewModels()
  private val subpilarViewModel: SubpilarViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

  private var dataPrazoSelecionada: Date? = null
  private val calendario = Calendar.getInstance()

  private val listaSubpilares = mutableListOf<SubpilarTemp>()
  private lateinit var subpilarAdapter: SubpilarAdapter

  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentCriarPilarBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

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
    configurarBotaoVoltar(view)

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      when (funcionario?.cargo) {
        Cargo.APOIO -> {
          binding.confirmarButtonWrapper.visibility = View.GONE
          binding.pedirConfirmarButtonWrapper.visibility = View.VISIBLE
        }
        Cargo.COORDENADOR -> {
          binding.confirmarButtonWrapper.visibility = View.VISIBLE
          binding.pedirConfirmarButtonWrapper.visibility = View.GONE
        }
        else -> {
          binding.confirmarButtonWrapper.visibility = View.GONE
          binding.pedirConfirmarButtonWrapper.visibility = View.GONE
        }
      }
    }

    subpilarAdapter = SubpilarAdapter(listaSubpilares)
    binding.recyclerViewSubpilares.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = subpilarAdapter
    }

    binding.buttonPickDate.setOnClickListener { abrirDatePicker() }
    binding.buttonAddSubpilar.setOnClickListener { abrirDialogAdicionarSubpilar() }
    binding.confirmarButtonWrapper.setOnClickListener { confirmarCriacaoPilar() }

    childFragmentManager.setFragmentResultListener("novoSubpilar", viewLifecycleOwner) { _, bundle ->
      val nome = bundle.getString("nomeSubpilar")
      val descricao = bundle.getString("descricaoSubpilar")
      val prazo = bundle.getSerializable("prazoSubpilar") as? Date

      if (nome != null && prazo != null) {
        listaSubpilares.add(SubpilarTemp(nome, descricao, prazo))
        subpilarAdapter.notifyItemInserted(listaSubpilares.size - 1)
      }
    }
  }

  private fun abrirDatePicker() {
    DatePickerDialog(
      requireContext(),
      { _, year, month, day ->
        calendario.set(year, month, day)
        dataPrazoSelecionada = calendario.time
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.buttonPickDate.text = fmt.format(dataPrazoSelecionada!!)
      },
      calendario.get(Calendar.YEAR),
      calendario.get(Calendar.MONTH),
      calendario.get(Calendar.DAY_OF_MONTH)
    ).show()
  }

  private fun abrirDialogAdicionarSubpilar() {
    dataPrazoSelecionada?.let { prazo ->
      AdicionarSubpilarDialogFragment.newInstance(-1, prazo)
        .show(childFragmentManager, "AdicionarSubpilarDialog")
    } ?: run {
      binding.buttonPickDate.error = "Escolha primeiro um prazo"
    }
  }

  private fun confirmarCriacaoPilar() {
    val nome = binding.inputNomePilar.text.toString().trim()
    val descricao = binding.inputDescricao.text.toString().trim()
    val prazo = dataPrazoSelecionada

    if (nome.isEmpty()) {
      binding.inputNomePilar.error = "Digite o nome do Pilar"
      return
    }
    if (prazo == null) {
      binding.buttonPickDate.error = "Escolha um prazo"
      return
    }

    val prefs = requireContext().getSharedPreferences(
      "funcionario_prefs",
      android.content.Context.MODE_PRIVATE
    )
    val funcionarioId = prefs.getInt("funcionario_id", -1)
    if (funcionarioId == -1) {
      Toast.makeText(context, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
      return
    }

    viewLifecycleOwner.lifecycleScope.launch {
      val idLong = pilarViewModel.inserirRetornandoId(
        PilarEntity(
          nome = nome,
          descricao = descricao,
          dataInicio = Date(),
          dataPrazo = prazo,
          status = StatusPilar.PLANEJADO,
          dataCriacao = Date(),
          criadoPor = funcionarioId,
          dataConclusao =  null,
          dataExcluido = null,


        )
      )
      val novoId = idLong.toInt()

      listaSubpilares.forEach { sub ->
        subpilarViewModel.inserir(
          SubpilarEntity(
            nome = sub.nome,
            descricao = sub.descricao,
            dataInicio = Date(),
            dataPrazo = sub.prazo,
            pilarId = novoId,
            criadoPor = funcionarioId,
            dataCriacao = Date(),
            status = StatusSubPilar.PLANEJADO
          )
        )
      }

      val possuiSubpilares = listaSubpilares.isNotEmpty()

      val destino = if (possuiSubpilares) {
        TelaPilarComSubpilaresFragment.newInstance(novoId)
      } else {
        TelaPilarFragment().apply {
          arguments = Bundle().apply { putInt("pilarId", novoId) }
        }
      }

      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, destino)
        .addToBackStack(null)
        .commit()

    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  data class SubpilarTemp(
    val nome: String,
    val descricao: String?,
    val prazo: Date
  )
}
