package com.example.deliveryapplication

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PackageListViewAdapter(context: Context, private val resource: Int, private val items: List<SupabaseManager.PackageData>): ArrayAdapter<SupabaseManager.PackageData>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val packages = items[position]
        var supabase = SupabaseManager()

        var imageView = view.findViewById<ImageView>(R.id.imageItemPackage)

        Glide.with(view)
            .load(packages.image)
            .into(imageView)

        view.findViewById<TextView>(R.id.idItemPackage).text =  packages.id.toString()
        view.findViewById<TextView>(R.id.titleItemPackage).text = packages.title
        view.findViewById<TextView>(R.id.addressItemPackage).text = packages.address
        view.findViewById<TextView>(R.id.priceItemPackage).text = packages.price.toString()


        /*deleteView.setOnClickListener() {
            activity.removeCartItem(cart.id)
        }*/

        return view
    }
}