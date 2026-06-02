package com.example.leitorrecibo.domain.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "produtos",
    foreignKeys = [
        ForeignKey(
            entity = NotaFiscal::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("notaFiscalId"),
            onDelete = ForeignKey.CASCADE // Se apagar a nota, apaga os produtos dela!
        )
    ],
    indices = [Index(value = ["notaFiscalId"])] // Deixa as buscas mais rápidas
)
data class Produto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val notaFiscalId: Long = 0, // A CHAVE ESTRANGEIRA MÁGICA
    val nome: String,
    val preco: String
)