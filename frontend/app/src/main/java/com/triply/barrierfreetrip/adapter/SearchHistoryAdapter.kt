package com.triply.barrierfreetrip.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.triply.barrierfreetrip.R
import com.triply.barrierfreetrip.databinding.ItemSearchKeywordBinding

class SearchHistoryAdapter : RecyclerView.Adapter<SearchKeywordViewHolder>() {
    private val _infoList = arrayListOf<String>()
    val infoList: List<String>
        get() = _infoList.toList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onDeleteClickListener: OnItemClickListener? = null

    fun setSearchHistory(history: List<String>) {
        this._infoList.clear()
        this._infoList.addAll(history)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchKeywordViewHolder {
        val binding = DataBindingUtil.inflate<ItemSearchKeywordBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_search_keyword, parent, false
        )
        return SearchKeywordViewHolder(binding).apply {
            setOnItemClickListener(onItemClickListener)
            setOnDeleteClickListener(onDeleteClickListener)
        }
    }

    override fun onBindViewHolder(holder: SearchKeywordViewHolder, position: Int) {
        holder.bind(_infoList[position])
    }
    override fun getItemCount() = _infoList.size

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.onItemClickListener = itemClickListener
    }

    fun setOnDeleteClickListener(deleteClickListener: OnItemClickListener) {
        this.onDeleteClickListener = deleteClickListener
    }
}

class SearchKeywordViewHolder(
    private val binding : ItemSearchKeywordBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var itemClickListener: OnItemClickListener? = null
    private var deleteClickListener: OnItemClickListener? = null

    init {
        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(adapterPosition)
        }
        binding.ivDeleteKeyword.setOnClickListener {
            deleteClickListener?.onItemClick(adapterPosition)
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    fun setOnDeleteClickListener(deleteClickListener: OnItemClickListener?) {
        this.deleteClickListener = deleteClickListener
    }

    fun bind(item: String) {
        binding.tvSearchKeyword.text = item
    }
}