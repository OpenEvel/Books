package ru.oraora.books.ui.screens.osearch

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.oraora.books.ui.LocalSearchRequester
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    modifier: Modifier = Modifier,
    firstLine: (@Composable () -> Unit),
    scrollState: LazyGridState,
    query: TextFieldValue,
    lastQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    searchHistory: List<String>,
    addHistory: (String) -> Unit,
    deletedHistory: List<String>,
    removeHistory: (String) -> Unit,
    realRemoveHistory: () -> Unit,
    clearHistory: () -> Unit,
    animationProgress: State<Float> = OSearchBarDefaults.animationProgress(active = active),
) {

    val firstLineId = "first_line"
    val secondLineId = "second_line"

    val topPadding = WindowInsets
        .statusBars.asPaddingValues().calculateTopPadding()

    Layout(
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .layoutId(firstLineId)
                    .height(OSearchBarDefaults.firstLineHeight)
                    .fillMaxWidth()
            ) {
                firstLine()
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .layoutId(secondLineId)
                    .padding(vertical = OSearchBarDefaults.secondLineVerticalPadding)
            ) {
                OSearchBar(
                    query = query,
                    lastQuery = lastQuery,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    active = active,
                    onActiveChange = onActiveChange,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    searchHistory = searchHistory,
                    addHistory = addHistory,
                    deletedHistory = deletedHistory,
                    removeHistory = removeHistory,
                    realRemoveHistory = realRemoveHistory,
                    clearHistory = clearHistory,
                    animationProgress = animationProgress,
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) { measurables, constraints ->
        val firstLinePlaceable =
            measurables.first { it.layoutId == firstLineId }.measure(constraints)
        val secondLinePlaceable =
            measurables.first { it.layoutId == secondLineId }.measure(constraints)

        val firstLineOffset = if (scrollState.firstVisibleItemIndex == 0) {
            min(firstLinePlaceable.height, scrollState.firstVisibleItemScrollOffset)
        } else {
            firstLinePlaceable.height
        }

        val firstLineAlpha = if (firstLinePlaceable.height == 0) {
            0f
        } else {
            (firstLinePlaceable.height - firstLineOffset) / firstLinePlaceable.height.toFloat()
        }

        val firstLineStartY = topPadding.roundToPx() - firstLineOffset
        val upHeight = firstLineStartY + firstLinePlaceable.height
        val secondLineStartY = lerp(upHeight, 0, animationProgress.value)


        val commonHeight = secondLineStartY + secondLinePlaceable.height

        layout(constraints.maxWidth, commonHeight) {

            firstLinePlaceable.placeRelativeWithLayer(
                x = 0,
                y = firstLineStartY,
                layerBlock = { alpha = firstLineAlpha })

            secondLinePlaceable.placeRelative(
                x = 0,
                y = secondLineStartY
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun OSearchBar(
    query: TextFieldValue,
    lastQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    searchHistory: List<String>,
    addHistory: (String) -> Unit,
    deletedHistory: List<String>,
    removeHistory: (String) -> Unit,
    realRemoveHistory: () -> Unit,
    clearHistory: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: OSearchBarColors = OSearchBarDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    tonalElevation: Dp = OSearchBarDefaults.elevation,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    animationProgress: State<Float> = OSearchBarDefaults.animationProgress(active = active),
) {
    
    LaunchedEffect(animationProgress.value) {
        if (animationProgress.value == 0f) {

            if (query != lastQuery) {
                onQueryChange(lastQuery)
            }

            if (query.text.trim().isNotEmpty() && query.text !in searchHistory) {
                addHistory(query.text)
            }
            if (deletedHistory.isNotEmpty()) {
                realRemoveHistory()
            }
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current


    val useFullScreenShape by remember {
        derivedStateOf(structuralEqualityPolicy()) { animationProgress.value == 1f }
    }


    val animatedShape = remember(useFullScreenShape) {
        // Если заполняем весь экран
        if (useFullScreenShape) {
            // То форма поискового поля - прямоугольник
            OSearchBarDefaults.finishShape
        } else {
            // Заполняем не весь экран, то форма - скруглённый квадрат
            GenericShape { size, _ ->
                // Если прогресс анимации = 0,
                // то получаетя полоска с полностью круглыми краями
                // По мере прогреса анимации скругление уменьшается
//                val radius = size.height * (1 - animationProgress.value) / 2

                val radius = with(density) {
                    (OSearchBarDefaults.fieldHeight * (1 - animationProgress.value)).toPx()
                }
                // Добавляем скурглённый прямоугольник
                addRoundRect(RoundRect(size.toRect(), CornerRadius(radius)))
            }
        }
    }

    var unconsumedInsets by remember {
        mutableStateOf(WindowInsets(0, 0, 0, 0))
    }

    val topPadding = remember(density) {
        derivedStateOf {
            20.dp + unconsumedInsets.asPaddingValues(density).calculateTopPadding()
        }
    }

    Surface(
        shape = animatedShape,
        color = colors.containerColor,
        contentColor = contentColorFor(colors.containerColor),
        tonalElevation = tonalElevation,
        modifier = modifier
            .zIndex(1f)
            .onConsumedWindowInsetsChanged { consumedInsets ->
                unconsumedInsets = windowInsets.exclude(consumedInsets)
            }
            .consumeWindowInsets(unconsumedInsets)
            .layout { measurable, constraints ->
                val animatedTopPadding =
                    lerp(0.dp, topPadding.value, animationProgress.value).roundToPx()
                val startWidth =
                    constraints.maxWidth - 2 * OSearchBarDefaults.fieldSideMargin.roundToPx()
                val startHeight =
                    max(constraints.minHeight, OSearchBarDefaults.fieldHeight.roundToPx())
                        .coerceAtMost(constraints.maxHeight)

                val endWidth = constraints.maxWidth
                val endHeight = constraints.maxHeight

                val width = lerp(startWidth, endWidth, animationProgress.value)
                val height =
                    lerp(startHeight, endHeight, animationProgress.value) + animatedTopPadding

                val placeable = measurable.measure(
                    Constraints
                        .fixed(width, height)
                )

                layout(width, height) {
                    placeable
                        .placeRelative(0, 0)
                }
            }
    ) {
        Column {
            OSearchBarField(
                query = query,
                onQueryChange = onQueryChange,
                onActiveChange = onActiveChange,
                onSearch = {
                    // Скрыть клавиатуру после поиска
                    onActiveChange(false)
                    keyboardController?.hide()
                    // Обновить историю поиска и выполнить поиск
                    onSearch()
//                    addHistory(query)
                },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon?.let { trailing ->
                    {
                        if (active) {
                            IconButton(
                                modifier = Modifier
                                    .rotate(animationProgress.value * 450)
                                    .alpha(animationProgress.value),
                                onClick = {
                                    if (query.text.isNotEmpty()) {
                                        onQueryChange(TextFieldValue(text = ""))
                                    } else {
                                        onActiveChange(false)
                                        keyboardController?.hide()
                                    }
                                }
                            ) {
                                trailing()
                            }
                        }
                    }
                },
                enabled = enabled,
                placeholder = placeholder,
                interactionSource = interactionSource,
                modifier = Modifier.padding(top = topPadding.value * animationProgress.value)
            )

            val showHistory by remember {
                derivedStateOf(structuralEqualityPolicy()) { animationProgress.value > 0 }
            }

            var imeBottomPadding =
                WindowInsets.ime.asPaddingValues().calculateBottomPadding() - 80.dp + 24.dp
            if (imeBottomPadding < 0.dp) {
                imeBottomPadding = 0.dp
            }
            if (showHistory) {
                Column(
                    Modifier
                        .padding(bottom = imeBottomPadding)
                        .graphicsLayer { alpha = animationProgress.value }) {
                    HorizontalDivider(color = colors.dividerColor)
                    AnimatedVisibility(
                        visible = searchHistory.size > deletedHistory.size,
                        enter = expandVertically(),
                        exit = shrinkVertically()

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "ВЫ НЕДАВНО ИСКАЛИ",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Очистить",
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 16.dp)
                                    .clickable {
                                        clearHistory()
                                    }

                            )
                        }
                    }
                    LazyColumn {
                        items(searchHistory) {
                            AnimatedVisibility(
                                visible = it !in deletedHistory,
                                enter = expandVertically(),
                                exit = fadeOut()  + shrinkVertically()
                            ) {
                                HistoryItem(
                                    history = it,
                                    onItemClick = {
                                        onActiveChange(false)
                                        keyboardController?.hide()
                                        onQueryChange(TextFieldValue(text=it))
                                        onSearch()
                                    },
                                    removeHistory = removeHistory,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Stable
fun HistoryItem(
    history: String,
    onItemClick: () -> Unit,
    removeHistory: (String) -> Unit,

    ) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onItemClick() }
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Icon(
            Icons.Outlined.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 16.dp, end = 12.dp)
        )

        Text(
            text = history,
            modifier = Modifier.weight(1f)
        )


        Icon(
            Icons.Default.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp)
                .clickable { removeHistory(history) },
        )

    }
}

@Composable
@Stable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OSearchBarField(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onSearch: () -> Unit,
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: OSearchBarColors = OSearchBarDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val isKeyboardVisible by keyboardAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
            onActiveChange(false)
        }
    }

    val textColor = LocalTextStyle.current.color.takeOrElse {
        colors.textColor
    }

    BasicTextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(LocalSearchRequester.current)
            .onFocusChanged {
                if (!it.isFocused) {
                    focusManager.clearFocus()
                }
                onActiveChange(it.isFocused)
            },
        singleLine = true,
        textStyle = LocalTextStyle.current.merge(TextStyle(color = textColor, textDecoration = TextDecoration.None)),
        cursorBrush = SolidColor(colors.cursorColor),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { if (query.text.trim().isNotEmpty()) onSearch() },
        ),
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = query.text,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = placeholder,
                leadingIcon = leadingIcon?.let { leading ->
                    {
                        Box(Modifier.offset(x = OSearchBarDefaults.iconOffsetX)) { leading() }
                    }
                },
                trailingIcon = trailingIcon?.let { trailing ->
                    {
                        Box(Modifier.offset(x = -OSearchBarDefaults.iconOffsetX)) { trailing() }
                    }
                },
                shape = OSearchBarDefaults.startShape,
                colors = colors.inputFieldColors,
                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(),
                container = {},
            )
        }
    )
}


@Immutable
object OSearchBarDefaults {
    val elevation: Dp = 6.dp
    val firstLineHeight: Dp = 56.dp
    val fieldHeight: Dp = 56.dp
    val secondLineVerticalPadding: Dp = 4.dp
    val secondLineHeight: Dp = fieldHeight + secondLineVerticalPadding * 2
    val topHeight: Dp = fieldHeight + secondLineHeight

    val fieldSideMargin: Dp = 16.dp
    val iconOffsetX = 4.dp

    val startShape = RoundedCornerShape(50)
    val finishShape = RectangleShape

    val AnimationEnterFloatSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 400,
        delayMillis = 100,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    )

    val AnimationExitFloatSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 350,
        delayMillis = 100,
        easing = EaseOutQuint
    )

    @Composable
    @Stable
    fun animationProgress(active: Boolean): State<Float> {
        return animateFloatAsState(
            targetValue = if (active) 1f else 0f,
            animationSpec = if (active) AnimationEnterFloatSpec else AnimationExitFloatSpec,
            label = ""
        )
    }

    @Composable
    @Stable
    fun colors(): OSearchBarColors {
        return OSearchBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            dividerColor = MaterialTheme.colorScheme.outline.copy(0.37f),
            textColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            selectionColors = LocalTextSelectionColors.current,
            leadingIconColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

}

@Immutable
class OSearchBarColors(
    val containerColor: Color,
    val dividerColor: Color,
    val textColor: Color,
    val cursorColor: Color,
    val selectionColors: TextSelectionColors,
    val leadingIconColor: Color,
    val trailingIconColor: Color,
    val placeholderColor: Color
) {
    val inputFieldColors: TextFieldColors
        @Composable
        @Stable
        get() = TextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            disabledTextColor = textColor.copy(alpha = 0.38f),
            cursorColor = cursorColor,
            selectionColors = selectionColors,
            focusedLeadingIconColor = leadingIconColor,
            unfocusedLeadingIconColor = leadingIconColor,
            disabledLeadingIconColor = leadingIconColor.copy(alpha = 0.38f),
            focusedTrailingIconColor = trailingIconColor,
            unfocusedTrailingIconColor = trailingIconColor,
            disabledTrailingIconColor = trailingIconColor.copy(alpha = 0.38f),
            focusedPlaceholderColor = placeholderColor,
            unfocusedPlaceholderColor = placeholderColor,
            disabledPlaceholderColor = placeholderColor.copy(alpha = 0.38f),
        )
}
