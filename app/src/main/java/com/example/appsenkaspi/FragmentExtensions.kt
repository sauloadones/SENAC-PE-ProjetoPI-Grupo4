package com.example.appsenkaspi.utils

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.appsenkaspi.R

fun Fragment.configurarBotaoVoltar(view: View) {
    val botao = view.findViewById<ImageView>(R.id.IconVoltar)
    botao?.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}
