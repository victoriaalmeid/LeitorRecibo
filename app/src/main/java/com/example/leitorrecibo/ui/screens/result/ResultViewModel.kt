package com.example.leitorrecibo.ui.screens.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.leitorrecibo.data.local.database.AppDatabase
import com.example.leitorrecibo.domain.models.Produto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Usamos AndroidViewModel para ter acesso ao Contexto (Application) para o Room
class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    private val notaFiscalDao = AppDatabase.getDatabase(application).notaFiscalDao()

    init {
        iniciarProcessamento()
    }

    private fun iniciarProcessamento() {
        viewModelScope.launch {
            // Fase 1: Arranque rápido
            atualizarProgresso(0.15f, "A estabelecer ligação segura...")
            delay(2000)

            // Fase 2: A parte demorada
            atualizarProgresso(0.15f, "A aguardar resposta do servidor da SEFAZ...")
            for (i in 16..85) {
                if (!_uiState.value.isLoading) break
                atualizarProgresso(i / 100f, _uiState.value.mensagemStatus)
                delay(500)
            }

            // Fase 3: Timeout de segurança
            if (_uiState.value.isLoading) {
                atualizarProgresso(0.85f, "O servidor está lento. A processar itens...")
                delay(25000)

                if (_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false, erroTimeout = true) }
                }
            }
        }
    }

    private fun atualizarProgresso(progresso: Float, mensagem: String) {
        _uiState.update { it.copy(progresso = progresso, mensagemStatus = mensagem) }
    }

    fun onProdutosExtraidos(produtos: List<Produto>, urlNota: String) {
        if (produtos.isEmpty() || _uiState.value.dadosSalvos) return

        viewModelScope.launch {
            // Atualiza UI para 100%
            atualizarProgresso(1f, "Nota lida com sucesso!")
            delay(600)

            // Salva a Nota e os Produtos juntos (numa transação) para evitar erro de Foreign Key
            notaFiscalDao.salvarNotaComProdutos(urlNota, produtos)

            _uiState.update {
                it.copy(
                    listaProdutos = produtos,
                    isLoading = false,
                    dadosSalvos = true
                )
            }
        }
    }
}