package com.example.gestionventas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionventas.ui.theme.HomeScreen
import com.example.gestionventas.ui.venta.ListaVentasScreen
import com.example.gestionventas.ui.venta.VentaFormScreen
import com.example.gestionventas.view.DrawerMenu

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            HomeScreen(navController = navController)
        }

        composable(
            route = "DrawerMenu/{username}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            DrawerMenu(username = username, navController = navController)
        }

        composable("listaVentas") {
            ListaVentasScreen(
                onNuevaVenta = { navController.navigate("ventaForm/-1") },
                onEditarVenta = { id -> navController.navigate("ventaForm/$id") }
            )
        }

        composable(
            route = "ventaForm/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            VentaFormScreen(
                ventaId = if (id == -1L) null else id,
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}
