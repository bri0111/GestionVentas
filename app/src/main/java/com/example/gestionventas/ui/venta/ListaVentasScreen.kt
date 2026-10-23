package com.example.gestionventas.ui.venta

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ventas Registradas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaVenta) { Text("+") }
        }
    ) { pad ->
        if (ventas.isEmpty()) {
            Box(
                Modifier.padding(pad).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay ventas registradas")
            }
        } else {
            LazyColumn(
                Modifier.padding(pad).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ventas) { v ->
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            if (v.estado != "Validada") onEditarVenta(v.id)
                        }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Código: ${v.codigoAutorizacion}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Servicio: ${v.tipoServicio}")
                            Text("Monto: $${v.monto}")
                            Text("Estado: ${v.estado}")
                        }
                    }
                }
            }
        }
    }
}