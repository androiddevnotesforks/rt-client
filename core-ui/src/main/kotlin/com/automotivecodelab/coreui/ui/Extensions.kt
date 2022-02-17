package com.automotivecodelab.coreui.ui

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(): String {
    return SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault())
        .format(this)
}
