package com.automotivecodelab.featuredetailsbottomsheet.domain.models

import com.google.gson.annotations.SerializedName

// reinventing HTML :)
sealed interface SDUIComponent {
    companion object {
        const val SDUI_VERSION = 1
    }
}

// "model" suffix for not to be confused with the same names composables
internal class SDUIImageModel(val url: String, val width: Int?, val height: Int?) : SDUIComponent
internal class SDUITextModel(val text: String, val fontWeight: SDUIFontWeight) : SDUIComponent
internal class SDUIColumnModel(val children: List<SDUIComponent>) : SDUIComponent
internal class SDUIRowModel(val children: List<SDUIComponent>) : SDUIComponent
internal class SDUIHiddenContentModel(
    val title: String,
    val children: List<SDUIComponent>
) : SDUIComponent
internal class SDUILinkModel(val url: String, val text: String?) : SDUIComponent
internal object SDUIDividerModel : SDUIComponent

internal enum class SDUIFontWeight {
    @SerializedName("thin") Thin,
    @SerializedName("regular") Regular,
    @SerializedName("bold") Bold
}
