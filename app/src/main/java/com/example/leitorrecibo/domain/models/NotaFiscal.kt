package com.example.leitorrecibo.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas_fiscais")
data class NotaFiscal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val urlConsulta: String,
    val dataLeitura: Long = System.currentTimeMillis(), // Guarda a data/hora do scan
    val supermercado: String = "Mercado Desconhecido" // No futuro podemos extrair o nome real!
)