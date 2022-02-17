package com.automotivecodelab.featuredetailsbottomsheet.ui

import com.automotivecodelab.featuredetailsbottomsheet.domain.models.SDUIComponent
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.TorrentDescription

internal data class TorrentDescriptionUIModel(
    val id: String,
    val SDUIData: SDUIComponent?,
    val isWrongSDUIVersion: Boolean,
    val formattedSize: String?,
    val timeAfterUpload: String?,
    val downloads: String?,
    val seeds: Int?,
    val leeches: Int?,
    val state: String?,
    val category: String?,
    val author: String,
    val title: String,
    val url: String,
    val threadId: String?
)

internal fun TorrentDescription.toUIModel(
    id: String,
    category: String?,
    author: String,
    title: String,
    url: String,
    isWrongSDUIVersion: Boolean
) = TorrentDescriptionUIModel(
    id = id,
    SDUIData = SDUIData,
    formattedSize = formattedSize,
    timeAfterUpload = timeAfterUpload,
    downloads = downloads,
    seeds = seeds,
    leeches = leeches,
    state = state,
    threadId = threadId,
    category = category,
    author = author,
    title = title,
    url = url,
    isWrongSDUIVersion = isWrongSDUIVersion
)
