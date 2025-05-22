package com.example.appsenkaspi

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HistoricoStorage {

    private const val PREF_NAME = "historico_prefs"
    private const val KEY_HISTORICO = "historico_relatorios"

    fun salvar(context: Context, lista: List<HistoricoRelatorio>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(lista)
        prefs.edit().putString(KEY_HISTORICO, json).apply()
    }

    fun carregar(context: Context): MutableList<HistoricoRelatorio> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORICO, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<HistoricoRelatorio>>() {}.type
        return Gson().fromJson(json, type)
    }
}
