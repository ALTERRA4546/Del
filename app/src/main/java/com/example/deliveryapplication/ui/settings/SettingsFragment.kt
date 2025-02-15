package com.example.deliveryapplication.ui.settings

import android.R
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.deliveryapplication.DataSaveManager
import com.example.deliveryapplication.SharedData
import com.example.deliveryapplication.databinding.FragmentSettingsBinding
import com.example.deliveryapplication.ui.packages.PackagesViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private var currentLanguage: String? = null
    private lateinit var dataSaveManager: DataSaveManager
    private lateinit var sharedData: SharedData

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        dataSaveManager = DataSaveManager(requireContext())
        sharedData = ViewModelProvider(this).get(SharedData::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val languageSpinner: Spinner = binding.languageSettings
        val nightModeSwitch: Switch = binding.nightModeSettings

        val supportedLanguages = listOf(
            Locale("en", "English"),
            Locale("ru", "Русский"),
            Locale("de", "Deutsch")
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, supportedLanguages.map { it.displayLanguage })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        currentLanguage = Locale.getDefault().language

        var currentIndex = -1
        if(sharedData.getLanguagePosition() == -2) {
            currentIndex = supportedLanguages.indexOfFirst { it.language == currentLanguage }
        }
        else {
            currentIndex = sharedData.getLanguagePosition()
        }

        if (currentIndex != -1) {
            languageSpinner.setSelection(currentIndex)
        }

        nightModeSwitch.isChecked = sharedData.getNightMode()

        lifecycleScope.launch {
            languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLanguage = supportedLanguages[position].language
                    if (selectedLanguage != currentLanguage) {
                        currentLanguage = selectedLanguage

                        fun saveString(context: Context, key: String, value: String) {
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(key, value)
                            editor.apply()
                        }

                        dataSaveManager.saveInt("language_position", position)
                        dataSaveManager.saveString("language", selectedLanguage)
                        sharedData.setLanguagePosition(position)
                        sharedData.setLanguage(selectedLanguage)

                        setLocale(selectedLanguage)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        lifecycleScope.launch {
            nightModeSwitch.setOnCheckedChangeListener { _, value ->
                lifecycleScope.launch {
                    dataSaveManager.saveBoolean("night_mode", value)
                    sharedData.setNightMode(value)

                    when (value) {
                        true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }

        return root
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = requireActivity().resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}