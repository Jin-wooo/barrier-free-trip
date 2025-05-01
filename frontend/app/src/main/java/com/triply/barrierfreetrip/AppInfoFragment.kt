package com.triply.barrierfreetrip

import androidx.navigation.fragment.findNavController
import com.triply.barrierfreetrip.databinding.FragmentAppinfoBinding
import com.triply.barrierfreetrip.feature.BaseFragment


class AppInfoFragment : BaseFragment<FragmentAppinfoBinding>(R.layout.fragment_appinfo) {
    override fun initInViewCreated() {
        val navController = findNavController()
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }
        binding.tvAppinfoApi.setOnClickListener {
            navController.navigate(resId = R.id.appApiFragment)
        }
    }
}