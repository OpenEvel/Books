package ru.oraora.books.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.myNavigate(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavDestination?.isSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
