package com.example.appsenkaspi

import androidx.room.TypeConverter
import java.util.Date



class Converters {


    enum class Cargo {
        COORDENADOR,
        GESTOR,
        APOIO
    }
    enum class StatusPilar {
        ATIVO,
        EXCLUIDO,
        VENCIDO
    }

    enum class StatusAcao {
        PLANEJADA,
        EM_ANDAMENTO,
        CONCLUIDA,
        EXCLUIDA,
        VENCIDA,
    }

    enum class StatusAtividade {
        PENDENTE,
        EM_ANDAMENTO,
        CONCLUIDA,
        EXCLUIDA,
        VENCIDA,
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter fun fromCargo(cargo: Cargo): String = cargo.name.lowercase()
    @TypeConverter fun toCargo(value: String): Cargo =
        try {
            Cargo.valueOf(value.uppercase())
        } catch(e: IllegalArgumentException) {
            // opcional: trate valor inválido aqui
            Cargo.APOIO
        }
    @TypeConverter fun fromStatusPilar(statuspilar: StatusPilar): String = statuspilar.name.lowercase()
    @TypeConverter fun toStatusPilar(value: String): StatusPilar =
        try {
            StatusPilar.valueOf(value.uppercase())
        } catch(e: IllegalArgumentException) {
            // opcional: trate valor inválido aqui
            StatusPilar.VENCIDO
        }

    @TypeConverter fun fromStatusAcao(statusacao: StatusAcao): String = statusacao.name.lowercase()
    @TypeConverter fun toStatusAcao(value: String): StatusAcao =
        try {
            StatusAcao.valueOf(value.uppercase())
        } catch(e: IllegalArgumentException) {
            // opcional: trate valor inválido aqui
            StatusAcao.VENCIDA
        }
    @TypeConverter fun fromStatusAtividade(statusatvidadde: StatusAtividade): String = statusatvidadde.name.lowercase()
    @TypeConverter fun toStatusAtividade(value: String): StatusAtividade =
        try {
            StatusAtividade.valueOf(value.uppercase())
        } catch(e: IllegalArgumentException) {
            // opcional: trate valor inválido aqui
            StatusAtividade.VENCIDA
        }
}

