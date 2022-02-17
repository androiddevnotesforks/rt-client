package com.automotivecodelab.coreui.ui

class Event<T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContent(): T {
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T {
        return content
    }
}
