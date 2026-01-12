package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.data.InfoListDto.InfoListItemDto
import com.triply.barrierfreetrip.data.InfoSquareListDto.InfoSquareItemDto
import com.triply.barrierfreetrip.databinding.ItemHomefragmentMainMenuBinding
import com.triply.barrierfreetrip.databinding.ItemHomefragmentTitleBinding
import com.triply.barrierfreetrip.databinding.ItemInfoListBinding
import com.triply.barrierfreetrip.databinding.ItemInfoSquareBinding

class HomeInfoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _infoList = arrayListOf<HomeInfoDTO>()
    val infoList: List<HomeInfoDTO>
        get() = _infoList

    fun setDataList(dataList: List<HomeInfoDTO>) {
        _infoList.clear()
        val newDataList = dataList.sortedWith(
            compareBy(
                { it.sortOrder() }
            )
        )
        _infoList.addAll(newDataList)
        notifyDataSetChanged()
    }

    private var onMenuClickListener: HomeMenuViewHolder.OnClickListener? = null
    private var onInfoSquareClickListener: OnItemClickListener? = null
    private var onInfoListClickListener: OnItemClickListener? = null

    fun setOnClickListener(onMenuClickListener: HomeMenuViewHolder.OnClickListener, onInfoSquareClickListener: OnItemClickListener, onInfoListClickListener: OnItemClickListener) {
        this.onMenuClickListener = onMenuClickListener
        this.onInfoSquareClickListener = onInfoSquareClickListener
        this.onInfoListClickListener = onInfoListClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEWTYPE_MAIN_MENU -> {
                HomeMenuViewHolder(ItemHomefragmentMainMenuBinding.inflate(layoutInflater, parent, false))
                    .apply {
                        setOnClickListener(onMenuClickListener)
                    }
            }
            VIEWTYPE_TITLE -> HomeTitleViewHolder(ItemHomefragmentTitleBinding.inflate(layoutInflater, parent, false))
            VIEWTYPE_NEARBY_STAY -> SquareViewHolder(ItemInfoSquareBinding.inflate(layoutInflater, parent, false))
                .apply {
                    setOnItemClickListener(onInfoSquareClickListener)
                    setLikeVisibility(false)
                }
            else -> ListViewHolder(ItemInfoListBinding.inflate(layoutInflater, parent, false)).apply {
                setOnItemClickListener(onInfoListClickListener)
                setLikeVisibility(false)
//                setShowMapVisibility(false)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (_infoList.getOrNull(position)) {
            is HomeInfoDTO.Menu -> VIEWTYPE_MAIN_MENU
            is HomeInfoDTO.InfoSquare -> VIEWTYPE_NEARBY_STAY
            is HomeInfoDTO.InfoList -> VIEWTYPE_NEARBY_CHARGER
            else -> VIEWTYPE_TITLE
        }
    }

    override fun getItemCount() = _infoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is HomeMenuViewHolder -> {}
            holder is HomeTitleViewHolder -> {
                holder.bind(
                    title = (_infoList.getOrNull(position) as HomeInfoDTO.Title).title
                )
            }
            holder is SquareViewHolder -> {
                holder.bind(
                    item = (_infoList.getOrNull(position) as HomeInfoDTO.InfoSquare).convertAdapterDataIntoDTO()
                )
            }
            holder is ListViewHolder -> {
                holder.setThumbnailIcon(R.drawable.ic_chargerlist_thumbnail)
                holder.bind(
                    item = (_infoList.getOrNull(position) as HomeInfoDTO.InfoList).convertAdapterDataIntoDTO()
                )
            }
        }
    }

    companion object {
        const val VIEWTYPE_MAIN_MENU = 0
        const val VIEWTYPE_TITLE = 1
        const val VIEWTYPE_NEARBY_STAY = 2
        const val VIEWTYPE_NEARBY_CHARGER = 3
    }

    sealed class HomeInfoDTO {
        object Menu: HomeInfoDTO()

        data class Title(
            val title: String
        ): HomeInfoDTO()

        data class InfoSquare(
            val addr: String,
            val contentId: String,
            val contentTypeId: String,
            val firstimg: String,
            val like: Boolean,
            val rating: String,
            val tel: String,
            val title: String
        ): HomeInfoDTO() {
            fun convertAdapterDataIntoDTO(): InfoSquareItemDto {
                return InfoSquareItemDto(
                    addr = this.addr,
                    contentId = this.contentId,
                    contentTypeId = this.contentTypeId,
                    firstimg = this.firstimg,
                    like = this.like,
                    rating = this.rating,
                    tel = this.tel,
                    title = this.title
                )
            }
        }

        data class InfoList(
            val id: Int,
            val addr: String,
            val like: Boolean,
            val tel: String,
            val title: String
        ): HomeInfoDTO() {
            fun convertAdapterDataIntoDTO(): InfoListItemDto {
                return InfoListItemDto(
                    id = this.id,
                    addr = this.addr,
                    like = this.like,
                    tel = this.tel,
                    title = this.title
                )
            }
        }
    }

    private fun HomeInfoDTO.sortOrder(): Int = when (this) {
        is HomeInfoDTO.Menu -> 0
        is HomeInfoDTO.Title -> {
            if (this.title == "내 주변 숙박시설") 1 else 3
        }
        is HomeInfoDTO.InfoSquare -> 2
        is HomeInfoDTO.InfoList -> 4
    }
}

class HomeMenuViewHolder(val binding: ItemHomefragmentMainMenuBinding): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnHomeCaretrip.setOnClickListener { onClickListener?.onHomeCareTripClick() }
        binding.btnHomeCharge.setOnClickListener { onClickListener?.onHomeChargeClick() }
        binding.btnHomeDestination.setOnClickListener { onClickListener?.onHomeDestinationClick() }
        binding.btnHomeRental.setOnClickListener { onClickListener?.onHomeRentalClick() }
        binding.btnHomeRestaurant.setOnClickListener { onClickListener?.onHomeRestaurantClick() }
        binding.btnHomeStay.setOnClickListener { onClickListener?.onHomeStayClick() }
    }

    private var onClickListener: OnClickListener? = null
    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    interface OnClickListener {
        fun onHomeCareTripClick()
        fun onHomeChargeClick()
        fun onHomeDestinationClick()
        fun onHomeRentalClick()
        fun onHomeRestaurantClick()
        fun onHomeStayClick()
    }
}

class HomeTitleViewHolder(private val binding: ItemHomefragmentTitleBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(title: String) {
        binding.tvNearbyFcltTitle.text = title
    }

    init {
        binding.btnFcltMore.visibility = View.GONE
    }
}