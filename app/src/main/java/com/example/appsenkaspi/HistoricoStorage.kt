package com.example.appsenkaspi

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HistoricoStorage {

    private const val PREFS_NAME = "historico_prefs"

    fun salvar(context: Context, historico: List<HistoricoRelatorio>, usuario: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(historico)
        editor.putString("historico_$usuario", json)
        editor.apply()
    }

    fun carregar(context: Context, usuario: String): List<HistoricoRelatorio> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("historico_$usuario", null)
        return if (json != null) {
            val type = object : TypeToken<List<HistoricoRelatorio>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
