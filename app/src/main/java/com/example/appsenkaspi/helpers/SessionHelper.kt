package com.example.appsenkaspi.helpers

import android.content.Context

fun getFuncionarioLogadoId(context: Context): Int {
    val prefs = context.getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    return prefs.getInt("funcionario_id", -1)
}

fun salvarFuncionarioLogado(context: Context, id: Int, nomeUsuario: String) {
    val prefs = context.getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putInt("funcionario_id", id)
        .putString("funcionario_nomeUsuario", nomeUsuario)
        .apply()
}

fun getFuncionarioNomeUsuario(context: Context): String? {
    val prefs = context.getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    return prefs.getString("funcionario_nomeUsuario", null)
}

fun limparFuncionarioLogado(context: Context) {
    val prefs = context.getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .remove("funcionario_id")
        .remove("funcionario_nomeUsuario")
        .apply()
}
