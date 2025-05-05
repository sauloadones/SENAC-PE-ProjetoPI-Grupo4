package com.example.appsenkaspi

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun abrirTelaNotificacoes(activity: AppCompatActivity, cargo: Cargo) {
    val fragment: Fragment = when (cargo) {
        Cargo.COORDENADOR -> TelaNotificacoesCoordenadorFragment()
        Cargo.APOIO -> TelaNotificacoesApoioFragment()
        else -> TelaNotificacoesApoioFragment()
    }

    activity.supportFragmentManager.beginTransaction()
        .replace(R.id.main_container, fragment)
        .addToBackStack(null)
        .commit()
}


