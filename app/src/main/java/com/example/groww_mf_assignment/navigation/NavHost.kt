package com.example.groww_mf_assignment.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groww_mf_assignment.presentation.Explore.ExploreScreen
import com.example.groww_mf_assignment.presentation.Info.ProductScreen
import com.example.groww_mf_assignment.presentation.Search.SearchScreen
import com.example.groww_mf_assignment.presentation.Explore.ViewAllScreen
import com.example.groww_mf_assignment.presentation.WatchList.WatchlistScreen
@Composable
fun MfAppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Explore.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Explore.route) {
                ExploreScreen(
                    onNavigateToViewAll = { categoryTitle, query ->
                        // Now perfectly matches (String, String) -> Unit
                        navController.navigate(Screen.ViewAll.createRoute(categoryTitle, query))
                    },
                    onNavigateToDetails = { schemeCode ->
                        navController.navigate(Screen.Product.createRoute(schemeCode))
                    }
                )
            }

            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    onNavigateToDetails = { schemeCode ->
                        navController.navigate(Screen.Product.createRoute(schemeCode))
                    }
                )
            }

            composable(
                route = Screen.Product.route,
                arguments = listOf(navArgument("schemeCode") { type = NavType.IntType })
            ) {
                ProductScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }


            composable(
                route = Screen.ViewAll.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType },
                    navArgument("query") { type = NavType.StringType }
                )
            ) {
                ViewAllScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToDetails = { schemeCode ->
                        navController.navigate(Screen.Product.createRoute(schemeCode))
                    }
                )
            }


            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateToDetails = { schemeCode ->
                        navController.navigate(Screen.Product.createRoute(schemeCode))
                    }
                )
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    if (currentRoute == Screen.Explore.route ||
        currentRoute == Screen.Search.route ||
        currentRoute == Screen.Watchlist.route) {

        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                label = { Text("Explore") },
                selected = currentRoute == Screen.Explore.route,
                onClick = {
                    navController.navigate(Screen.Explore.route) {
                        popUpTo(Screen.Explore.route) { inclusive = true }
                    }
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = currentRoute == Screen.Search.route,
                onClick = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Explore.route)
                    }
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.FolderSpecial, contentDescription = "Watchlist") },
                label = { Text("Watchlist") },
                selected = currentRoute == Screen.Watchlist.route,
                onClick = {
                    navController.navigate(Screen.Watchlist.route) {
                        popUpTo(Screen.Explore.route)
                    }
                }
            )
        }
    }
}