package com.example.gestionventas.ui.venta

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionventas.data.local.AppDatabase
import com.example.gestionventas.data.local.VentaEntity
import com.example.gestionventas.data.repository.VentaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VentaViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VentaRepository(AppDatabase.getInstance(app).ventaDao())


    var uiState by mutableStateOf(VentaUiState())
        private set


    val ventas: StateFlow<List<VentaEntity>> = repo.obtenerVentas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var ventaIdActual: Long? = null


    fun onTipoServicioChange(v: String) {
        uiState = uiState.copy(tipoServicio = v, tipoServicioError = null)
    }
    fun onFechaChange(v: String) { uiState = uiState.copy(fechaOperacion = v, fechaError = null) }
    fun onClienteChange(v: String) { uiState = uiState.copy(cliente = v, clienteError = null) }
    fun onMontoChange(v: String) {
        uiState = uiState.copy(monto = v, montoError = null)
    }
    fun onCodigoChange(v: String) {
        uiState = uiState.copy(codigoAutorizacion = v, codigoError = null)
    }
    fun onObservacionesChange(v: String) { uiState = uiState.copy(observaciones = v) }
    fun onRespaldoCapturado(uri: String) { uiState = uiState.copy(respaldoUri = uri) }

    //Cargar venta para editar
    fun cargarVenta(id: Long) {
        viewModelScope.launch {
            repo.obtenerVenta(id)?.let { v ->
                ventaIdActual = v.id
                uiState = uiState.copy(
                    tipoServicio = v.tipoServicio,
                    fechaOperacion = v.fechaOperacion,
                    cliente = v.cliente,
                    monto = v.monto.toString(),
                    codigoAutorizacion = v.codigoAutorizacion,
                    observaciones = v.observaciones,
                    respaldoUri = v.respaldoUri,
                    estado = v.estado
                )
            }
        }
    }


    fun validarYGuardar() {
        val s = uiState

        val tipoErr = if (s.tipoServicio.isBlank()) "Requerido" else null
        val fechaErr = if (s.fechaOperacion.isBlank()) "Requerido" else null
        val clienteErr = if (s.cliente.isBlank()) "Requerido" else null
        val montoD = s.monto.toDoubleOrNull()
        val montoErr = if (montoD == null || montoD <= 0) "Debe ser numérico > 0" else null
        val codErr = if (s.codigoAutorizacion.isBlank()) "Requerido" else null

        if (tipoErr != null || fechaErr != null || clienteErr != null ||
            montoErr != null || codErr != null) {
            uiState = s.copy(
                tipoServicioError = tipoErr,
                fechaError = fechaErr,
                clienteError = clienteErr,
                montoError = montoErr,
                codigoError = codErr
            )
            return
        }

        viewModelScope.launch {
            if (repo.existeCodigo(s.codigoAutorizacion, ventaIdActual ?: -1L)) {
                uiState = uiState.copy(codigoError = "Código duplicado")
                return@launch
            }
            uiState = uiState.copy(guardando = true)

            val entity = VentaEntity(
                id = ventaIdActual ?: 0L,
                tipoServicio = s.tipoServicio,
                fechaOperacion = s.fechaOperacion,
                cliente = s.cliente,
                monto = montoD!!,
                codigoAutorizacion = s.codigoAutorizacion,
                observaciones = s.observaciones,
                respaldoUri = s.respaldoUri,
                estado = s.estado
            )

            if (ventaIdActual == null) repo.guardar(entity) else repo.actualizar(entity)
            uiState = uiState.copy(guardando = false, guardadoExitosamente = true)
        }
    }
}