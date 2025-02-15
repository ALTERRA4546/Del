package com.example.deliveryapplication.ui.packages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.deliveryapplication.PackageListViewAdapter
import com.example.deliveryapplication.R
import com.example.deliveryapplication.SharedData
import com.example.deliveryapplication.SupabaseManager
import com.example.deliveryapplication.databinding.FragmentPackageBinding
import kotlinx.coroutines.launch

class PackagesFragment : Fragment() {
    lateinit var supabase: SupabaseManager
    private var _binding: FragmentPackageBinding? = null
    private lateinit var sharedData: SharedData

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val packagesViewModel =
            ViewModelProvider(this).get(PackagesViewModel::class.java)

        supabase = SupabaseManager()
        sharedData = ViewModelProvider(requireActivity()).get(SharedData::class.java)

        _binding = FragmentPackageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listView: ListView = binding.packageListView

        lifecycleScope.launch {
            listView.adapter = null
            var packageData = supabase.GetPackageMemory()
            var adapter = PackageListViewAdapter( requireContext(), R.layout.package_item_template, packageData)
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val selectedPostomat = packageData[position]

                sharedData.latitude = selectedPostomat.longitude.toDouble()
                sharedData.longitude = selectedPostomat.width.toDouble()
                sharedData.title = selectedPostomat.title
                sharedData.displaySinglePoint = true

                switchToMapFragment()
            }
        }

        return root
    }

    fun switchToMapFragment() {
        val navController = findNavController()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_package, true)
            .build()

        navController.navigate(R.id.navigation_map, null, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}