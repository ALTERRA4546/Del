package com.example.deliveryapplication

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.JacksonSerializer

class SupabaseManager {
    companion object {
        var packageMemoryData: MutableList<PackageData> = mutableListOf()
        var postomatMemoryData: MutableList<PostomatDecode> = mutableListOf()
    }

    val supabase = createSupabaseClient(
        supabaseUrl = "",
        supabaseKey = ""
    )
    {
        //install(Auth)
        install(Postgrest)
        defaultSerializer = JacksonSerializer()
    }

    suspend fun GetPostomat() {
        var result = supabase.from("postomat").select {}.decodeList<PostomatDecode>()

        postomatMemoryData.clear()

        postomatMemoryData.addAll(result)
    }

    suspend fun GetPackage () {
        var postomatTempData = supabase.from("postomat").select {}.decodeList<PostomatDecode>()
        var packageTempData = supabase.from("package").select {}.decodeList<PackageDecode>()

        packageMemoryData.clear()

        for (item in packageTempData) {
            val packageTextDataClass = PackageData(
                id = item.id,
                title = item.title,
                address = (postomatTempData.find { it.id == item.id_postomat })?.address ?: "",
                price = item.price,
                image = item.image,
                longitude = (postomatTempData.find { it.id == item.id_postomat })?.longitude ?: 0f,
                width = (postomatTempData.find{it.id == item.id_postomat})?.width ?: 0f
            )
            packageMemoryData.add(packageTextDataClass)
        }
    }

    fun GetPostomatMemory() : List<PostomatDecode> {
        return postomatMemoryData
    }

    fun GetPackageMemory() : List<PackageData> {
        return packageMemoryData
    }

    data class PostomatDecode (
        val id: Int,
        val title: String,
        val address: String,
        val image: String,
        val stars: Float,
        val longitude: Float,
        val width: Float
    )

    data class PackageDecode (
        val id: Int,
        val title: String,
        val id_postomat: Int,
        val price: Float,
        val image: String
    )

    data class PackageData (
        var id: Int,
        var title: String,
        var address: String,
        var price: Float,
        var image: String,
        val longitude: Float,
        val width: Float
    )
}