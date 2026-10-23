package com.example.gestionventas.data.repository

import com.example.gestionventas.data.local.VentaDao
import com.example.gestionventas.data.local.VentaEntity
import kotlinx.coroutines.flow.Flow

/**
 *   actua como puente entre el ViewModel y la capa de datos (Room).

 */
class VentaRepository(private val dao: VentaDao) {
    fun obtenerVentas(): Flow<List<VentaEntity>> = dao.obtenerTodas()

    suspend fun obtenerVenta(id: Long): VentaEntity? = dao.obtenerPorId(id)

    suspend fun guardar(venta: VentaEntity): Long = dao.insertar(venta)

    suspend fun actualizar(venta: VentaEntity) = dao.actualizar(venta)

    suspend fun existeCodigo(codigo: String, idExcluir: Long = -1L): Boolean =
        dao.contarPorCodigo(codigo, idExcluir) > 0
}