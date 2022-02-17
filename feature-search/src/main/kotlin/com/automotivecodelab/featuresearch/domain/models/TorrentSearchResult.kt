package com.automotivecodelab.featuresearch.domain.models

import java.util.*

data class TorrentSearchResult(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val size: Long,
    val formattedSize: String,
    val seeds: Int,
    val leeches: Int,
    val url: String,
    val state: String,
    val downloads: Int,
    val registered: Date
)

enum class Sort(val value: String) {
    Registered("registered"),
    Title("title"),
    Downloads("downloads"),
    Size("size"),
    LastMessage("lastMessage"),
    Seeds("seeds"),
    Leeches("leeches")
}

enum class Order(val value: String) {
    Asc("asc"),
    Desc("desc")
}
