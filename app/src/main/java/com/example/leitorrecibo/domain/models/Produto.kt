package com.example.leitorrecibo.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_produtos")
data class Produto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // O banco vai gerar 1, 2, 3 automaticamente
    val nome: String,
    val preco: String
)