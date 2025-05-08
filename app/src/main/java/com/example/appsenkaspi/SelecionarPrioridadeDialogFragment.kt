package com.example.appsenkaspi

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class EscolherPrioridadeDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_prioridade, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fechar = view.findViewById<ImageView>(R.id.fecharDialogPrioridade)
        val opcaoAlta = view.findViewById<View>(R.id.cardPrioridadeAlta)
        val opcaoMedia = view.findViewById<View>(R.id.cardPrioridadeMedia)
        val opcaoBaixa = view.findViewById<View>(R.id.cardPrioridadeBaixa)

        fechar.setOnClickListener {
            dismiss()
        }

        val selecionarPrioridade = { prioridade: PrioridadeAtividade ->
            parentFragmentManager.setFragmentResult(
                "prioridadeSelecionada",
                Bundle().apply {
                    putString("valor", prioridade.name)
                }
            )
            dismiss()
        }

        opcaoAlta.setOnClickListener { selecionarPrioridade(PrioridadeAtividade.ALTA) }
        opcaoMedia.setOnClickListener { selecionarPrioridade(PrioridadeAtividade.MEDIA) }
        opcaoBaixa.setOnClickListener { selecionarPrioridade(PrioridadeAtividade.BAIXA) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
