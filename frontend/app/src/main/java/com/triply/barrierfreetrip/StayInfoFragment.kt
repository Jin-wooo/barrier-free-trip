package com.triply.barrierfreetrip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_TITLE
import com.triply.barrierfreetrip.adapter.ConvenienceInfoAdapter
import com.triply.barrierfreetrip.adapter.ViewPagerAdapter
import com.triply.barrierfreetrip.adapter.decoration.ConvenienceInfoViewHolderDecoration
import com.triply.barrierfreetrip.data.ConvenienceInfoDTO
import com.triply.barrierfreetrip.databinding.FragmentStayInfoBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel
import com.triply.barrierfreetrip.util.convertHomepageToURL
import com.triply.barrierfreetrip.util.toUIString
import androidx.core.net.toUri
import com.triply.barrierfreetrip.MainActivity.Companion.PAGE_TITLE
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_ADDR
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_LATITUDE
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_LIKE
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_LONGITUDE
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_OFFICE_CLOSE_HOUR
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_OFFICE_OPEN_HOUR
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_TEL
import com.triply.barrierfreetrip.WishlistMapFragment.Companion.ITEM_TITLE

class StayInfoFragment : BaseFragment<FragmentStayInfoBinding>(R.layout.fragment_stay_info) {
    private val viewModel: MainViewModel by activityViewModels()
    private var contentId = ""
    private var contentTitle = ""
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(CONTENT_ID)?.let {
            contentId = it
        }
    }

    override fun initInViewCreated() {
        val navController = findNavController()
        with(binding.rvConvenienceInfo) {
            adapter = ConvenienceInfoAdapter()
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            if (itemDecorationCount < 1) addItemDecoration(ConvenienceInfoViewHolderDecoration())
        }

        binding.imgbtnBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnStayinfoReview.setOnClickListener {
            val bundle = Bundle()

            bundle.putString(CONTENT_ID, contentId)
            bundle.putString(CONTENT_TITLE, contentTitle)
            navController.navigate(
                resId = R.id.reviewFragment,
                args = bundle
            )
        }

        with(binding.vpStayinfo) {
            adapter = ViewPagerAdapter()
            registerOnPageChangeCallback(object: OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.indicatorVp.setCurrentIndicator(position)
                }
            })

            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        // call api
        contentId.let {
            viewModel.getFcltDetail(it)
            viewModel.getReviews(it)
        }
        viewModel.fcltDetail.observe(viewLifecycleOwner) { detail ->
            if (detail.contentId.isBlank()) return@observe
            contentTitle = detail.title

            (binding.vpStayinfo.adapter as ViewPagerAdapter).setDataList(detail.imgs)
            binding.indicatorVp.initIndicators(detail.imgs.size)

            binding.tvStayinfoPlaceName.text = detail.title.toUIString()
            binding.tvStayinfoRate.text = detail.rating.toUIString()
            binding.tvStayinfoLocation.text = detail.addr1.toUIString()
            binding.tvStayinfoEnterTime.text = detail.checkInTime.toUIString()
            binding.tvStayinfoLeaveTime.text = detail.checkOutTime.toUIString()
            binding.tvStayinfoIntroduce.text = detail.overview.toUIString()
            binding.tbToggleLike.isChecked = detail.like == 1

            val convenienceInfos = mutableListOf(
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_elevator), content = detail.elevator?.toUIString() ?: ""),
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_restroom), content = detail.restroom?.toUIString() ?: ""),
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_handicapetc), content = detail.handicapetc?.toUIString() ?: ""),
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_braileblock), content = detail.braileblock?.toUIString() ?: ""),
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_freeParking), content = detail.freeParking?.toUIString() ?: ""),
                ConvenienceInfoDTO(subject = ContextCompat.getString(requireContext(), R.string.stayinfo_publictransport), content = detail.publictransport?.toUIString() ?: ""),
            ).filter {
                it.content.isNotBlank()
            }
            binding.clStayinfoConvInfo.visibility = if (convenienceInfos.isEmpty()) View.GONE else View.VISIBLE
            (binding.rvConvenienceInfo.adapter as ConvenienceInfoAdapter).setInfoList(convenienceInfos)

            binding.tbToggleLike.setOnClickListener {
                viewModel.postLikes(
                    contentType = detail.contentTypeId,
                    contentId = detail.contentId,
                    likes = detail.like xor 1
                )
            }

            binding.btnStayinfoCall.setOnClickListener {
                val phoneNumber = if (detail.tel.contains(',')) detail.tel.split(',').getOrElse(0) { "" } else detail.tel
                val intent = Intent(Intent.ACTION_DIAL, "tel: $phoneNumber".toUri())
                startActivity(intent)
            }
            binding.btnStayinfoMap.setOnClickListener {
                val bundle = Bundle()

                bundle.putString(CONTENT_ID, it.toString())
                bundle.putString(PAGE_TITLE, when (detail.contentTypeId) {
                    "39" -> resources.getString(R.string.home_restaurant)
                    "12" -> resources.getString(R.string.home_destination)
                    else -> resources.getString(R.string.all_stay)
                })
                bundle.putString(ITEM_TITLE, detail.title)
                bundle.putString(ITEM_OFFICE_OPEN_HOUR, detail.checkInTime)
                bundle.putString(ITEM_OFFICE_CLOSE_HOUR, detail.checkOutTime)
                bundle.putString(ITEM_ADDR, detail.addr1)
                bundle.putString(ITEM_TEL, detail.tel)
                bundle.putInt(ITEM_LIKE, detail.like)
                bundle.putDouble(ITEM_LATITUDE, detail.latitude)
                bundle.putDouble(ITEM_LONGITUDE, detail.longitude)
                navController.navigate(
                    resId = R.id.wishListMapFragment,
                    args = bundle
                )
            }
            binding.btnStayinfoPage.setOnClickListener {
                try {
                    val homepageUrl = if (detail.homepage.first() == '<') convertHomepageToURL(detail.homepage) else detail.homepage
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        homepageUrl.toUri()
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        viewModel.reviews.observe(viewLifecycleOwner) {
            binding.btnStayinfoReview.text = getString(R.string.show_reviews, it.totalCnt)
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                loadingProgressBar.show()
            } else {
                loadingProgressBar.dismiss()
            }
        }
    }

    companion object {
        private const val TAG = "StayInfoFragment"
    }
}