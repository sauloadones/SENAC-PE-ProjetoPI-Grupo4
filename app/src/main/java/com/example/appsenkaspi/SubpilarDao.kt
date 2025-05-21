package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubpilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirSubpilar(subpilar: SubpilarEntity)

    @Query("SELECT * FROM subpilares WHERE pilarId = :pilarId")
    fun listarSubpilaresPorPilar(pilarId: Int): LiveData<List<SubpilarEntity>>

  @Query("SELECT * FROM subpilares WHERE pilarId = :pilarId")
  suspend fun listarSubpilaresPorTelaPilar(pilarId: Int): List<SubpilarEntity>
    @Query("SELECT * FROM subpilares WHERE id = :id")
    suspend fun buscarSubpilarPorId(id: Int): SubpilarEntity?

    @Update
    suspend fun atualizarSubpilar(subpilar: SubpilarEntity)

    @Delete
    suspend fun deletarSubpilar(subpilar: SubpilarEntity)

  @Query("SELECT nome FROM subpilares WHERE id = :subpilarId LIMIT 1")
  suspend fun buscarNomeSubpilarPorId(subpilarId: Int): String?

  @Query("SELECT * FROM subpilares WHERE id = :id LIMIT 1")
  fun getSubpilarById(id: Int): LiveData<SubpilarEntity>

  @Insert
  suspend fun inserirRetornandoId(subpilar: SubpilarEntity): Long

  @Query("SELECT COUNT(*) FROM subpilares WHERE pilarId = :pilarId")
  suspend fun contarSubpilaresDoPilar(pilarId: Int): Int

  @Query("SELECT COUNT(*) FROM subpilares WHERE pilarId = :pilarId")
  suspend fun getQuantidadePorPilar(pilarId: Int): Int



}
