package com.example.deliveryapplication.ui.postomat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.deliveryapplication.PackageListViewAdapter
import com.example.deliveryapplication.PostomatListViewAdapter
import com.example.deliveryapplication.R
import com.example.deliveryapplication.SharedData
import com.example.deliveryapplication.SupabaseManager
import com.example.deliveryapplication.databinding.FragmentPostomatBinding
import com.example.deliveryapplication.ui.map.MapFragment
import kotlinx.coroutines.launch

class PostomatFragment : Fragment() {
    lateinit var supabase: SupabaseManager
    private var _binding: FragmentPostomatBinding? = null
    private lateinit var sharedData: SharedData

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val postomatViewModel =
            ViewModelProvider(this).get(PostomatViewModel::class.java)

        supabase = SupabaseManager()
        sharedData = ViewModelProvider(requireActivity()).get(SharedData::class.java)

        _binding = FragmentPostomatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listView: ListView = binding.postomatListView

        lifecycleScope.launch {
            listView.adapter = null
            var postomatData = supabase.GetPostomatMemory()
            sharedData.postomatList = postomatData
            var adapter = PostomatListViewAdapter( requireContext(), R.layout.postomat_item_template, postomatData)
            listView.adapter = adapter

                listView.setOnItemClickListener { parent, view, position, id ->
                val selectedPostomat = postomatData[position]

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
            .setPopUpTo(R.id.navigation_postomat, true)
            .build()

        navController.navigate(R.id.navigation_map, null, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}