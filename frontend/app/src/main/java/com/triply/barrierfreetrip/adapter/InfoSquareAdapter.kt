package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.data.InfoSquareListDto.InfoSquareItemDto
import com.triply.barrierfreetrip.databinding.ItemInfoSquareBinding
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RESTAURANT
import com.triply.barrierfreetrip.util.CONTENT_TYPE_TOUR

class InfoSquareAdapter : RecyclerView.Adapter<SquareViewHolder>() {
    private lateinit var binding : ItemInfoSquareBinding
    private val infoList = arrayListOf<InfoSquareItemDto>()
    private var onItemClickListener: OnItemClickListener? = null
    private var onLikeClickListener: OnLikeClickListener? = null
    private var contentTypeId = ""

    fun setDataList(infoList: List<InfoSquareItemDto>) {
        this.infoList.clear()
        this.infoList.addAll(infoList)
        notifyDataSetChanged()
    }

    /**
     * contentTypeId 콘텐츠 타입 ID. 관광지: "12", 음식점: "39", 숙박: "32".
     */
    fun setContentType(contentTypeId: String) {
        this.contentTypeId = contentTypeId
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SquareViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_info_square, parent, false
        )
        return SquareViewHolder(binding).apply {
            setOnItemClickListener(onItemClickListener)
            setLikeClickListener {
                onLikeClickListener?.onLikeClick(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: SquareViewHolder, position: Int) {
        holder.bind(
            item = infoList[position],
            emptyImgRes = when (contentTypeId) {
                CONTENT_TYPE_TOUR -> R.drawable.ic_tourlist_thumbnail_placeholder
                CONTENT_TYPE_RESTAURANT -> R.drawable.ic_restaurantlist_thumbnail_placeholder
                else -> R.drawable.ic_tourlist_thumbnail_placeholder
            }
        )
    }
    override fun getItemCount() = infoList.size

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.onItemClickListener = itemClickListener
    }

    fun setOnLikeClickListener(likeClickListener: OnLikeClickListener) {
        this.onLikeClickListener = likeClickListener
    }
}

class SquareViewHolder(
    private val binding : ItemInfoSquareBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var itemClickListener: OnItemClickListener? = null
    private var likeClickListener: OnClickListener? = null

    init {
        binding.ivPlaceImage.clipToOutline = true
        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(adapterPosition)
        }
        binding.tbSquareLike.setOnClickListener {
            likeClickListener?.onClick(it)
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    fun setLikeClickListener(likeClickListener: OnClickListener) {
        this.likeClickListener = likeClickListener
    }

    private var isLikeVisible = true
    fun setLikeVisibility(visibility: Boolean) {
        isLikeVisible = visibility
    }

    fun bind(item: InfoSquareItemDto, emptyImgRes: Int = R.drawable.ic_restaurantlist_thumbnail_placeholder) {
        binding.squareItem = item
        binding.tbSquareLike.visibility = if (isLikeVisible) View.VISIBLE else View.GONE
        binding.tvSquareAddress.text = item.addr.take(14)

        val thumbnail = item.firstimg.ifBlank { emptyImgRes }
        Glide.with(binding.root.context)
            .load(thumbnail)
            .centerCrop()
            .into(binding.ivPlaceImage)
    }
}