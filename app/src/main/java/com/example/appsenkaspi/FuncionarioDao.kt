

package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FuncionarioDao {

    @Query("SELECT * FROM funcionarios WHERE id_acesso = :idAcesso AND senha = :senha LIMIT 1")
    suspend fun autenticar(idAcesso: Int, senha: String): FuncionarioEntity?

    @Query("SELECT * FROM funcionarios")
    suspend fun buscarTodos(): List<FuncionarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFuncionario(funcionario: FuncionarioEntity)

    @Query("SELECT * FROM funcionarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): FuncionarioEntity?

    @Query("SELECT * FROM funcionarios WHERE id = :id")
    suspend fun buscarFuncionarioPorId(id: Int): FuncionarioEntity?

    @Query("SELECT * FROM funcionarios")
    fun listarTodosFuncionarios(): LiveData<List<FuncionarioEntity>>

    @Query("SELECT * FROM funcionarios WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<FuncionarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(funcionario: FuncionarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(funcionarios: List<FuncionarioEntity>)
    @Update
    suspend fun atualizarFuncionario(funcionario: FuncionarioEntity)

    @Delete
    suspend fun deletarFuncionario(funcionario: FuncionarioEntity)

    @Query("SELECT * FROM funcionarios WHERE id IN (:ids)")
    suspend fun getFuncionariosPorIds(ids: List<Int>): List<FuncionarioEntity>

    @Query("SELECT * FROM funcionarios WHERE id = :id LIMIT 1")
    suspend fun getFuncionarioById(id: Int): FuncionarioEntity?





}
