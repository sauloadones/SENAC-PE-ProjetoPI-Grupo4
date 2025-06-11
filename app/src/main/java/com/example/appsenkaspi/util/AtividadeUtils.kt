package com.example.appsenkaspi.util


import com.example.appsenkaspi.data.local.entity.AtividadeEntity
import com.example.appsenkaspi.data.local.enums.StatusAtividade
import java.util.*

fun AtividadeEntity.estaVencidaAgora(): Boolean {
  val hoje = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
  }.time
  return dataPrazo?.before(hoje) == true && status != StatusAtividade.CONCLUIDA
}
