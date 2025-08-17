package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RESTAURANT
import com.triply.barrierfreetrip.util.CONTENT_TYPE_STAY
import com.triply.barrierfreetrip.util.CONTENT_TYPE_TOUR

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    private val item = arrayListOf<String>()
    fun setDataList(list: List<String>) {
        item.clear()
        item.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.item_img, parent, false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(img = item.getOrNull(position))
    }

    inner class PagerViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_imgs)

        init {
            imageView.clipToOutline = true
        }

        fun bind(img: String?) {
            val thumbnail = when (img) {
                CONTENT_TYPE_TOUR -> R.drawable.ic_tourlist_thumbnail_placeholder
                CONTENT_TYPE_STAY -> R.drawable.ic_tourlist_thumbnail_placeholder
                CONTENT_TYPE_RESTAURANT -> R.drawable.ic_restaurantlist_thumbnail_placeholder
                else -> img
            }
            Glide.with(imageView.context)
                .load(thumbnail)
                .into(imageView)
        }
    }
}