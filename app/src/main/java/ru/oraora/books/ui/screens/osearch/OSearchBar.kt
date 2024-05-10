package ru.oraora.books.ui.screens.osearch

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    modifier: Modifier = Modifier,
    firstLine: (@Composable () -> Unit),
    scrollState: LazyListState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    searchHistory: MutableList<String>,
    animationProgress: State<Float> = OSearchBarDefaults.animationProgress(active = active),
) {

    val firstLineId = "first_line"
    val secondLineId = "second_line"

    val topPadding = WindowInsets
        .statusBars.asPaddingValues().calculateTopPadding()

    Layout(
        content = {
            Box(
                modifier = Modifier.layoutId(firstLineId)
            ) {
                firstLine()
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .layoutId(secondLineId)
                    .padding(vertical = 4.dp)
            ) {
                OSearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    active = active,
                    onActiveChange = onActiveChange,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    searchHistory = searchHistory,
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
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    searchHistory: MutableList<String>,
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
                val radius = size.height * (1 - animationProgress.value) / 2
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
                    if (query.isNotEmpty() && query !in searchHistory) {
                        searchHistory.add(query)
                    }
                    onSearch()
                },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon?.let { trailing ->
                    {
                        if (animationProgress.value > 0) {
                            IconButton(
                                modifier = Modifier
                                    .rotate(animationProgress.value * 360)
                                    .alpha(animationProgress.value),
                                onClick = {
                                    if (query.isNotEmpty()) {
                                        onQueryChange("")
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

            val showResults by remember {
                derivedStateOf(structuralEqualityPolicy()) { animationProgress.value > 0 }
            }

            if (showResults) {
                Column(
                    Modifier
                        .imePadding()
                        .padding(bottom = 16.dp)
                        .graphicsLayer { alpha = animationProgress.value }) {
                    HorizontalDivider(color = colors.dividerColor)
                    LazyColumn {
                        items(searchHistory) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        onActiveChange(false)
                                        keyboardController?.hide()
                                        onQueryChange(it)
                                        onSearch()
                                    }
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.History,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                                )
                                Text(
                                    text = it,
                                    modifier = Modifier.weight(1f)
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
    query: String,
    onQueryChange: (String) -> Unit,
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
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { onActiveChange(it.isFocused) },
        singleLine = true,
        textStyle = LocalTextStyle.current.merge(TextStyle(color = textColor)),
        cursorBrush = SolidColor(colors.cursorColor),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() },
        ),
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = query,
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

@ExperimentalMaterial3Api
object OSearchBarDefaults {
    val elevation: Dp = 6.dp
    val fieldHeight: Dp = 56.dp
    val fieldSideMargin: Dp = 16.dp
    val iconOffsetX = 4.dp

    val startShape = RoundedCornerShape(50)
    val finishShape = RectangleShape

    val AnimationEnterFloatSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 600,
        delayMillis = 100,
        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    )

    val AnimationExitFloatSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 350,
        delayMillis = 100,
        easing = CubicBezierEasing(0.0f, 1.0f, 0.0f, 1.0f),
    )

    @Composable
    fun animationProgress(active: Boolean): State<Float> {
        return animateFloatAsState(
            targetValue = if (active) 1f else 0f,
            animationSpec = if (active) AnimationEnterFloatSpec else AnimationExitFloatSpec,
            label = ""
        )
    }

    @Composable
    fun colors(): OSearchBarColors {
        return OSearchBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            dividerColor = MaterialTheme.colorScheme.outline,
            textColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            selectionColors = LocalTextSelectionColors.current,
            leadingIconColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

}


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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MyNewSearchBarPreview() {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val searchHistory = rememberSaveable { mutableListOf("A", "B", "C") }
    Surface(modifier = Modifier.wrapContentSize(Alignment.Center)) {
        OSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { },
            active = active,
            onActiveChange = { active = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            searchHistory = searchHistory,
        )
    }

}