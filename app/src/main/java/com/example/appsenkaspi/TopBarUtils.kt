package com.example.appsenkaspi

import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner


fun configurarBotaoSino(
    rootView: View,
    lifecycleOwner: LifecycleOwner,
    viewModel: FuncionarioViewModel
) {
    val botaoSino = rootView.findViewById<ImageView>(R.id.notificationIcon)

    botaoSino?.let { sino ->
        sino.visibility = View.VISIBLE

        viewModel.funcionarioLogado.observe(lifecycleOwner) { funcionario ->
            val cargo = funcionario?.cargo ?: Cargo.APOIO

            sino.setOnClickListener {
                val activity = rootView.context as? AppCompatActivity
                activity?.let {
                    abrirTelaNotificacoes(it, cargo)
                }
            }
        }
    }
}
