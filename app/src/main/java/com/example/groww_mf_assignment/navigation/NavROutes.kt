package com.example.groww_mf_assignment.navigation

sealed class Screen(val route: String) {
    object Explore : Screen("explore")
    object Watchlist : Screen("watchlist")
    object ViewAll : Screen("view_all/{category}/{query}") {
        fun createRoute(category: String, query: String) = "view_all/$category/$query"
    }
    object Product : Screen("product/{schemeCode}") {
        fun createRoute(schemeCode: Int) = "product/$schemeCode"
    }
    object Search : Screen("search")
}