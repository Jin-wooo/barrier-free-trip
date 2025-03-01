package com.triply.barrierfreetrip

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.triply.barrierfreetrip.adapter.InfoSquareAdapter
import com.triply.barrierfreetrip.adapter.OnItemClickListener
import com.triply.barrierfreetrip.adapter.decoration.StayListItemViewHolderDecoration
import com.triply.barrierfreetrip.databinding.FragmentSearchBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel


class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: MainViewModel by viewModels()

    private val TAG = "SearchFragment"

    override fun initInViewCreated() {
        with(binding) {
            rvSearchList.adapter = InfoSquareAdapter()
            rvSearchList.layoutManager = GridLayoutManager(context, 2)
            if (rvSearchList.itemDecorationCount < 1) rvSearchList.addItemDecoration(StayListItemViewHolderDecoration())
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

        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode in listOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SEARCH) && event.action == KeyEvent.ACTION_DOWN) {
                if (binding.etSearch.text.toString().isNotEmpty()) {
                    viewModel.getSearchResult(binding.etSearch.text.toString())
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
    }
}