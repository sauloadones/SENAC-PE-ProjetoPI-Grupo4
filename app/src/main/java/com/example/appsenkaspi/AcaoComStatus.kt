package com.example.appsenkaspi.data  // ou com.example.appsenkaspi

import androidx.room.Embedded
import com.example.appsenkaspi.AcaoEntity

/**
 * Projeção de Ação com status de atividades para exibição na UI.
 */
data class AcaoComStatus(
    @Embedded
    val acao: AcaoEntity,

    val totalAtividades: Int,
    val ativasConcluidas: Int
)
