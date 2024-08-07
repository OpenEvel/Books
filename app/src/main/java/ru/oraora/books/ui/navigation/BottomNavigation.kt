package ru.oraora.books.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.oraora.books.R
import ru.oraora.books.viewmodel.Routes

import androidx.compose.ui.Alignment


@Composable
fun BottomNavigationBar(
    navController: NavController,
    activateSearch: () -> Unit,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination: NavDestination?
    val isRealBookInfo: Boolean

    if (!navBackStackEntry?.destination.isCurrent(Routes.BOOK_INFO)) {
        currentDestination = navBackStackEntry?.destination
        isRealBookInfo = false
    } else {
        currentDestination = navController.previousBackStackEntry?.destination
        isRealBookInfo = true

    }

    AnimatedVisibility(
        visible = !isRealBookInfo,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {

        val colorScheme = MaterialTheme.colorScheme

        Box {
            NavigationBar(
                tonalElevation = 0.dp,
            ) {

                NavigationBarItem(
                    selected = currentDestination.isCurrent(Routes.ADVICE),
                    onClick = { navController.navigateWithBackStart(Routes.ADVICE) },
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
                    selected = currentDestination.isCurrent(Routes.SEARCH),
                    onClick = {
                        if (!isRealBookInfo && currentDestination.isCurrent(Routes.SEARCH)) {
                            activateSearch()
                        } else {
                            navController.navigateWithBackStart(Routes.SEARCH)
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
                    selected = currentDestination.isCurrent(Routes.FAVORITE),
                    onClick = { navController.navigateWithBackStart(Routes.FAVORITE) },
                    icon = {
                        val bookmarksIcon: ImageVector =
                            if (currentDestination.isCurrent(Routes.FAVORITE)) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                        Icon(bookmarksIcon, contentDescription = null)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .drawBehind {
                        val brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.onBackground.copy(alpha = 0.05f),
                                colorScheme.background
                            )
                        )
                        drawRect(
                            brush = brush,
                            size = size
                        )
                    }
            )
        }
    }
}