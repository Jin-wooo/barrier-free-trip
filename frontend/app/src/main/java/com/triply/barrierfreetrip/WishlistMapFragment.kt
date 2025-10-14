package com.triply.barrierfreetrip

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.MainActivity.Companion.PAGE_TITLE
import com.triply.barrierfreetrip.data.ChargerDetail
import com.triply.barrierfreetrip.databinding.FragmentWishlistMapBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel
import com.triply.barrierfreetrip.util.CONTENT_TYPE_CHARGER
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RESTAURANT
import com.triply.barrierfreetrip.util.CONTENT_TYPE_STAY
import com.triply.barrierfreetrip.util.CONTENT_TYPE_TOUR
import com.triply.barrierfreetrip.util.convertDrawableToBitmapIcon
import java.lang.System.currentTimeMillis


class WishlistMapFragment : BaseFragment<FragmentWishlistMapBinding>(R.layout.fragment_wishlist_map) {
    private val viewModel: MainViewModel by activityViewModels()
    private var kakaoMap: KakaoMap? = null
    private var pendingFacilityInfo: ChargerDetail? = null
    private var timeOnClickLike = currentTimeMillis()
    private val debouncingInterval = 300L
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }
    private var pageTitle: String? = null

    /**
     * @param itemId 시설 ID
     * @param itemTitle 시설명
     * @param itemOfficeOpenHour 시설 운영 시작 시간. 형식: "00:00"
     * @param itemOfficeCloseHour 시설 운영 마감 시간. 형식: "23:59"
     * @param itemAddr 시설 주소
     * @param itemTel 시설 전화번호
     * @param itemLike 찜 현황
     */
    private var itemId: String? = null
    private var itemTitle = ""
    private var itemOfficeOpenHour = ""
    private var itemOfficeCloseHour = ""
    private var itemAddr = ""
    private var itemTel = ""
    private var itemLike = 0
    private var itemLatitude = 0.0
    private var itemLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it
                .getString(CONTENT_ID)
                .toString()
            pageTitle = it
                .getString(PAGE_TITLE)
                .toString()
            itemTitle = it
                .getString(ITEM_TITLE)
                .toString()
            itemOfficeOpenHour = it
                .getString(ITEM_OFFICE_OPEN_HOUR)
                .toString()
            itemOfficeCloseHour = it
                .getString(ITEM_OFFICE_CLOSE_HOUR)
                .toString()
            itemAddr = it
                .getString(ITEM_ADDR)
                .toString()
            itemTel = it
                .getString(ITEM_TEL)
                .toString()
            itemLike = it
                .getInt(ITEM_LIKE)
            itemLatitude = it
                .getDouble(ITEM_LATITUDE)
            itemLongitude = it
                .getDouble(ITEM_LONGITUDE)
        }
    }

    override fun initInViewCreated() {
        val navController = findNavController()
        initMap()
        binding.tvTitle.text = pageTitle

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        when {
            pageTitle == resources.getString(R.string.title_charge) -> {
                viewModel.chargerInfo.observe(viewLifecycleOwner) { chargerInfo ->
                    if (chargerInfo == null) return@observe

                    binding.dialogMapInfo.setDialogInfo(
                        title = chargerInfo.title,
                        officeHour = "Open ${chargerInfo.weekdayOpen} | Close ${chargerInfo.weekdayClose}",
                        location = chargerInfo.addr.replace("  ", " "),
                        callNumber = chargerInfo.tel,
                        multiCharger = chargerInfo.possible,
                        airChargerCapability = if (chargerInfo.equals("N")) "불가" else "가능",
                        phoneChargerCapability = if (chargerInfo.equals("N")) "불가" else "가능",
                        like = chargerInfo.like == 1,
                    )
                    if (kakaoMap == null) {
                        pendingFacilityInfo = chargerInfo
                        return@observe
                    }
                    kakaoMap?.mapWidgetManager?.infoWindowLayer?.addInfoWindow(makeWidget(chargerInfo.title, chargerInfo.latitude, chargerInfo.longitude))
                    setCameraPosition(chargerInfo.latitude, chargerInfo.longitude)
                    binding.dialogMapInfo.setOnClickListener { _ ->
                        setCameraPosition(chargerInfo.latitude, chargerInfo.longitude)
                    }
                    binding.dialogMapInfo.setOnLikeClick {
                        if (currentTimeMillis() - timeOnClickLike < debouncingInterval) {
                            return@setOnLikeClick
                        }
                        timeOnClickLike = currentTimeMillis()
                        itemId?.let {
                            viewModel.postLikes(contentType = CONTENT_TYPE_CHARGER, contentId = it, likes = chargerInfo.like xor 1)
                            binding.dialogMapInfo.updateLike(like = chargerInfo.like xor 1 == 1)
                        }
                    }
                }
            }
            else -> {
                binding.dialogMapInfo.setDialogInfo(
                    title = itemTitle,
                    iconRes = when (pageTitle) {
                        resources.getString(R.string.title_charge) -> R.drawable.ic_chargerlist_thumbnail
                        resources.getString(R.string.all_stay) -> R.drawable.ic_stay_thumbnail
                        resources.getString(R.string.home_tour) -> R.drawable.ic_tour_thumbnail
                        else -> R.drawable.ic_restaunrant_thumbnail
                    },
                    officeHour = "Open $itemOfficeOpenHour | Close $itemOfficeCloseHour",
                    location = itemAddr,
                    callNumber = itemTel,
                    multiCharger = "",
                    airChargerCapability = "",
                    phoneChargerCapability = "",
                    like = itemLike == 1,
                )
                binding.dialogMapInfo.setOnClickListener { _ ->
                    setCameraPosition(itemLatitude, itemLongitude)
                }
                binding.dialogMapInfo.setOnLikeClick {
                    if (currentTimeMillis() - timeOnClickLike < debouncingInterval) {
                        return@setOnLikeClick
                    }
                    timeOnClickLike = currentTimeMillis()
                    itemId?.let {
                        viewModel.postLikes(
                            contentType = when (pageTitle) {
                                resources.getString(R.string.all_stay) -> CONTENT_TYPE_STAY
                                resources.getString(R.string.home_tour) -> CONTENT_TYPE_TOUR
                                else -> CONTENT_TYPE_RESTAURANT
                                                              },
                            contentId = it,
                            likes = itemLike xor 1
                        )
                        binding.dialogMapInfo.updateLike(like = itemLike xor 1 == 1)
                    }
                }
            }
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                loadingProgressBar.show()
            } else {
                loadingProgressBar.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapFacilityInfo.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapFacilityInfo.pause()
    }

    private fun initMap() {
        val mapLifeCycleCallback = object : MapLifeCycleCallback() {
            override fun onMapDestroy() {

            }

            override fun onMapError(e: Exception?) {
                e?.printStackTrace()
            }
        }
        val kakaoMapReadyCycleCallback = object: KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@WishlistMapFragment.kakaoMap = kakaoMap

                if (pageTitle == resources.getString(R.string.title_charge)) {
                    pendingFacilityInfo?.let {
                        this@WishlistMapFragment.kakaoMap
                            ?.mapWidgetManager
                            ?.infoWindowLayer
                            ?.addInfoWindow(
                                makeWidget(it.title, it.latitude, it.longitude)
                            )
                    }
                    itemId?.let {
                        viewModel.getChargerInfo(contentId = it.toLong())
                    }
                } else {
                    kakaoMap.mapWidgetManager
                        ?.infoWindowLayer
                        ?.addInfoWindow(
                            makeWidget(itemTitle, itemLatitude, itemLongitude)
                        )
                    setCameraPosition(latitude = itemLatitude, longitude = itemLongitude)
                }

            }
        }
        binding.mapFacilityInfo.start(mapLifeCycleCallback, kakaoMapReadyCycleCallback)
    }

    private fun makeWidget(title: String, latitude: Double, longitude: Double): InfoWindowOptions {
        val body = GuiLayout(Orientation.Horizontal)
        body.setPadding(10, 8, 10, 10)
        val background = GuiImage(R.drawable.shape_oval_white_stroke1_main_pink_widget, true)
        body.setBackground(background)

        val chargerIcon = GuiImage(convertDrawableToBitmapIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_charge_with_background)!!))
        body.addView(chargerIcon)

        val text = GuiText(" $title")
        text.setTextSize(24)
        body.addView(text)

        val options = InfoWindowOptions.from(LatLng.from(latitude, longitude))
        options.setBody(body)
        options.setBodyOffset(0f, -4F)
        return options
    }

    private fun setCameraPosition(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
        if (kakaoMap == null) {
            return
        }
        kakaoMap?.moveCamera(cameraUpdate)
    }

    companion object {
        const val ITEM_TITLE = "item_title"
        const val ITEM_OFFICE_OPEN_HOUR = "item_office_open_hour"
        const val ITEM_OFFICE_CLOSE_HOUR = "item_office_close_hour"
        const val ITEM_ADDR = "item_addr"
        const val ITEM_TEL = "item_tel"
        const val ITEM_LIKE = "item_like"
        const val ITEM_LATITUDE = "item_latitude"
        const val ITEM_LONGITUDE = "item_longitude"

    }
}