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
    viewModel: SearchViewModel,
    onMenuItemClick: () -> Unit
) {
    Surface(
        elevation = 4.dp, // top bar material design
        shape = RoundedCornerShape(DefaultCornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .padding(DefaultPadding)
            .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.toInt()) }
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            val query = viewModel.query.collectAsState()
            TextField(
                value = query.value,
                onValueChange = { viewModel.onQueryChange(it) },
                leadingIcon = {
                    IconButton(onClick = onMenuItemClick) {
                        Icon(
                            Icons.Filled.Menu,
                            "Menu",
                        )
                    }
                },
                trailingIcon = {
                    if (viewModel.searchBarState == SearchBarState.EXPANDED ||
                        viewModel.searchBarState == SearchBarState.WITH_QUERY
                    ) {
                        IconButton(onClick = { viewModel.onSearchBarCloseIconClicked() }) {
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
                    .onFocusChanged { focusState ->
                        viewModel.onSearchBarFocusChanged(focusState.isFocused)
                    }
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.onKeyboardSearchButtonClicked() }
                ),
                placeholder = { Text(stringResource(id = R.string.search)) }
            )
            val feedTitle = viewModel.feedIdWithTitle?.second
            if (feedTitle != null) {
                val contentColor = MaterialTheme.colors.onSurface.copy(
                    alpha = TextFieldDefaults.IconOpacity
                )
                Text(
                    modifier = Modifier.padding(DefaultPadding),
                    text = feedTitle,
                    style = MaterialTheme.typography.caption,
                    color = contentColor
                )
            }
            val chipRowLazyListState = rememberLazyListState()
            val suggestions = viewModel.searchSuggestions.collectAsState()
            if (viewModel.searchBarState == SearchBarState.EXPANDED) {
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
                                append(suggestion.substring(startIndex, endIndex))
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
                                .clickable { viewModel.onSuggestionClicked(suggestion) }
                                .fillMaxWidth()
                                .padding(DefaultPadding)
                        ) {
                            Text(text = str)
                        }
                    }
                }
            }
        }
    }
}

