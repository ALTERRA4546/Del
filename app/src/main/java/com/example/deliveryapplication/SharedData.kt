package com.example.deliveryapplication

import androidx.lifecycle.ViewModel

class SharedData : ViewModel() {
    var latitude: Double? = null
    var longitude: Double? = null
    var title: String? = null
    var postomatList: List<SupabaseManager.PostomatDecode> = emptyList()
    var displaySinglePoint: Boolean = false

        companion object {
        var nightMode: Boolean = false
        var languagePosition: Int = -2
        var language: String = "en"
    }

    fun getNightMode(): Boolean {
        return nightMode
    }

    fun setNightMode(value: Boolean) {
        nightMode = value
    }

    fun getLanguagePosition(): Int {
        return languagePosition
    }

    fun setLanguagePosition(value: Int) {
        languagePosition = value
    }

    fun getLanguage(): String {
        return language
    }

    fun setLanguage(value: String) {
        language = value
    }
}