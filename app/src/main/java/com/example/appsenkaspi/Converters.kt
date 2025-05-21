package com.example.appsenkaspi

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.Date

class Converters {

    // ✅ Conversores corretos para Date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    // Cargo
    @TypeConverter
    fun fromCargo(cargo: Cargo): String = cargo.name.lowercase()

    @TypeConverter
    fun toCargo(value: String): Cargo = try {
        Cargo.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        Cargo.APOIO
    }

    // StatusPilar
    @TypeConverter
    fun fromStatusPilar(status: StatusPilar): String = status.name.lowercase()

    @TypeConverter
    fun toStatusPilar(value: String): StatusPilar = try {
        StatusPilar.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        StatusPilar.PLANEJADA
    }

  @TypeConverter
  fun fromStatusSubPilar(status: StatusSubPilar): String = status.name.lowercase()
  @TypeConverter
  fun toStatusSubPilar(value: String): StatusSubPilar = try {
    StatusSubPilar.valueOf(value.uppercase())
  } catch (e: IllegalArgumentException) {
    StatusSubPilar.PLANJEADA
  }

    // StatusAcao
    @TypeConverter
    fun fromStatusAcao(status: StatusAcao): String = status.name.lowercase()

    @TypeConverter
    fun toStatusAcao(value: String): StatusAcao = try {
        StatusAcao.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        StatusAcao.PLANEJADA
    }

    // StatusAtividade
    @TypeConverter
    fun fromStatusAtividade(status: StatusAtividade): String = status.name.lowercase()

    @TypeConverter
    fun toStatusAtividade(value: String): StatusAtividade = try {
        StatusAtividade.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        StatusAtividade.PENDENTE
    }

    // Prioridade
    @TypeConverter
    fun fromPrioridade(prioridade: PrioridadeAtividade): String = prioridade.name.lowercase()

    @TypeConverter
    fun toPrioridade(value: String): PrioridadeAtividade = try {
        PrioridadeAtividade.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        PrioridadeAtividade.MEDIA
    }

    // StatusRequisicao
    @TypeConverter
    fun fromStatusRequisicao(status: StatusRequisicao): String = status.name.lowercase()

    @TypeConverter
    fun toStatusRequisicao(value: String): StatusRequisicao = try {
        StatusRequisicao.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        StatusRequisicao.PENDENTE
    }

    // TipoRequisicao
    @TypeConverter
    fun fromTipoRequisicao(tipo: TipoRequisicao): String = tipo.name.lowercase()

    @TypeConverter
    fun toTipoRequisicao(value: String): TipoRequisicao = try {
        TipoRequisicao.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        TipoRequisicao.COMPLETAR_ATIVIDADE
    }




    // TipoDeNotificacao


    // JSON para entidades completas (opcional, se usados em campos string)
    private val gson = Gson()

    @TypeConverter
    fun fromAcao(acao: AcaoEntity?): String? = gson.toJson(acao)

    @TypeConverter
    fun toAcao(json: String?): AcaoEntity? = json?.let { gson.fromJson(it, AcaoEntity::class.java) }

    @TypeConverter
    fun fromAtividade(atividade: AtividadeEntity?): String? = gson.toJson(atividade)

    @TypeConverter
    fun toAtividade(json: String?): AtividadeEntity? = json?.let { gson.fromJson(it, AtividadeEntity::class.java) }
}
