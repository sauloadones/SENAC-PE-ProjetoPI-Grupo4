package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AtividadeFuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)




    @Dao
    interface AtividadeFuncionarioDao {
        @Query("SELECT f.* FROM funcionarios f INNER JOIN atividade_funcionario af ON f.id = af.funcionarioId WHERE af.atividadeId = :atividadeId")
        suspend fun getResponsaveisByAtividadeId(atividadeId: Int): List<FuncionarioEntity>
    }

  @Query("SELECT f.* FROM funcionarios f INNER JOIN atividades_funcionarios af ON f.id = af.funcionarioId WHERE af.atividadeId = :atividadeId")
  suspend fun getResponsaveisDaAtividade(atividadeId: Int): List<FuncionarioEntity>




  @Delete
    suspend fun deletarAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)

    @Query("SELECT funcionarioId FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    fun listarFuncionariosPorAtividade(atividadeId: Int): LiveData<List<Int>>

    @Query("SELECT atividadeId FROM atividades_funcionarios WHERE funcionarioId = :funcionarioId")
    fun listarAtividadesPorFuncionario(funcionarioId: Int): LiveData<List<Int>>

    @Query("DELETE FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    suspend fun deletarPorAtividade(atividadeId: Int)

    @Query("""
        SELECT f.* FROM funcionarios f
        INNER JOIN atividades_funcionarios af ON af.funcionarioId = f.id
        WHERE af.atividadeId = :atividadeId
    """)
    suspend fun getResponsaveisByAtividadeId(atividadeId: Int): List<FuncionarioEntity>




}
