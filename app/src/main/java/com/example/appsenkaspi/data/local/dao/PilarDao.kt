package com.example.appsenkaspi.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsenkaspi.data.local.entity.PilarEntity
import com.example.appsenkaspi.ui.pilar.PilarNomeDTO
import com.example.appsenkaspi.data.local.enums.StatusPilar
import java.util.Date

@Dao
interface PilarDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun inserirPilar(pilar: PilarEntity): Long


    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun buscarPilarPorId(id: Int): PilarEntity?

    @Query("SELECT * FROM pilares WHERE id = :id")
    suspend fun getPilarPorId(id: Int): PilarEntity?

    @Query("SELECT * FROM pilares WHERE id = :id")
    fun getPilarById(id: Int): LiveData<PilarEntity?>

    @Update
    suspend fun atualizarPilar(pilar: PilarEntity)

  @Update
  suspend fun atualizarPilarTeste(pilar: PilarEntity): Int

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

  @Query("SELECT id, nome FROM pilares WHERE status IN (:status)")
  fun listarIdsENomesPorStatus(status: List<StatusPilar>): LiveData<List<PilarNomeDTO>>

  @Query("SELECT * FROM pilares WHERE status IN (:statusList)")
  suspend fun getPilaresPorStatus(statusList: List<StatusPilar>): List<PilarEntity>

  @Query("UPDATE pilares SET status = :novoStatus, dataConclusao = :dataConclusao WHERE id = :pilarId")
  suspend fun atualizarStatusEDataConclusao(
      pilarId: Int,
      novoStatus: StatusPilar, // ✅ tipo correto com TypeConverter
      dataConclusao: Date
  ): Int



  @Query("SELECT * FROM pilares WHERE status = :status")
    fun listarPilaresPorStatus(status: StatusPilar): LiveData<List<PilarEntity>>

    @Query("SELECT * FROM pilares WHERE status = :status AND dataExclusao = :dataExclusao")
    fun listarPilaresPorStatusEData(status: StatusPilar, dataExclusao: String): LiveData<List<PilarEntity>>

}
