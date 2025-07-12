package com.ikseong.ucompass.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

object AnalyticsManager {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize() {
        if(this.firebaseAnalytics == null) {
            firebaseAnalytics = Firebase.analytics
        }
    }

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }
}