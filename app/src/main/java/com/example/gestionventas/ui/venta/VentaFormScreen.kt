package com.example.gestionventas.ui.venta

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionventas.ui.theme.utils.CameraPermissionHelper
import com.example.gestionventas.ui.theme.utils.QrScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaFormScreen(
    ventaId: Long?,
    onGuardado: () -> Unit,
    vm: VentaViewModel = viewModel()
) {
    val state = vm.uiState
    val context = LocalContext.current

    // Estado local para mostrar/ocultar el escáner QR
    var mostrarQr by remember { mutableStateOf(false) }
    var tienePermiso by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }

    // Lanzador de permiso de cámara
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        tienePermiso = granted
        if (granted) mostrarQr = true
    }

    // Cargar venta si es edición
    LaunchedEffect(ventaId) { ventaId?.let { vm.cargarVenta(it) } }

    // Volver al guardar
    LaunchedEffect(state.guardadoExitosamente) {
        if (state.guardadoExitosamente) onGuardado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ventaId == null) "Nueva Venta" else "Editar Venta") }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Campos del formulario ---
            OutlinedTextField(
                value = state.tipoServicio,
                onValueChange = vm::onTipoServicioChange,
                label = { Text("Tipo de servicio") },
                isError = state.tipoServicioError != null,
                supportingText = { Text(state.tipoServicioError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.fechaOperacion,
                onValueChange = vm::onFechaChange,
                label = { Text("Fecha (dd/mm/aaaa)") },
                isError = state.fechaError != null,
                supportingText = { Text(state.fechaError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.cliente,
                onValueChange = vm::onClienteChange,
                label = { Text("Cliente") },
                isError = state.clienteError != null,
                supportingText = { Text(state.clienteError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.monto,
                onValueChange = vm::onMontoChange,
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.montoError != null,
                supportingText = { Text(state.montoError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.codigoAutorizacion,
                onValueChange = vm::onCodigoChange,
                label = { Text("Código de autorización") },
                isError = state.codigoError != null,
                supportingText = { Text(state.codigoError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.observaciones,
                onValueChange = vm::onObservacionesChange,
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón para escanear QR
            Button(
                onClick = {
                    if (tienePermiso) {
                        mostrarQr = true
                    } else {
                        permisoLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escanear QR del código")
            }

            // Escáner QR en línea
            if (mostrarQr && tienePermiso) {
                Text(
                    "Enfoca el QR en el recuadro",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    QrScanner(
                        onQrCodeScanned = { codigo ->
                            vm.onCodigoChange(codigo)
                            mostrarQr = false
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                TextButton(
                    onClick = { mostrarQr = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar escaneo")
                }
            }

            // Botón Guardar
            Button(
                onClick = vm::validarYGuardar,
                enabled = !state.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.guardando) "Guardando..." else "Guardar")
            }
        }
    }
}