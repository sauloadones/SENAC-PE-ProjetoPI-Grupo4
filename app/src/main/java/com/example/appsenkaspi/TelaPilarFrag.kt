package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class TelaPilarFragment : Fragment() {

    private lateinit var iconeMenu: ImageView
    private lateinit var sobreWrapper: LinearLayout
    private lateinit var emptyStateView: TextView
    private lateinit var atividadesContainer: LinearLayout
    private lateinit var progressoPilar: ProgressBar
    private lateinit var percentual: TextView
    private lateinit var btnAdicionarAcoes: Button

    private var listaAcoes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_pilar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconeMenu = view.findViewById(R.id.iconeMenu)
        sobreWrapper = view.findViewById(R.id.sobreWrapper)
        emptyStateView = view.findViewById(R.id.emptyStateView)
        atividadesContainer = view.findViewById(R.id.atividadesContainer)
        progressoPilar = view.findViewById(R.id.progressoPilar)
        percentual = view.findViewById(R.id.percentual)
        btnAdicionarAcoes = view.findViewById(R.id.btnAdicionarAcoes)

        iconeMenu.setOnClickListener {
            sobreWrapper.visibility = if (sobreWrapper.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnAdicionarAcoes.setOnClickListener {
            adicionarNovaAcao()
        }

        atualizarUI()
    }

    private fun adicionarNovaAcao() {
        listaAcoes.add("Nova Ação ${listaAcoes.size + 1}")
        atualizarUI()
    }

    private fun atualizarUI() {
        if (listaAcoes.isEmpty()) {
            atividadesContainer.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
            progressoPilar.progress = 0
            percentual.text = "0%"
            progressoPilar.progressTintList = resources.getColorStateList(R.color.gray, null)
        } else {
            atividadesContainer.visibility = View.VISIBLE
            emptyStateView.visibility = View.GONE

            atividadesContainer.removeAllViews()

            listaAcoes.forEach { acao ->
                val itemView = layoutInflater.inflate(R.layout.item_acao, atividadesContainer, false)
                itemView.findViewById<TextView>(R.id.tituloAcao).text = acao
                atividadesContainer.addView(itemView)
            }

            // CORREÇÃO DO CÁLCULO DE PROGRESSO
            val maxAcoes = 10  // quantidade máxima teórica para 100%
            val progresso = (listaAcoes.size * 100 / maxAcoes).coerceAtMost(100)

            progressoPilar.progress = progresso
            percentual.text = "$progresso%"

            if (progresso > 0) {
                progressoPilar.progressTintList = resources.getColorStateList(R.color.green, null)
            }
        }
    }
}

