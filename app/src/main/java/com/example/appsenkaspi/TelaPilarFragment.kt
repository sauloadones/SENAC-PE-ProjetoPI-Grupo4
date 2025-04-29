package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.databinding.FragmentTelaPilarBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TelaPilarFragment : Fragment() {

    private var _binding: FragmentTelaPilarBinding? = null
    private val binding get() = _binding!!

    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel   by activityViewModels()

    private var pilarId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Recupera o ID
        pilarId = arguments?.getInt("pilarId") ?: -1
        if (pilarId == -1) {
            Toast.makeText(requireContext(), "Pilar inválido!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // 2) OBSERVA o LiveData do ViewModel
        pilarViewModel.getPilarById(pilarId)
            .observe(viewLifecycleOwner) { pilar ->
                if (pilar != null) {
                    preencherCamposComPilar(pilar)
                } else {
                    Toast.makeText(requireContext(), "Pilar não encontrado!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }

        // 3) Configura o resto normalmente
        configurarBotoes()
        binding.iconeMenu.setOnClickListener { toggleSobre() }
        observarAcoes()
    }




    private fun preencherCamposComPilar(pilar: PilarEntity) {
        // ordinal baseado no ID
        binding.tituloPilar.text       = "${pilar.id}° Pilar"
        // nome do pilar
        binding.subtituloPilar.apply {
            text       = pilar.nome.ifBlank { "Sem nome" }
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        // data de prazo
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dataPrazoPilar.text = "Prazo: ${sdf.format(pilar.dataPrazo)}"
        // descrição na seção “Sobre”
        binding.textoSobre.text    = pilar.descricao.ifBlank { "Nenhuma descrição adicionada." }
        // atualiza a barra de progresso (aqui só zera, troque pelo cálculo real)
        animarProgresso(0)
    }

    private fun configurarBotoes() {
        binding.cardEditarPilar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    EditarPilarFragment().apply {
                        arguments = Bundle().apply { putInt("pilarId", pilarId) }
                    }
                )
                .addToBackStack(null)
                .commit()
        }
        binding.cardAdicionarAcoes.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    CriarAcaoFragment().apply {
                        arguments = Bundle().apply { putInt("pilarId", pilarId) }
                    }
                )
                .addToBackStack(null)
                .commit()
        }
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
            duration = 500
            start()
        }
        binding.percentual.text = "$target%"
    }

    private fun observarAcoes() {
        val container = binding.atividadesContainer
        val emptyView = binding.emptyStateView

        // LiveData<List<AcaoComStatus>>
        acaoViewModel.listarPorPilar(pilarId)
            .observe(viewLifecycleOwner) { listaAcoes ->
                container.removeAllViews()
                if (listaAcoes.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    container.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    container.visibility = View.VISIBLE
                    listaAcoes.forEach { status ->
                        // infla o layout do item de ação
                        val item = layoutInflater.inflate(
                            R.layout.item_acao,
                            container,
                            false
                        )
                        val nomeTv   = item.findViewById<TextView>(R.id.textTituloAcao)
                        val statusTv = item.findViewById<TextView>(R.id.textFracaoAcao)
                        val progBar  = item.findViewById<ProgressBar>(R.id.progressoAcaoItem)
                        val setaIv   = item.findViewById<ImageView>(R.id.iconArrowAcao)

                        // popula dados
                        nomeTv.text = status.acao.nome
                        val concluidas = status.ativasConcluidas
                        val total      = status.totalAtividades
                        statusTv.text = "$concluidas/$total"

                        // anima barra
                        val pct = if (total > 0) concluidas * 100 / total else 0
                        progBar.max = 100
                        progBar.setProgress(pct, true)

                        // seta verde
                        setaIv.setColorFilter(Color.GREEN)

                        container.addView(item)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
