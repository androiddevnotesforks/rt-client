package com.automotivecodelab.featuredetails.domain.models

import com.google.gson.annotations.SerializedName

// reinventing HTML :)
sealed interface SDUIComponent {
    companion object {
        const val SDUI_VERSION = 1
    }
}

// "model" suffix for not to be confused with the same names composables
class SDUIImageModel(val url: String, val width: Int?, val height: Int?) : SDUIComponent
class SDUITextModel(val text: String, val fontWeight: SDUIFontWeight) : SDUIComponent
class SDUIColumnModel(val children: List<SDUIComponent>) : SDUIComponent
class SDUIRowModel(val children: List<SDUIComponent>) : SDUIComponent
class SDUIHiddenContentModel(
    val title: String,
    val children: List<SDUIComponent>
) : SDUIComponent
class SDUILinkModel(val url: String, val text: String?) : SDUIComponent
object SDUIDividerModel : SDUIComponent

enum class SDUIFontWeight {
    @SerializedName("thin") Thin,
    @SerializedName("regular") Regular,
    @SerializedName("bold") Bold
}
