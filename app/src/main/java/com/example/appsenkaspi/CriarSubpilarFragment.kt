package com.example.appsenkaspi

import com.example.appsenkaspi.databinding.FragmentCriarSubpilaresBinding


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CriarSubpilarFragment : Fragment() {

  private var _binding: FragmentCriarSubpilaresBinding? = null
  private val binding get() = _binding!!

  private val subpilarViewModel: SubpilarViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val pilarViewModel: PilarViewModel by activityViewModels()

  private var dataPrazoSelecionada: Date? = null
  private val calendario = Calendar.getInstance()

  private var pilarId: Int = -1

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentCriarSubpilaresBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    configurarBotaoVoltar(view)

    pilarId = arguments?.getInt("pilarId") ?: -1
    if (pilarId == -1) {
      Toast.makeText(requireContext(), "Pilar inválido!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
      return
    }

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      when (funcionario?.cargo) {
        Cargo.COORDENADOR -> binding.confirmarButtonWrapper.visibility = View.VISIBLE
        else -> binding.confirmarButtonWrapper.visibility = View.GONE
      }
    }

    binding.buttonPickDate.setOnClickListener { abrirDatePicker() }
    binding.confirmarButtonWrapper.setOnClickListener {
      lifecycleScope.launch {
        if (validarPrazoComPilar()) {
          confirmarCriacaoSubpilar()
        }
      }
    }
  }

  private fun abrirDatePicker() {
    DatePickerDialog(
      requireContext(),
      { _, year, month, day ->
        calendario.set(year, month, day)
        dataPrazoSelecionada = calendario.time
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.buttonPickDate.text = formato.format(dataPrazoSelecionada!!)
      },
      calendario.get(Calendar.YEAR),
      calendario.get(Calendar.MONTH),
      calendario.get(Calendar.DAY_OF_MONTH)
    ).show()
  }

  private suspend fun validarPrazoComPilar(): Boolean {
    if (dataPrazoSelecionada == null) {
      binding.buttonPickDate.error = "Escolha um prazo"
      return false
    }

    val dataLimite = pilarViewModel.getDataPrazoDoPilar(pilarId)

    if (dataLimite == null) {
      withContext(Dispatchers.Main) {
        Toast.makeText(requireContext(), "Erro ao buscar data do pilar.", Toast.LENGTH_SHORT).show()
      }
      return false
    }

    val selecionadaTruncada = truncarData(dataPrazoSelecionada!!)
    val limiteTruncado = truncarData(dataLimite)

    if (selecionadaTruncada.after(limiteTruncado)) {
      val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(limiteTruncado)
      withContext(Dispatchers.Main) {
        Toast.makeText(
          requireContext(),
          "A data do subpilar não pode ultrapassar o prazo do pilar ($dataFormatada).",
          Toast.LENGTH_LONG
        ).show()
      }
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

  private fun confirmarCriacaoSubpilar() {
    val nome = binding.inputNomeSubpilar.text.toString().trim()
    val descricao = binding.inputDescricao.text.toString().trim()
    val prazo = dataPrazoSelecionada

    if (nome.isEmpty()) {
      binding.inputNomeSubpilar.error = "Digite o nome do Subpilar"
      return
    }
    if (prazo == null) {
      binding.buttonPickDate.error = "Escolha um prazo"
      return
    }

    val prefs = requireContext().getSharedPreferences("funcionario_prefs", android.content.Context.MODE_PRIVATE)
    val funcionarioId = prefs.getInt("funcionario_id", -1)
    if (funcionarioId == -1) {
      Toast.makeText(context, "Erro: usuário não autenticado", Toast.LENGTH_LONG).show()
      return
    }

    lifecycleScope.launch {
      val novoId = subpilarViewModel.inserirRetornandoId(
        SubpilarEntity(
          nome = nome,
          descricao = descricao,
          dataInicio = Date(),
          dataPrazo = prazo,
          pilarId = pilarId,
          criadoPor = funcionarioId,
          dataCriacao = Date(),
          status = StatusSubPilar.PLANEJADO
        )
      )

      parentFragmentManager.beginTransaction()
        .replace(
          R.id.main_container,
          TelaSubpilarComAcoesFragment.newInstance(novoId.toInt())
        )
        .addToBackStack(null)
        .commit()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    fun newInstance(pilarId: Int): CriarSubpilarFragment {
      return CriarSubpilarFragment().apply {
        arguments = Bundle().apply {
          putInt("pilarId", pilarId)
        }
      }
    }
  }
}
