package com.example.appsenkaspi

import androidx.room.*

import java.util.Date

@Entity(
    tableName = "notificacoes",
    foreignKeys = [
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["remetenteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FuncionarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["destinatarioId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RequisicaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["requisicaoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("remetenteId"),
        Index("destinatarioId"),
        Index("requisicaoId")
    ]
)
data class NotificacaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "tipo")
    val tipo: TipoDeNotificacao,

    @ColumnInfo(name = "mensagem")
    val mensagem: String,

    @ColumnInfo(name = "remetenteId")
    val remetenteId: Int?,

    @ColumnInfo(name = "destinatarioId")
    val destinatarioId: Int?,

    @ColumnInfo(name = "requisicaoId")
    val requisicaoId: Int? = null,

    @ColumnInfo(name = "vinculoAtividadeId")
    val atividadeId: Int? = null,

    @ColumnInfo(name = "vinculoAcaoId")
    val acaoId: Int? = null,

    @ColumnInfo(name = "status")
    val status: StatusNotificacao = StatusNotificacao.NAO_LIDA,

    @ColumnInfo(name = "dataCriacao")
    val dataCriacao: Date = Date()
)
