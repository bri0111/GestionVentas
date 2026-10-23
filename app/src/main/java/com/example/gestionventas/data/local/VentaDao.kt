package com.example.gestionventas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Query("SELECT * FROM ventas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<VentaEntity>>

    @Query("SELECT * FROM ventas WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): VentaEntity?


    @Query(
        "SELECT COUNT(*) FROM ventas " +
                "WHERE codigoAutorizacion = :codigo AND id != :idExcluir"
    )
    suspend fun contarPorCodigo(codigo: String, idExcluir: Long = -1L): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(venta: VentaEntity): Long

    @Update
    suspend fun actualizar(venta: VentaEntity)

    @Query("DELETE FROM ventas WHERE id = :id")
    suspend fun eliminarPorId(id: Long)
}