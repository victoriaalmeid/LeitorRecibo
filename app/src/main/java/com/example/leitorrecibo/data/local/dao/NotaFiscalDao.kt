package com.example.leitorrecibo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.leitorrecibo.domain.models.NotaFiscal
import com.example.leitorrecibo.domain.models.NotaFiscalComProdutos
import com.example.leitorrecibo.domain.models.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaFiscalDao {

    @Insert
    suspend fun inserirNota(nota: NotaFiscal): Long // Retorna o ID gerado

    @Insert
    suspend fun inserirProdutos(produtos: List<Produto>)

    // O @Transaction garante que ou salva tudo, ou não salva nada (evita dados corrompidos)
    @Transaction
    suspend fun salvarNotaComProdutos(urlNota: String, produtos: List<Produto>) {
        val novaNota = NotaFiscal(urlConsulta = urlNota)
        val notaId = inserirNota(novaNota) // 1. Salva a nota e pega o ID dela

        // 2. Coloca o ID da nota em todos os produtos
        val produtosComNotaId = produtos.map { it.copy(notaFiscalId = notaId) }

        // 3. Salva os produtos
        inserirProdutos(produtosComNotaId)
    }

    // Usaremos isto no ecrã de histórico!
    @Transaction
    @Query("SELECT * FROM notas_fiscais ORDER BY dataLeitura DESC")
    fun obterHistoricoDeNotas(): Flow<List<NotaFiscalComProdutos>>
}