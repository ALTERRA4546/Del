package com.example.deliveryapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide

class PostomatListViewAdapter (context: Context, private val resource: Int, private val items: List<SupabaseManager.PostomatDecode>): ArrayAdapter<SupabaseManager.PostomatDecode>(context, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val postomat = items[position]
        var supabase = SupabaseManager()

        var imageView = view.findViewById<ImageView>(R.id.imageItemPostomat)

        Glide.with(view)
            .load(postomat.image)
            .into(imageView)

        view.findViewById<TextView>(R.id.idItemPostomat).text =  postomat.id.toString()
        view.findViewById<TextView>(R.id.titleItemPostomat).text = postomat.title
        view.findViewById<TextView>(R.id.addressItemPostomat).text = postomat.address
        view.findViewById<RatingBar>(R.id.startsItemPostomat).rating = postomat.stars


        /*deleteView.setOnClickListener() {
            activity.removeCartItem(cart.id)
        }*/

        return view
    }
}