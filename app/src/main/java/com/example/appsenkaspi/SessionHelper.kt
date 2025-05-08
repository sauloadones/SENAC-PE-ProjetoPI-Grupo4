package com.example.appsenkaspi

import android.content.Context

fun getFuncionarioLogadoId(context: Context): Int {
    val prefs = context.getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    return prefs.getInt("funcionario_id", -1)
}
