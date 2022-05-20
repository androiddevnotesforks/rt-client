package com.automotivecodelab.featuredetails.domain.models

data class TorrentDescription(
    val SDUIData: SDUIComponent?,
    val formattedSize: String,
    val timeAfterUpload: String,
    val downloads: String,
    val seeds: Int,
    val leeches: Int,
    val state: String,
    val threadId: String
)
