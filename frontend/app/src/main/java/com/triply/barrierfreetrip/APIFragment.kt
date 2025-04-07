package com.triply.barrierfreetrip

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.triply.barrierfreetrip.adapter.APIAdapter
import com.triply.barrierfreetrip.adapter.decoration.ReviewViewHolderDecoration
import com.triply.barrierfreetrip.databinding.FragmentApiBinding
import com.triply.barrierfreetrip.feature.BaseFragment

class APIFragment : BaseFragment<FragmentApiBinding>(R.layout.fragment_api) {
    private val apiList = listOf(
        Pair(R.string.api_license_retrofit_title, R.string.api_license_retrofit_license),
        Pair(R.string.api_license_glide_title, R.string.api_license_glide_license),
        Pair(R.string.api_license_andx_title, R.string.api_license_andx_license),
        Pair(R.string.api_license_kakaomap_title, R.string.api_license_kakaomap_license),
        Pair(R.string.api_license_coroutine_title, R.string.api_license_coroutine_license),
        Pair(R.string.api_license_gps_title, R.string.api_license_gps_license)
    )

    override fun initInViewCreated() {
        val navController = findNavController()

        with(binding) {
            ivApiBack.setOnClickListener {
                navController.navigateUp()
            }

            // 리뷰 리사이클러뷰 설정
            rvApi.adapter = APIAdapter().apply {
                apiList.forEach {
                    addAPIList(Pair(resources.getString(it.first), resources.getString(it.second)))
                }
            }
            rvApi.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            if (rvApi.itemDecorationCount == 0) {
                rvApi.addItemDecoration(ReviewViewHolderDecoration())
            }
        }
    }
}