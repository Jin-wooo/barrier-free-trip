package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triply.barrierfreetrip.databinding.ItemApiLicenseBinding

class APIAdapter: RecyclerView.Adapter<APIAdapter.APIViewHolder>() {
    private val apiList = mutableListOf<Pair<String, String>>()

    fun addAPIList(api: Pair<String, String>) {
        this.apiList.add(api)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): APIViewHolder {
        return APIViewHolder(
            ItemApiLicenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = apiList.size

    override fun onBindViewHolder(holder: APIViewHolder, position: Int) {
        holder.bind(apiList.getOrElse(position) { Pair("", "") })
    }

    inner class APIViewHolder(private val binding: ItemApiLicenseBinding) : ReviewViewHolder(binding.root) {
        fun bind(data: Pair<String, String>) {
            binding.tvApi.text = data.first
            binding.tvLicense.text = data.second
        }
    }
}