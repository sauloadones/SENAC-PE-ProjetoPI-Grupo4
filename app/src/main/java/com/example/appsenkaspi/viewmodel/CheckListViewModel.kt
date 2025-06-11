package com.example.appsenkaspi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.data.local.dao.ChecklistDao
import com.example.appsenkaspi.data.local.database.AppDatabase
import com.example.appsenkaspi.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ DAO obtido diretamente a partir da instância do AppDatabase
    private val dao: ChecklistDao = AppDatabase.getDatabase(application).checklistDao()

    fun getChecklist(atividadeId: Int): LiveData<List<ChecklistItemEntity>> =
        dao.getChecklist(atividadeId)

    fun inserir(item: ChecklistItemEntity) = viewModelScope.launch {
        dao.inserir(item)
    }

    fun atualizar(item: ChecklistItemEntity) = viewModelScope.launch {
        dao.atualizar(item)
    }

    fun deletar(item: ChecklistItemEntity) = viewModelScope.launch {
        dao.deletar(item)
    }
}
