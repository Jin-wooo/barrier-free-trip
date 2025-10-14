package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.data.InfoListDto.InfoListItemDto
import com.triply.barrierfreetrip.databinding.ItemInfoListBinding
import com.triply.barrierfreetrip.util.toDp

// 화면에 표시만 하면 되는 경우 사용하는 기본 Adapter
open class InfoListAdapter : RecyclerView.Adapter<ListViewHolder>() {
    private val infoList: ArrayList<InfoListItemDto> = arrayListOf()
    @DrawableRes private var icon: Int = 0
    fun setInfoList(listDto: List<InfoListItemDto>) {
        infoList.clear()
        infoList.addAll(listDto)
        notifyDataSetChanged()
    }

    fun setThumbnailIcon(iconRes: Int) {
        icon = iconRes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ListViewHolder(binding).apply {
            setOnItemClickListener(onItemClickListener)
            setOnLikeClickListener {
                onLikeClickListener?.onLikeClick(adapterPosition)
            }
//            setOnShowMapClickListener {
//                onShowMapClickListener?.onShowMapClick(adapterPosition)
//            }
//            setShowMapVisibility(isShowMapVisible)
        }
        return viewHolder
    }

    override fun getItemCount() = infoList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(infoList[position])
        holder.setThumbnailIcon(icon)
    }

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
    private var onLikeClickListener: OnLikeClickListener? = null
    fun setOnLikeClickListener(listener: OnLikeClickListener) {
        this.onLikeClickListener = listener
    }
//    private var onShowMapClickListener: OnShowMapClickListener? = null
//    fun setOnShowMapClickListener(listener: OnShowMapClickListener) {
//        this.onShowMapClickListener = listener
//    }
//    private var isShowMapVisible = false
//    fun setShowMapVisibility(visibility: Boolean) {
//        isShowMapVisible = visibility
//    }
}

// 충전기처럼 다른 페이지로 넘어가야 하는 경우 사용하는 Adapter
class InfoListClickAdapter(infoList: ArrayList<InfoListItemDto>) : InfoListAdapter() {

    private lateinit var itemClickListener: InfoListClickAdapter.OnItemClickListener

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        //holder.bind(infoList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}

class ListViewHolder(
    private val binding : ItemInfoListBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var itemClickListener: OnItemClickListener? = null
    private var likeClickListener: View.OnClickListener? = null
//    private var showMapClickListener: View.OnClickListener? = null
//    private var isShowMapVisible = false
    private var isLikeVisible = true

    fun setThumbnailIcon(iconRes: Int) {
        binding.ivListPlaceImg.setImageResource(iconRes)
        if (iconRes == R.drawable.ic_chargerlist_thumbnail) binding.ivListPlaceImg.setPadding(18F.toDp(binding.root.context).toInt())
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.itemClickListener = listener
    }

    fun setOnLikeClickListener(listener: View.OnClickListener?) {
        likeClickListener = listener
    }

//    fun setOnShowMapClickListener(listener: View.OnClickListener?) {
//        showMapClickListener = listener
//    }

    fun setLikeVisibility(visibility: Boolean) {
        isLikeVisible = visibility
    }

//    fun setShowMapVisibility(visibility: Boolean) {
//        isShowMapVisible = visibility
//    }

    fun bind(item: InfoListItemDto) {
        binding.tbListLike.isChecked = item.like
        binding.tvListPlaceName.text = item.title
        binding.tvListCallNumber.text = item.tel
        binding.tbListLike.isChecked = item.like
//        binding.btnChargerlistMap.visibility = if (isShowMapVisible) View.VISIBLE else View.GONE
        binding.tbListLike.visibility = if (isLikeVisible) View.VISIBLE else View.GONE
        binding.tvListLocation.text = item.addr.substring(0, item.addr.length.coerceAtMost(14))
    }

    init {
        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(adapterPosition)
        }
        binding.tbListLike.setOnClickListener {
            likeClickListener?.onClick(it)
        }
//        binding.btnChargerlistMap.setOnClickListener {
//            showMapClickListener?.onClick(it)
//        }
    }
}

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

interface OnLikeClickListener {
    fun onLikeClick(position: Int)
}

interface OnShowMapClickListener {
    fun onShowMapClick(position: Int)
}