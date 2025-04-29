package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubpilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirSubpilar(subpilar: SubpilarEntity)

    @Query("SELECT * FROM subpilares WHERE pilarId = :pilarId")
    fun listarSubpilaresPorPilar(pilarId: Int): LiveData<List<SubpilarEntity>>

    @Query("SELECT * FROM subpilares WHERE id = :id")
    suspend fun buscarSubpilarPorId(id: Int): SubpilarEntity?

    @Update
    suspend fun atualizarSubpilar(subpilar: SubpilarEntity)

    @Delete
    suspend fun deletarSubpilar(subpilar: SubpilarEntity)
}
