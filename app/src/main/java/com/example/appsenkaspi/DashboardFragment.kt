package com.example.appsenkaspi

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

  private var _binding: FragmentDashboardBinding? = null
  private val binding get() = _binding!!

  private val acaoViewModel: AcaoViewModel by viewModels()
  private val pilarViewModel: PilarViewModel by viewModels()

  // Usar activityViewModels para compartilhar com outros fragments
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  private val mapaNomesParaIds = mutableMapOf<String, Int?>()
  private var funcionarioLogadoId: Int = -1

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDashboardBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Observar funcionário logado para configurar notificação e badges
    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      funcionario?.let {
        funcionarioLogadoId = it.id

        configurarNotificacaoBadge(
          rootView = view,
          lifecycleOwner = viewLifecycleOwner,
          fragmentManager = parentFragmentManager,
          funcionarioId = it.id,
          cargo = it.cargo,
          viewModel = notificacaoViewModel
        )

        // Se quiser controlar algum botão pela permissão do cargo:
        // Exemplo (se tiver botão no layout):
        // binding.algumBotao.visibility = if (it.cargo == Cargo.COORDENADOR) View.VISIBLE else View.GONE
      }
    }

    configurarSpinner()
  }

  private fun configurarSpinner() {
    pilarViewModel.listarIdsENomes().observe(viewLifecycleOwner) { pilares ->
      val nomes = pilares.map { it.nome }
      val opcoes = listOf("Visão Geral") + nomes

      mapaNomesParaIds.clear()
      mapaNomesParaIds["Visão Geral"] = null
      pilares.forEach { mapaNomesParaIds[it.nome] = it.id }

      val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opcoes)
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      binding.spinnerFiltro.adapter = adapter
    }

    binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val nome = parent.getItemAtPosition(position).toString()
        val pilarId = mapaNomesParaIds[nome]
        carregarResumo(pilarId)
        carregarGraficoDeBarras(pilarId)
      }

      override fun onNothingSelected(parent: AdapterView<*>) {}
    }
  }

  private fun carregarResumo(pilarId: Int?) {
    acaoViewModel.gerarResumoDashboard(pilarId) { resumo ->
      binding.valorTotal.text = resumo.totalAcoes.toString()
      binding.valorConcluidas.text = resumo.atividadesConcluidas.toString()
      binding.valorAndamento.text = resumo.atividadesAndamento.toString()
      binding.valorAtraso.text = resumo.atividadesAtraso.toString()

      binding.labelDonut.text = if (pilarId == null)
        "Progressão geral dos pilares"
      else
        "Progressão do pilar"

      atualizarDonutChart(resumo.atividadesConcluidas, resumo.totalAtividades)
    }
  }

  private fun atualizarDonutChart(concluidas: Int, total: Int) {
    if (total == 0) {
      binding.labelDonut.text = "Sem atividades disponíveis"
      binding.donutChart.clear()
      return
    }

    val percentConcluido = (concluidas.toFloat() / total) * 100f
    val percentPendente = 100f - percentConcluido

    val entries = listOf(
      PieEntry(percentConcluido, "Concluído"),
      PieEntry(percentPendente, "Pendente")
    )

    val dataSet = PieDataSet(entries, "").apply {
      colors = listOf(Color.parseColor("#164773"), Color.parseColor("#3A3A3A"))
      setDrawValues(false)
    }

    val data = PieData(dataSet)

    with(binding.donutChart) {
      this.data = data
      description.isEnabled = false
      legend.isEnabled = false
      setHoleColor(Color.TRANSPARENT)
      setTransparentCircleAlpha(0)
      setUsePercentValues(true)
      setDrawEntryLabels(false)

      setDrawCenterText(true)
      centerText = "${percentConcluido.toInt()}%"
      setCenterTextSize(18f)
      setCenterTextColor(Color.WHITE)

      invalidate()
    }
  }

  private fun carregarGraficoDeBarras(pilarId: Int?) {
    if (pilarId == null) {
      carregarGraficoDeBarrasVisaoGeral()
    } else {
      carregarGraficoDeBarrasPorAcoes(pilarId)
    }
  }

  private fun carregarGraficoDeBarrasVisaoGeral() {
    lifecycleScope.launch {
      val pilares = withContext(Dispatchers.IO) {
        pilarViewModel.getPilaresParaDashboard()
      }

      val nomes = mutableListOf<String>()
      val entradas = mutableListOf<BarEntry>()

      pilares.forEachIndexed { index, pilar ->
        val progresso = withContext(Dispatchers.IO) {
          pilarViewModel.calcularProgressoInterno(pilar.id)
        }
        nomes.add(pilar.nome)
        entradas.add(BarEntry(index.toFloat(), progresso * 100f))
      }

      if (entradas.isEmpty()) {
        binding.barChart.clear()
        binding.barChart.setNoDataText("Nenhum dado disponível para os pilares.")
        return@launch
      }

      val dataSet = BarDataSet(entradas, "Progresso por Pilar (%)").apply {
        valueTextColor = Color.WHITE
        valueTextSize = 12f
        color = Color.parseColor("#164773")
      }

      val barData = BarData(dataSet).apply {
        barWidth = 0.5f
      }

      with(binding.barChart) {
        data = barData
        setFitBars(true)
        description.isEnabled = false
        legend.isEnabled = false
        setDrawValueAboveBar(true)
        setTouchEnabled(false)
        animateY(800)
        setScaleEnabled(false)

        axisLeft.apply {
          axisMinimum = 0f
          axisMaximum = 100f
          granularity = 10f
          textColor = Color.WHITE
          textSize = 12f
        }

        axisRight.isEnabled = false

        xAxis.apply {
          position = XAxis.XAxisPosition.BOTTOM
          valueFormatter = IndexAxisValueFormatter(nomes)
          granularity = 1f
          isGranularityEnabled = true
          setDrawGridLines(false)
          textColor = Color.WHITE
          textSize = 10f
          labelRotationAngle = 0f
          setLabelCount(nomes.size, false)
          setAvoidFirstLastClipping(true)
          yOffset = 2f
        }

        invalidate()
      }
    }
  }

  private fun carregarGraficoDeBarrasPorAcoes(pilarId: Int) {
    lifecycleScope.launch {
      val progressoAcoes = withContext(Dispatchers.IO) {
        AppDatabase.getDatabase(requireContext())
          .acaoDao()
          .listarProgressoPorPilar(pilarId)
      }

      if (progressoAcoes.isEmpty()) {
        binding.barChart.clear()
        binding.barChart.setNoDataText("Nenhuma ação encontrada para este pilar.")
        return@launch
      }

      val entries = progressoAcoes.mapIndexed { index, acao ->
        BarEntry(index.toFloat(), acao.progresso * 100f)
      }

      val labels = progressoAcoes.mapIndexed { index, _ ->
        "Ação ${index + 1}"
      }

      val dataSet = BarDataSet(entries, "Progresso (%)").apply {
        valueTextColor = Color.WHITE
        valueTextSize = 12f
        color = Color.parseColor("#164773")
      }

      val barData = BarData(dataSet).apply {
        barWidth = 0.5f
      }

      with(binding.barChart) {
        data = barData
        setFitBars(true)
        description.isEnabled = false
        legend.isEnabled = false
        setDrawValueAboveBar(true)
        setTouchEnabled(false)
        animateY(800)
        setScaleEnabled(false)

        axisLeft.apply {
          axisMinimum = 0f
          axisMaximum = 100f
          granularity = 10f
          textColor = Color.WHITE
          textSize = 12f
        }

        axisRight.isEnabled = false

        xAxis.apply {
          position = XAxis.XAxisPosition.BOTTOM
          valueFormatter = IndexAxisValueFormatter(labels)
          granularity = 1f
          isGranularityEnabled = true
          setDrawGridLines(false)
          textColor = Color.WHITE
          textSize = 10f
          labelRotationAngle = 0f
          setLabelCount(labels.size, false)
          setAvoidFirstLastClipping(true)
          yOffset = 2f
        }

        invalidate()
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
