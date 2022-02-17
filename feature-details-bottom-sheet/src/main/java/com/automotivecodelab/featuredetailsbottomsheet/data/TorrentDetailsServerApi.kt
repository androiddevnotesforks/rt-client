package com.automotivecodelab.featuredetailsbottomsheet.data

import com.automotivecodelab.featuredetailsbottomsheet.domain.models.*
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TorrentDetailsServerApi {

    companion object {
        fun getGsonConverterFactory(): GsonConverterFactory {
            val adapterFactory = RuntimeTypeAdapterFactory
                .of(SDUIComponent::class.java)
                .registerSubtype(SDUIImageModel::class.java, "image")
                .registerSubtype(SDUITextModel::class.java, "text")
                .registerSubtype(SDUIColumnModel::class.java, "column")
                .registerSubtype(SDUIRowModel::class.java, "row")
                .registerSubtype(SDUIHiddenContentModel::class.java, "hiddenContent")
                .registerSubtype(SDUILinkModel::class.java, "link")
                .registerSubtype(SDUIDividerModel::class.java, "divider")

            val gson = GsonBuilder()
                .registerTypeAdapterFactory(adapterFactory)
                .create()

            return GsonConverterFactory.create(gson)
        }
    }

    @GET("torrent/description")
    suspend fun getTorrentDescription(
        @Query("id") torrentId: String,
        @Query("sduiversion") SDUIversion: Int
    ): TorrentDescription
}
