package dev.daniza.draflix.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class DraflixDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val navArguments: List<NamedNavArgument> = emptyList(),
) {
    data object HomeList : DraflixDestination(
        route = "list",
        title = "List",
        icon = Icons.AutoMirrored.Filled.List,
    )

    data object Detail : DraflixDestination(
        route = "detail/{movieId}",
        title = "Detail",
        icon = Icons.Filled.AccountBox,
        navArguments = listOf(navArgument("movieId") {
            type = NavType.StringType
        })
    ) {
        const val paramName = "movieId"
        fun createRoute(movieId: String) = "detail/$movieId"
    }
}