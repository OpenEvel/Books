package ru.oraora.books.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.oraora.books.R
import ru.oraora.books.viewmodel.Routes

@Composable
fun BottomNavigationBar(
    navController: NavController,
    activateSearch: () -> Unit,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination: NavDestination?
    val isRealBookInfo: Boolean

    if (!navBackStackEntry?.destination.isSelected(Routes.BOOK_INFO)) {
        currentDestination = navBackStackEntry?.destination
        isRealBookInfo = false
    } else {
        currentDestination = navController.previousBackStackEntry?.destination
        isRealBookInfo = true

    }

    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.37f))
        NavigationBar(
            tonalElevation = 0.dp
        ) {

            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.ADVICE),
                onClick = { navController.myNavigate(Routes.ADVICE) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.asterisk
                        ),
                        contentDescription = null
                    )
                },
            )

            // Search Screen
            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.SEARCH),
                onClick = {
                    if (!isRealBookInfo && currentDestination.isSelected(Routes.SEARCH)) {
                        activateSearch()
                    } else {
                        navController.myNavigate(Routes.SEARCH)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            // Favorite Screen
            NavigationBarItem(
                selected = currentDestination.isSelected(Routes.FAVORITE),
                onClick = { navController.myNavigate(Routes.FAVORITE) },
                icon = {
                    val bookmarksIcon: ImageVector =
                        if (currentDestination.isSelected(Routes.FAVORITE)) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                    Icon(bookmarksIcon, contentDescription = null)
                }
            )
        }
    }
}