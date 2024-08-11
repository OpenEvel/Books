package ru.oraora.books.ui.navigation

import androidx.annotation.DrawableRes
import ru.oraora.books.R

sealed class BottomNavigationData {
    val route: String
    @DrawableRes
    val selectedId: Int
    @DrawableRes
    val unselectedId: Int

    constructor(route: String, @DrawableRes iconId: Int) {
        this.route = route
        this.selectedId = iconId
        this.unselectedId = iconId
    }

    constructor(route: String, @DrawableRes selectedId: Int, @DrawableRes unselectedId: Int) {
        this.route = route
        this.selectedId = selectedId
        this.unselectedId = unselectedId
    }

    data object ADVICE : BottomNavigationData(route = "advice", iconId = R.drawable.icon_advice)
    data object SEARCH : BottomNavigationData(route = "search", iconId = R.drawable.icon_search)
    data object FAVORITE : BottomNavigationData(route = "favorite",
                                           selectedId = R.drawable.icon_selected_favorite,
                                           unselectedId = R.drawable.icon_unselected_favorite)
}

val BOTTOM_TABS: List<BottomNavigationData> = listOf(BottomNavigationData.ADVICE, BottomNavigationData.SEARCH, BottomNavigationData.FAVORITE)