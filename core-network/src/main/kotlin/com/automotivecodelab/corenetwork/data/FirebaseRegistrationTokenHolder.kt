package com.automotivecodelab.corenetwork.data

fun interface FirebaseRegistrationTokenHolder {
    suspend fun get(): String
}
