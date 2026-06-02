package com.example.leitorrecibo.ui.screens.NotasSalvas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.leitorrecibo.data.local.database.AppDatabase
import com.example.leitorrecibo.domain.models.NotaFiscalComProdutos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NotasSalvasViewModel(application: Application) : AndroidViewModel(application) {

    private val notaFiscalDao = AppDatabase.getDatabase(application).notaFiscalDao()

    // O StateFlow observa o banco de dados de forma reativa.
    // Sempre que o Room sofrer uma alteração, esta lista é atualizada automaticamente.
    val historicoNotas: StateFlow<List<NotaFiscalComProdutos>> = notaFiscalDao.obterHistoricoDeNotas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Mantém o fluxo vivo por 5s após a UI ser destruída
            initialValue = emptyList()
        )
}