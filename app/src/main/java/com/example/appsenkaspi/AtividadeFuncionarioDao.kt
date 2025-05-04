package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AtividadeFuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)



    @Delete
    suspend fun deletarAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)

    @Query("SELECT funcionarioId FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    fun listarFuncionariosPorAtividade(atividadeId: Int): LiveData<List<Int>>

    @Query("SELECT atividadeId FROM atividades_funcionarios WHERE funcionarioId = :funcionarioId")
    fun listarAtividadesPorFuncionario(funcionarioId: Int): LiveData<List<Int>>

    @Query("DELETE FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    suspend fun deletarPorAtividade(atividadeId: Int)

}
