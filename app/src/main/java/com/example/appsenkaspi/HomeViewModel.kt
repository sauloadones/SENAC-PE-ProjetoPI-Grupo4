package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val pilarDao: PilarDao
) : ViewModel() {

    val listaPilaresLiveData: LiveData<List<PilarEntity>> = pilarDao.getAllPilares()

    fun adicionarPilar(nome: String, descricao: String, dataPrazo: Date?) {
        val novoPilar = PilarEntity(
            nome = nome,
            descricao = descricao,
            dataPrazo = dataPrazo
        )
        viewModelScope.launch {
            pilarDao.insert(novoPilar)
            // Não precisa chamar carregarPilares, o LiveData do Room já atualiza automaticamente
        }
    }
}
