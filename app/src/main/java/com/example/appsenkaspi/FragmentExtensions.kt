package com.example.appsenkaspi

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.appsenkaspi.R

fun Fragment.configurarBotaoVoltar(view: View) {
    val botao = view.findViewById<ImageView>(R.id.IconVoltar)
    botao?.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}
fun configurarBotaoSino(view: View, fragmentManager: FragmentManager) {
    val sino = view.findViewById<ImageView>(R.id.notificationIcon)
    sino?.setOnClickListener {
        fragmentManager.beginTransaction()
            .replace(R.id.main_container, NotificacaoFragment())
            .addToBackStack(null)
            .commit()
    }
}


