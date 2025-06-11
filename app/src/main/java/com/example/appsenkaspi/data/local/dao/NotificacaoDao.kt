package com.example.appsenkaspi.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsenkaspi.data.local.entity.NotificacaoEntity

@Dao
interface NotificacaoDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun inserir(notificacao: NotificacaoEntity)

    @Update
    suspend fun atualizar(notificacao: NotificacaoEntity)

    @Delete
    suspend fun deletar(notificacao: NotificacaoEntity)

    @Query("SELECT * FROM notificacoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): NotificacaoEntity?





    @Query("DELETE FROM notificacoes WHERE status = 'ARQUIVIDA'")
    suspend fun deletarArquivadas()




    @Query("UPDATE notificacoes SET status = :status WHERE id = :id")
    fun atualizarStatus(id: Int, status: String)







}
