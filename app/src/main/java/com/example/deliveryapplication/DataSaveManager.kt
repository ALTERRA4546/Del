package com.example.deliveryapplication

import android.content.Context
import android.content.SharedPreferences

class DataSaveManager(private val context: Context) {
    val dataBaseName = "SettingsDeliveryApplication"

    fun saveString(key: String, value: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun loadString(key: String): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun saveBoolean(key: String, value: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun loadBoolean(key: String): Boolean? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        return if (sharedPreferences.contains(key)) {
            sharedPreferences.getBoolean(key, false)
        } else {
            null
        }
    }

    fun saveInt(key: String, value: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun loadInt(key: String): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(dataBaseName, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, -2)
    }
}