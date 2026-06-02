package com.example.leitorrecibo.ui.screens.result

import com.example.leitorrecibo.domain.models.Produto

data class ResultUiState(
    val isLoading: Boolean = true,
    val progresso: Float = 0f,
    val mensagemStatus: String = "A iniciar ligação...",
    val listaProdutos: List<Produto> = emptyList(),
    val dadosSalvos: Boolean = false,
    val erroTimeout: Boolean = false
)