

package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFuncionario(funcionario: FuncionarioEntity)

    @Query("SELECT * FROM funcionarios WHERE id = :id")
    suspend fun buscarFuncionarioPorId(id: Int): FuncionarioEntity?

    @Query("SELECT * FROM funcionarios")
    fun listarTodosFuncionarios(): LiveData<List<FuncionarioEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(funcionarios: List<FuncionarioEntity>)
    @Update
    suspend fun atualizarFuncionario(funcionario: FuncionarioEntity)

    @Delete
    suspend fun deletarFuncionario(funcionario: FuncionarioEntity)
}
