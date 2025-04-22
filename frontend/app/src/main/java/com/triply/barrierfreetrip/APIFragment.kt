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
        Pair(R.string.api_license_gps_title, R.string.api_license_gps_license),
        Pair(R.string.api_license_jjwt_title, R.string.api_license_jjwt_license),
        Pair(R.string.api_license_modelmapper_title, R.string.api_license_modelmapper_license),
        Pair(R.string.api_license_springboot_title, R.string.api_license_springboot_license)
    )

    override fun initInViewCreated() {
        val navController = findNavController()
        val apacheLicenseFullText = requireContext().resources.openRawResource(R.raw.apache_license_2).bufferedReader().use { it.readText() }

        with(binding) {
            ivApiBack.setOnClickListener {
                navController.navigateUp()
            }

            rvApi.adapter = APIAdapter().apply {
                apiList.forEach {
                    addAPIList(Pair(resources.getString(it.first), resources.getString(it.second)))
                }
                addAPIList(Pair(resources.getString(R.string.api_license_apache_title), apacheLicenseFullText))
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