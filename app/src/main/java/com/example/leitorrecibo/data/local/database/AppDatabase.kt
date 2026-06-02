package com.example.leitorrecibo.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.leitorrecibo.data.local.dao.NotaFiscalDao
import com.example.leitorrecibo.data.local.dao.ProdutoDao
import com.example.leitorrecibo.domain.models.NotaFiscal
import com.example.leitorrecibo.domain.models.Produto

@Database(
    entities = [NotaFiscal::class, Produto::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaFiscalDao(): NotaFiscalDao
    abstract fun produtoDao(): ProdutoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leitor_recibo_database"
                )
                    // Como estamos em fase de desenvolvimento, dizemos ao Room para destruir e recriar
                    // o banco de dados se a versão mudar, para não termos de escrever scripts de migração agora.
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
