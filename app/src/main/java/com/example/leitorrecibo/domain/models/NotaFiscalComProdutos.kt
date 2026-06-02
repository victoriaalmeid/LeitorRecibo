package com.example.leitorrecibo.domain.models

import androidx.room.Embedded
import androidx.room.Relation

data class NotaFiscalComProdutos(
    @Embedded val nota: NotaFiscal,

    @Relation(
        parentColumn = "id",
        entityColumn = "notaFiscalId"
    )
    val produtos: List<Produto>
)