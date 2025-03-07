package com.triply.barrierfreetrip

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.triply.barrierfreetrip.adapter.InfoSquareAdapter
import com.triply.barrierfreetrip.adapter.OnItemClickListener
import com.triply.barrierfreetrip.adapter.SearchHistoryAdapter
import com.triply.barrierfreetrip.adapter.decoration.StayListItemViewHolderDecoration
import com.triply.barrierfreetrip.databinding.FragmentSearchBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel
import com.triply.barrierfreetrip.model.SearchViewModel


class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: MainViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    private val TAG = "SearchFragment"

    override fun initInViewCreated() {

        // 검색결과 리사이클러뷰 세팅
        with(binding) {
            rvSearchList.adapter = InfoSquareAdapter()
            rvSearchList.layoutManager = GridLayoutManager(context, 2)
            if (rvSearchList.itemDecorationCount < 1) rvSearchList.addItemDecoration(StayListItemViewHolderDecoration())
        }

        // 검색키워드 리사이클러뷰 세팅
        with(binding) {
            rvKeywordList.adapter = SearchHistoryAdapter().apply {
                setOnDeleteClickListener(object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        searchViewModel.deleteSearchKeyword(infoList.getOrElse(position) { "" })
                    }
                })
                setOnItemClickListener(object: OnItemClickListener {
                    override fun onItemClick(position: Int) {

                    }
                })
            }
            rvKeywordList.layoutManager = LinearLayoutManager(context)
        }

        binding.btnDeletionOfAllSearchHistory.setOnClickListener {
            searchViewModel.deleteAllSearchKeyword()
        }

        viewModel.searchResult.observe(viewLifecycleOwner) { result ->
            if (result.isNullOrEmpty()) {
                binding.ivNoneData.isVisible = true
                Log.d(TAG, "no data from search api")
            } else {
                (binding.rvSearchList.adapter as InfoSquareAdapter).setDataList(result)
            }
            (binding.rvSearchList.adapter as InfoSquareAdapter).setOnItemClickListener(
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
//                        val itemId = result.getOrNull(position)?.id ?: 0
//                        val bundle = Bundle()
//                        val stayInfoFragment = StayInfoFragment()
//
//                        bundle.putString("id", itemId.toString())
//                        stayInfoFragment.arguments = bundle
//
//                        requireActivity().supportFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.main_nav_host_fragment, stayInfoFragment)
//                            .commit()
                    }
                }
            )
        }

        searchViewModel.searchKeywordList.observe(viewLifecycleOwner) { history ->
            if (history.isNullOrEmpty()) {
                binding.rvKeywordList.visibility = View.GONE
                binding.tvNoneRecentlySearchKeyword.visibility = View.VISIBLE
                binding.btnDeletionOfAllSearchHistory.visibility = View.GONE
            } else {
                binding.rvKeywordList.visibility = View.VISIBLE
                binding.tvNoneRecentlySearchKeyword.visibility = View.GONE
                binding.btnDeletionOfAllSearchHistory.visibility = View.VISIBLE
                (binding.rvKeywordList.adapter as SearchHistoryAdapter).setSearchHistory(history = history)
            }
        }

        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode in listOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SEARCH) && event.action == KeyEvent.ACTION_DOWN) {
                if (binding.etSearch.text.toString().isNotEmpty()) {
                    viewModel.getSearchResult(binding.etSearch.text.toString())
                    searchViewModel.addSearchKeyword(binding.etSearch.text.toString())
                }
            }
            true
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.imgbtnSearchBack.visibility = View.VISIBLE
            }
        }
        binding.imgbtnSearchBack.setOnClickListener {
            it.visibility = View.INVISIBLE
            val imeService = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imeService.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            binding.etSearch.clearFocus()
        }

        binding.etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        binding.clSearchHistoryContainer.visibility = View.VISIBLE
                    } else {
                        binding.clSearchHistoryContainer.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
    }
}