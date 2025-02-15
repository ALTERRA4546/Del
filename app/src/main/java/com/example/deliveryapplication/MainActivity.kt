package com.example.deliveryapplication

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var sharedData: SharedData
    private lateinit var dataSaveManager: DataSaveManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedData = ViewModelProvider(this).get(SharedData::class.java)
        dataSaveManager = DataSaveManager(this)

        val supportedLanguages = listOf(
            Locale("en", "English"),
            Locale("ru", "Русский"),
            Locale("de", "Deutsch")
        )

        var screen = findViewById<ImageView>(R.id.logoSplashScreen)
        var noInternet = findViewById<TextView>(R.id.noInternetSplashScreen)

        screen.alpha = 0f
        screen.animate().setDuration(1000).alpha(1f).withEndAction() {}

        lifecycleScope.launch {
            if (!isInternetAvailable(this@MainActivity)) {
                noInternet.visibility = View.VISIBLE
                noInternet.alpha = 0f
                noInternet.animate().setDuration(2500).alpha(1f).withEndAction() {}
            }
            else {
                var loadSavedData = lifecycleScope.launch {
                    var locale = getCurrentLocale(this@MainActivity)

                    sharedData.setNightMode(
                        if (dataSaveManager.loadBoolean("night_mode") == false) getCurrentNightMode(
                            this@MainActivity
                        ) else false
                    )
                    sharedData.setLanguagePosition(if (dataSaveManager.loadInt("language_position") == -2) supportedLanguages.indexOfFirst { it.language == locale.language } else dataSaveManager.loadInt(
                        "language_position"
                    ))
                    sharedData.setLanguage(
                        if (dataSaveManager.loadString("language") == null) locale.language else dataSaveManager.loadString(
                            "language"
                        )!!
                    )
                }

                var loadSupabase = lifecycleScope.launch {
                    var supabase = SupabaseManager()
                    supabase.GetPackage()
                    delay(250)
                    supabase.GetPostomat()
                }

                joinAll(loadSavedData, loadSupabase)

                setLocale(sharedData.getLanguage())
                when (sharedData.getNightMode()) {
                    true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                startActivity(Intent(this@MainActivity, Home::class.java))
                finish()
            }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun getCurrentNightMode(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    }

    fun getCurrentLocale(context: Context): Locale {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
        recreate()
    }
}