package com.example.gestionventas.ui.venta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaVentasScreen(
    onNuevaVenta: () -> Unit,
    onEditarVenta: (Long) -> Unit,
    vm: VentaViewModel = viewModel()
) {
    val ventas by vm.ventas.collectAsState()

    // Estado para guardar la venta que se quiere eliminar
    var ventaAEliminar by remember { mutableStateOf<Long?>(null) }

    // Diálogo de confirmación
    if (ventaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { ventaAEliminar = null },
            title = { Text("Eliminar Venta") },
            text = { Text("¿Estás seguro de que deseas eliminar esta venta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ventaAEliminar?.let { vm.eliminarVenta(it) }
                        ventaAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { ventaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ventas Registradas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaVenta) { Text("+") }
        }
    ) { pad ->
        if (ventas.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay ventas registradas")
            }
        } else {
            LazyColumn(
                Modifier.padding(pad).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ventas) { v ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Zona clickeable para editar
                            Column(
                                modifier = Modifier.weight(1f).clickable {
                                    if (v.estado != "Validada") onEditarVenta(v.id)
                                }
                            ) {
                                Text("Código: ${v.codigoAutorizacion}", style = MaterialTheme.typography.titleMedium)
                                Text("Servicio: ${v.tipoServicio}")
                                Text("Monto: $${v.monto}")
                                Text("Estado: ${v.estado}")
                            }

                            //Botón de eliminar
                            IconButton(onClick = { ventaAEliminar = v.id }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar venta",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}