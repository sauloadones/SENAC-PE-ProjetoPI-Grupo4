package com.example.appsenkaspi.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appsenkaspi.data.local.entity.ChecklistItemEntity


@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_itens WHERE atividadeId = :atividadeId")
    fun getChecklist(atividadeId: Int): LiveData<List<ChecklistItemEntity>>

    @Insert
    suspend fun inserir(item: ChecklistItemEntity)

    @Update
    suspend fun atualizar(item: ChecklistItemEntity)

    @Delete
    suspend fun deletar(item: ChecklistItemEntity)
}
