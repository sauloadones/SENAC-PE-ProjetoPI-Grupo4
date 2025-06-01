package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.graphics.Paint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.appsenkaspi.databinding.FragmentTelaPilarComSubpilaresBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class TelaPilarComSubpilaresFragment : Fragment() {

  private var _binding: FragmentTelaPilarComSubpilaresBinding? = null
  private val binding get() = _binding!!

  private val pilarViewModel: PilarViewModel by activityViewModels()
  private val subpilarViewModel: SubpilarViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  private var pilarId: Int = -1
  private lateinit var subpilarAdapter: TelaSubpilarAdapter
  private val progressoMap = mutableMapOf<Int, Float>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentTelaPilarComSubpilaresBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)



    pilarId = arguments?.getInt("pilarId") ?: -1
    if (pilarId == -1) {
      Toast.makeText(requireContext(), "Pilar inválido!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
      return
    }

    viewLifecycleOwner.lifecycleScope.launch {
      // Calcula o progresso real, considerando se tem ou não subpilares
      val progresso = pilarViewModel.calcularProgressoInterno(pilarId)

      // Atualiza o status no banco com base no progresso e na data
      pilarViewModel.atualizarStatusAutomaticamente(pilarId)

      // Anima a barra
      animarProgresso((progresso * 100).toInt())
    }

    configurarBotaoVoltar(view)
    configurarRecycler()
    configurarBotoes()

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

        when (it.cargo) {
          Cargo.APOIO, Cargo.GESTOR -> {
            binding.cardEditarPilar.visibility = View.GONE
            binding.cardAdicionarSubPilares.visibility = View.GONE
            binding.cardConcluirPilar.visibility = View.GONE
          }

          Cargo.COORDENADOR -> {
            binding.cardEditarPilar.visibility = View.VISIBLE
            binding.cardAdicionarSubPilares.visibility = View.VISIBLE
            binding.cardConcluirPilar.visibility = View.VISIBLE
          }

          else -> {
            binding.cardEditarPilar.visibility = View.GONE
            binding.cardAdicionarSubPilares.visibility = View.GONE
          }
        }
      }
    }

    pilarViewModel.getPilarById(pilarId).observe(viewLifecycleOwner) { pilar ->
      if (pilar != null) {
        preencherCamposComPilar(pilar)
      } else {
        Toast.makeText(requireContext(), "Pilar não encontrado!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
      }
    }
    binding.cardConcluirPilar.setOnClickListener {
      lifecycleScope.launch {
        pilarViewModel.concluirPilar(pilarId)

        // Recarrega os dados do pilar após atualizar o status
        pilarViewModel.getPilarById(pilarId).observe(viewLifecycleOwner) { pilar ->
          if (pilar != null) {
            preencherCamposComPilar(pilar)
            Toast.makeText(requireContext(), "Pilar concluído com sucesso!", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }

    subpilarViewModel.listarSubpilaresPorPilar(pilarId).observe(viewLifecycleOwner) { subpilares ->
      viewLifecycleOwner.lifecycleScope.launch {
        // Limpa mapa antigo
        progressoMap.clear()

        // Calcula todos os progressos paralelamente
        val progressoList = subpilares.map { subpilar ->
          async {
            val progresso = subpilarViewModel.calcularProgressoInterno(subpilar.id)
            subpilar.id to progresso
          }
        }.awaitAll()

        // Atualiza o mapa completo de uma vez
        progressoMap.putAll(progressoList.toMap())

        // Atualiza lista do adapter com os dados já prontos
        subpilarAdapter.submitList(subpilares.toList())
      }

      // Atualiza a barra de progresso do Pilar
      pilarViewModel.calcularProgressoDoPilar(pilarId) { progresso ->
        animarProgresso((progresso * 100).toInt())
      }
    }


    binding.iconeMenu.setOnClickListener { toggleSobre() }
  }

  private fun configurarBotoes() {
    binding.cardEditarPilar.setOnClickListener {
      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, EditarPilarFragment().apply {
          arguments = Bundle().apply { putInt("pilarId", pilarId) }
        })
        .addToBackStack(null)
        .commit()
    }

    binding.cardAdicionarSubPilares.setOnClickListener {
      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, CriarSubpilarFragment.newInstance(pilarId))
        .addToBackStack(null)
        .commit()
    }
  }

  private fun preencherCamposComPilar(pilar: PilarEntity) {
    binding.tituloPilar.text = "${pilar.id}° Pilar"
    binding.subtituloPilar.apply {
      text = pilar.nome.ifBlank { "Sem nome" }
      paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    binding.dataPrazoPilar.text = "Prazo: ${sdf.format(pilar.dataPrazo)}"
    binding.textoSobre.text = pilar.descricao.ifBlank { "Nenhuma descrição adicionada." }

    viewLifecycleOwner.lifecycleScope.launch {
      val progresso = withContext(Dispatchers.IO) {
        pilarViewModel.calcularProgressoInterno(pilar.id).also {
          pilarViewModel.atualizarStatusAutomaticamente(pilar.id)
        }
      }

      animarProgresso((progresso * 100).toInt()) // caso tenha animação de barra de progresso

      val pilarAtualizado = pilarViewModel.getPilarById(pilar.id).value ?: pilar

      if (pilarAtualizado.status == StatusPilar.CONCLUIDO) {
        binding.cardConcluirPilar.visibility = View.GONE
      } else {
        val dataVencimento = pilar.dataPrazo.toLocalDate()
        val podeConcluir = withContext(Dispatchers.IO) {
          pilarViewModel.podeConcluirPilar(pilar.id, dataVencimento)
        }

        binding.cardConcluirPilar.visibility = if (podeConcluir) View.VISIBLE else View.GONE
      }
    }
  }
  private fun Date.toLocalDate(): LocalDate {
    return this.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate()
  }

  private fun configurarRecycler() {
    binding.recyclerSubpilares.layoutManager = LinearLayoutManager(requireContext())
    subpilarAdapter = TelaSubpilarAdapter(
      onClick = { subpilar -> abrirTelaSubpilar(subpilar) },
      progressoMap = progressoMap
    )
    binding.recyclerSubpilares.adapter = subpilarAdapter
  }

  private fun abrirTelaSubpilar(subpilar: SubpilarEntity) {
    val frag = TelaSubpilarComAcoesFragment.newInstance(subpilar.id)
    parentFragmentManager.beginTransaction()
      .replace(R.id.main_container, frag)
      .addToBackStack(null)
      .commit()
  }

  private fun toggleSobre() {
    val transition = AutoTransition().apply { duration = 300 }
    TransitionManager.beginDelayedTransition(binding.cabecalhoCard, transition)
    if (binding.sobreWrapper.visibility == View.VISIBLE) {
      binding.sobreWrapper.visibility = View.GONE
      binding.iconeMenu.animate().rotation(0f).setDuration(300).start()
      binding.headerLayout.elevation = 8f
    } else {
      binding.sobreWrapper.visibility = View.VISIBLE
      binding.iconeMenu.animate().rotation(180f).setDuration(300).start()
      binding.headerLayout.elevation = 16f
    }
  }

  private fun animarProgresso(target: Int) {
    ObjectAnimator.ofInt(binding.progressoPilar, "progress", target).apply {
      duration = 60
      start()
    }
    binding.percentual.text = "$target%"
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    fun newInstance(pilarId: Int): TelaPilarComSubpilaresFragment {
      return TelaPilarComSubpilaresFragment().apply {
        arguments = Bundle().apply {
          putInt("pilarId", pilarId)
        }
      }
    }
  }
}
