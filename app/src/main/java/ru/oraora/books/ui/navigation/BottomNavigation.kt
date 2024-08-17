package ru.oraora.books.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.oraora.books.viewmodel.Routes


@Composable
fun BottomNavigationBar(
    navController: NavController,
    activateSearch: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
    ) {

        val selectedColor = MaterialTheme.colorScheme.onBackground
        val unselectedColor = selectedColor.copy(alpha = 0.5f)

        BottomNavigation(
            backgroundColor = MaterialTheme.colorScheme.background
        ) {
            BOTTOM_TABS.forEach { item ->
                val isSelected = currentDestination.isCurrent(item.route)
                BottomNavigationItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigateWithBackStart(item.route)
                        } else if (item.selectedId == BottomNavigationData.SEARCH.selectedId) {
                            activateSearch()
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (isSelected) item.selectedId else item.unselectedId
                            ),
                            contentDescription = null,
                            tint = if (isSelected) selectedColor else unselectedColor
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

    }
}