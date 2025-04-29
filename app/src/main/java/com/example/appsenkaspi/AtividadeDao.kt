package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AtividadeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAtividade(atividade: AtividadeEntity)

    @Update
    suspend fun atualizarAtividade(atividade: AtividadeEntity)

    @Delete
    suspend fun deletarAtividade(atividade: AtividadeEntity)

    @Query("SELECT * FROM atividades WHERE acaoId = :acaoId")
    fun listarAtividadesPorAcao(acaoId: Int): LiveData<List<AtividadeEntity>>

    @Query("SELECT * FROM atividades WHERE id = :id")
    suspend fun buscarAtividadePorId(id: Int): AtividadeEntity?

    @Transaction
    @Query("SELECT * FROM atividades WHERE id = :atividadeId")
    fun buscarAtividadeComFuncionarios(atividadeId: Int): LiveData<AtividadeComFuncionarios>
}
