package com.example.appsenkaspi.domain.model

import androidx.room.Embedded
import com.example.appsenkaspi.data.local.entity.AcaoEntity

/**
 * Projeção de Ação com status de atividades para exibição na UI.
 */
data class AcaoComStatus(
  @Embedded
    val acao: AcaoEntity,

    val totalAtividades: Int,
    val ativasConcluidas: Int
)
