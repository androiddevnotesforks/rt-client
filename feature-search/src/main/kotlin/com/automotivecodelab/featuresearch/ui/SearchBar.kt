package com.automotivecodelab.featuresearch.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultCornerRadius
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuresearch.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun SearchBar(
    toolbarOffsetHeightPx: State<Float>, // when passing a pure float, scroll is freezing
    searchBarState: SearchBarState,
    onSearchBarStateChange: (SearchBarState) -> Unit,
    viewModel: SearchViewModel,
    onMenuItemClick: () -> Unit,
    clearFocus: () -> Unit
) {
    Surface(
        elevation = 4.dp, // top bar material design
        shape = RoundedCornerShape(DefaultCornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .padding(DefaultPadding)
            .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.toInt()) }
    ) {
        val query = viewModel.query.collectAsState()
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            TextField(
                value = query.value,
                onValueChange = {
                    if (searchBarState == SearchBarState.EXPANDED)
                        viewModel.onQueryChange(it)
                },
                leadingIcon = {
                    IconButton(onClick = onMenuItemClick) {
                        Icon(
                            Icons.Filled.Menu,
                            "Menu",
                        )
                    }
                },
                trailingIcon = {
                    if (query.value.isNotEmpty()) {
                        IconButton(onClick = {
                            when (searchBarState) {
                                SearchBarState.EXPANDED -> {
                                    clearFocus()
                                    onSearchBarStateChange(SearchBarState.WITH_QUERY)
                                }
                                SearchBarState.WITH_QUERY -> {
                                    viewModel.onQueryChange("")
                                    clearFocus()
                                    onSearchBarStateChange(SearchBarState.EMPTY)
                                }
                                else -> {}
                            }
                        }) {
                            Icon(
                                Icons.Filled.Clear,
                                "Clear",
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .onFocusChanged {
                        val newSearchBarState = when {
                            it.isFocused -> {
                                SearchBarState.EXPANDED
                            }
                            query.value.isEmpty() -> {
                                clearFocus()
                                SearchBarState.EMPTY
                            }
                            else -> {
                                clearFocus()
                                SearchBarState.WITH_QUERY
                            }
                        }
                        onSearchBarStateChange(newSearchBarState)
                    }
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    clearFocus()
                    viewModel.search()
                }),
                placeholder = { Text(stringResource(id = R.string.search)) }
            )
            val chipRowLazyListState = rememberLazyListState()
            val suggestions = viewModel.searchSuggestions.collectAsState()
            if (searchBarState == SearchBarState.EXPANDED) {
                ChipRow(
                    currentSort = viewModel.sort,
                    currentOrder = viewModel.order,
                    onSortChange = viewModel::onSortChange,
                    onOrderChange = viewModel::onOrderChange,
                    lazyListState = chipRowLazyListState
                )
                LazyColumn {
                    items(items = suggestions.value) { suggestion ->
                        val str = buildAnnotatedString {
                            val startIndex = suggestion.indexOf(
                                string = query.value,
                                ignoreCase = true
                            )
                            if (startIndex != -1) {
                                val endIndex = startIndex + query.value.length

                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onSurface
                                            .copy(alpha = TextFieldDefaults.IconOpacity)
                                    )
                                ) {
                                    append(suggestion.take(startIndex))
                                }
                                withStyle(style = SpanStyle()) {
                                    append(suggestion.substring(startIndex, endIndex))
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onSurface
                                            .copy(alpha = TextFieldDefaults.IconOpacity)
                                    )
                                ) {
                                    append(suggestion.drop(endIndex))
                                }
                            } else {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.onSurface
                                            .copy(alpha = TextFieldDefaults.IconOpacity)
                                    )
                                ) {
                                    append(suggestion)
                                }
                            }
                        }
                        // surface for hover effect
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    clearFocus()
                                    viewModel.onQueryChange(suggestion)
                                    viewModel.search()
                                }
                                .fillMaxWidth()
                                .padding(DefaultPadding)
                        ) {
                            Text(
                                text = str
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class SearchBarState {
    EMPTY, EXPANDED, WITH_QUERY
}
