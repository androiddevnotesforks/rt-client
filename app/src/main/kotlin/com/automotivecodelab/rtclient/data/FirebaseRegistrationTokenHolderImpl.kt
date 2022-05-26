package com.automotivecodelab.rtclient.data

import com.automotivecodelab.corenetwork.data.FirebaseRegistrationTokenHolder
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class FirebaseRegistrationTokenHolderImpl @Inject constructor() : FirebaseRegistrationTokenHolder {
    // bridging callbacks and coroutines
    override suspend fun get() = suspendCoroutine<String> { continuation ->
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.d("Fetching FCM registration token failed", task.exception)
                throw task.exception!!
            }
            val token = requireNotNull(task.result) { "token == null" }
            continuation.resume(token)
        }
    }
}
