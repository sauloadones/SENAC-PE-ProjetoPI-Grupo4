package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

@Dao
interface PilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirPilar(pilar: PilarEntity): Long


    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun buscarPilarPorId(id: Int): PilarEntity?

    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun getPilarPorId(id: Int): PilarEntity?

    @Query("SELECT * FROM pilares WHERE id = :id")
    fun getPilarById(id: Int): LiveData<PilarEntity?>

    @Update
    suspend fun atualizarPilar(pilar: PilarEntity)

    @Delete
    suspend fun deletarPilar(pilar: PilarEntity)

    @Query("SELECT * FROM pilares")
    suspend fun getTodosPilares(): List<PilarEntity>

    @Query("SELECT * FROM pilares")
    fun listarTodosPilares(): LiveData<List<PilarEntity>>

    @Insert
    suspend fun inserirRetornandoId(pilar: PilarEntity): Long


    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun getById(id: Int): PilarEntity

    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun buscarPorId(id: Int): PilarEntity?

    @Query("SELECT nome FROM pilares WHERE id = :id")
    suspend fun getNomePilarPorId(id: Int): String?

  @Query("SELECT * FROM pilares WHERE status IN (:statusList)")
  fun listarPilaresPorStatus(statusList: List<StatusPilar>): LiveData<List<PilarEntity>>

  @Query("UPDATE pilares SET status = :status, dataExcluido = :data WHERE id = :id")
  suspend fun excluirPilarPorId(id: Int, status: StatusPilar, data: Date): Int






}
