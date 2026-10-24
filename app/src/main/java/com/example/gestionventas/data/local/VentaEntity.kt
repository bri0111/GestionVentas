package com.example.gestionventas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(
    tableName = "ventas",
    indices = [androidx.room.Index(value = ["codigoAutorizacion"], unique = true)]
)
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val tipoServicio: String,
    val fechaOperacion: String,
    val cliente: String,
    val monto: Double,
    val codigoAutorizacion: String,
    val observaciones: String = "",


    val respaldoUri: String? = null,


    val estado: String = "Borrador"
)