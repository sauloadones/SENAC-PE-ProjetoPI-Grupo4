package com.example.appsenkaspi


import androidx.room.*
import com.example.appsenkaspi.data.AcaoComStatus
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Dao
interface AcaoDao {



    /**
     * Insere uma Ação no banco e retorna o ID gerado.
     * Usa REPLACE para conflitos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAcao(acao: AcaoEntity): Long

    /**
     * Atualiza uma Ação existente.
     */
    @Update
    suspend fun atualizarAcao(acao: AcaoEntity)

    /**
     * Remove uma Ação.
     */
    @Delete
    suspend fun deletarAcao(acao: AcaoEntity)

    /**
     * Lista todas as Ações pertencentes a um Pilar.
     */
    @Query("SELECT * FROM acoes WHERE pilarId = :pilarId")
    fun listarAcoesPorPilar(pilarId: Int): LiveData<List<AcaoEntity>>

    /**
     * Busca uma Ação pelo ID.
     */
    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun buscarAcaoPorId(id: Int): AcaoEntity?

    @Query("SELECT * FROM acoes WHERE id = :id")
    fun getAcaoById(id: Int): LiveData<AcaoEntity?>



    /**
     * Retorna a Ação junto com os Funcionários relacionados.
     */
    @Transaction


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRetornandoId(acao: AcaoEntity): Long



    @Query("""
    SELECT
      a.id,
      a.nome,
      a.descricao,
      a.dataInicio,
      a.dataPrazo,    
      a.pilarId,    
      a.status,
      a.criado_por,
      a.data_criacao  ,
      COUNT(at.id)    AS totalAtividades,
      SUM(
        CASE WHEN at.status = 'concluida' THEN 1 
             ELSE 0 
        END
      ) AS ativasConcluidas
    FROM acoes AS a
    LEFT JOIN atividades AS at
      ON at.acaoId = a.id
    WHERE a.pilarId = :pilarId
    GROUP BY a.id
""")
    fun listarPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>>

}








