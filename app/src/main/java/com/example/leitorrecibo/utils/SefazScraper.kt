package com.example.leitorrecibo.utils

import com.example.leitorrecibo.domain.models.Produto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

// Função 'suspend' significa que ela roda em background (Coroutines)
suspend fun extrairDadosDoHtml(htmlCompleto: String): List<Produto> = withContext(Dispatchers.IO) {
    val listaProdutos = mutableListOf<Produto>()

    try {
        val documento = Jsoup.parse(htmlCompleto)

        // Estratégia Principal: Procura as linhas da tabela
        val linhasProdutos = documento.select("table#tabResult tr")

        for (linha in linhasProdutos.take(1)) {
            println(linha.outerHtml())
        }

        for (linha in linhasProdutos) {

            val nomeProduto = linha
                .selectFirst("span.txtTit")
                ?.text()
                ?.trim()
                ?: ""

            var precoProduto = linha
                .selectFirst("span.valor")
                ?.text()
                ?.trim()
                ?: ""

            if (precoProduto.isNotBlank()) {
                precoProduto = "R$ $precoProduto"
            }

            if (nomeProduto.isNotBlank() && precoProduto.isNotBlank()) {
                listaProdutos.add(
                    Produto(
                        nome = nomeProduto,
                        preco = precoProduto
                    )
                )
            }
        }

        // Estratégia de Emergência (caso a tabela não se chame tabResult)
        if (listaProdutos.isEmpty()) {
            val titulos = documento.select(".txtTit")
            for (titulo in titulos) {
                val nome = titulo.text()
                val preco = titulo.parent()?.parent()?.select(".valor")?.last()?.text() ?: ""

                if (nome.isNotBlank()) {
                    listaProdutos.add(Produto(nome=nome, preco=preco))
                }
            }
        }

    } catch (e: Exception) {
        println("Erro ao processar HTML: ${e.message}")
    }

    return@withContext listaProdutos
}