package dev.daniza.draflix.ui.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.daniza.draflix.ui.screen.list.HomeListScreen

@Composable
fun DraflixScreen() {
    val navController = rememberNavController()
    DraflixNavHost(
        navController = navController
    )
}

@Composable
fun DraflixNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = DraflixDestination.HomeList.route) {
        composable(route = DraflixDestination.HomeList.route) {
            HomeListScreen(
                onMovieClick = {
                    navController.navigate(DraflixDestination.Detail.createRoute(it))
                }
            )
        }
        composable(
            route = DraflixDestination.Detail.route,
            arguments = DraflixDestination.Detail.navArguments
        ) {
            val movieId = it.arguments?.getString(DraflixDestination.Detail.paramName)
            Toast.makeText(LocalContext.current, "THE MOVIE ID IS $movieId", Toast.LENGTH_SHORT)
                .show()
        }
    }
}