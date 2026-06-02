package com.example.leitorrecibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.leitorrecibo.domain.models.Produto

@Dao
interface ProdutoDao {

    // Insere uma lista inteira de produtos de uma só vez
    @Insert
    suspend fun inserirVarios(produtos: List<Produto>)

    // Busca todos os produtos guardados (para mostrar no ecrã inicial depois)
    @Query("SELECT * FROM produtos")
    suspend fun buscarTodos(): List<Produto>

    // Apaga tudo (útil para testes ou botão de reset)
    @Query("DELETE FROM produtos")
    suspend fun limparTudo()
}
