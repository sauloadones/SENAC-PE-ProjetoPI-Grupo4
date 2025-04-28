package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PilarDao {

    @Insert
    suspend fun insert(pilar: PilarEntity)

    @Query("SELECT * FROM pilares ORDER BY id ASC")
    fun getAllPilares(): LiveData<List<PilarEntity>>
}
