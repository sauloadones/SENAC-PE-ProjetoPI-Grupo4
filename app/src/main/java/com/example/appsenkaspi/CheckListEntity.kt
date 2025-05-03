package com.example.appsenkaspi

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.appsenkaspi.Converters.PrioridadeAtividade
import com.example.appsenkaspi.Converters.StatusAcao
import com.example.appsenkaspi.Converters.StatusAtividade

@Entity(
    tableName = "checklist_itens",
    foreignKeys = [ForeignKey(
        entity = AtividadeEntity::class,
        parentColumns = ["id"],
        childColumns = ["atividadeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("atividadeId")]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descricao: String,
    val concluido: Boolean = false,
    val atividadeId: Int
)
