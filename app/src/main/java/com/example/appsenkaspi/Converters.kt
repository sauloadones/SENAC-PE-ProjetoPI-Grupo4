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

    enum class PrioridadeAtividade {
        ALTA,
        MEDIA,
        BAIXA,
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    enum class StatusRequisicao {
        PENDENTE,
        APROVADA,
        REJEITADA
    }

    enum class TipoRequisicao {
        CONFIRMACAO_ATIVIDADE,
        EDICAO_PILAR,
        CRIACAO_ACAO
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
            StatusAcao.PLANEJADA
        }
    @TypeConverter fun fromStatusAtividade(statusAtividade: StatusAtividade): String = statusAtividade.name.lowercase()
    @TypeConverter fun toStatusAtividade(value: String): StatusAtividade =
        try {
            StatusAtividade.valueOf(value.uppercase())
        } catch(e: IllegalArgumentException) {
            // opcional: trate valor inválido aqui
            StatusAtividade.PENDENTE
        }
    @TypeConverter fun fromPrioridade(prioridadeAtividade: PrioridadeAtividade): String = prioridadeAtividade.name.lowercase()
    @TypeConverter fun toPrioridade(value: String): PrioridadeAtividade =
        try {
            PrioridadeAtividade.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            PrioridadeAtividade.MEDIA
        }

    @TypeConverter fun fromStatusRequisicao(status: StatusRequisicao): String = status.name.lowercase()
    @TypeConverter fun toStatusRequisicao(value: String): StatusRequisicao =
        try {
            StatusRequisicao.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            StatusRequisicao.PENDENTE
        }
    @TypeConverter fun fromTipoRequisicao(tipo: TipoRequisicao): String = tipo.name.lowercase()
    @TypeConverter fun toTipoRequisicao(value: String): TipoRequisicao =
        try {
            TipoRequisicao.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            TipoRequisicao.CONFIRMACAO_ATIVIDADE
        }

}



