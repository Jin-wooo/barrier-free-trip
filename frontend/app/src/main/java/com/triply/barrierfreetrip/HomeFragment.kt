package com.triply.barrierfreetrip

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_ID
import com.triply.barrierfreetrip.MainActivity.Companion.CONTENT_TYPE
import com.triply.barrierfreetrip.adapter.HomeInfoAdapter
import com.triply.barrierfreetrip.adapter.HomeMenuViewHolder
import com.triply.barrierfreetrip.adapter.OnItemClickListener
import com.triply.barrierfreetrip.adapter.decoration.HomeListItemViewHolderDecoration
import com.triply.barrierfreetrip.data.InfoListDto
import com.triply.barrierfreetrip.data.InfoSquareListDto
import com.triply.barrierfreetrip.databinding.FragmentHomeBinding
import com.triply.barrierfreetrip.feature.BaseFragment
import com.triply.barrierfreetrip.model.MainViewModel
import com.triply.barrierfreetrip.util.CONTENT_TYPE_CARE
import com.triply.barrierfreetrip.util.CONTENT_TYPE_CHARGER
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RENTAL
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RESTAURANT
import com.triply.barrierfreetrip.util.CONTENT_TYPE_STAY
import com.triply.barrierfreetrip.util.CONTENT_TYPE_TOUR

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()
    private val homeInfoList = arrayListOf<HomeInfoAdapter.HomeInfoDTO>(HomeInfoAdapter.HomeInfoDTO.Menu)
    private val loadingProgressBar by lazy { BFTLoadingProgressBar(requireContext()) }
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun initInViewCreated() {
        val navController = findNavController()
        val bundle = Bundle()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        )

        with(binding.rvHome) {
            adapter = HomeInfoAdapter().apply {
                this.setOnClickListener(
                    onMenuClickListener = object : HomeMenuViewHolder.OnClickListener {

                        override fun onHomeCareTripClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_CARE)
                            navController.navigate(
                                resId = R.id.wishListFragment,
                                args = bundle
                            )
                        }

                        override fun onHomeChargeClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_CHARGER)
                            navController.navigate(
                                resId = R.id.wishListFragment,
                                args = bundle
                            )
                        }

                        override fun onHomeDestinationClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_TOUR)
                            navController.navigate(
                                resId = R.id.staylistFragment,
                                args = bundle
                            )
                        }

                        override fun onHomeRentalClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_RENTAL)
                            navController.navigate(
                                resId = R.id.wishListFragment,
                                args = bundle
                            )
                        }

                        override fun onHomeRestaurantClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_RESTAURANT)
                            navController.navigate(
                                resId = R.id.staylistFragment,
                                args = bundle
                            )
                        }

                        override fun onHomeStayClick() {
                            bundle.putString(CONTENT_TYPE, CONTENT_TYPE_STAY)
                            navController.navigate(
                                resId = R.id.staylistFragment,
                                args = bundle
                            )
                        }
                    },
                    onInfoSquareClickListener = object : OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val item = (adapter as HomeInfoAdapter).infoList.getOrNull(position) as HomeInfoAdapter.HomeInfoDTO.InfoSquare? ?: return

                            bundle.putString(CONTENT_ID, item.contentId)
                            navController.navigate(
                                resId = R.id.stayInfoFragment,
                                args = bundle
                            )
                        }
                    },
                    onInfoListClickListener = object : OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val item = (adapter as HomeInfoAdapter).infoList.getOrNull(position) as HomeInfoAdapter.HomeInfoDTO.InfoList? ?: return

                            bundle.putString(CONTENT_ID, item.id.toString())
                            navController.navigate(
                                resId = R.id.wishListMapFragment,
                                args = bundle
                            )
                        }
                    }
                )
            }
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when ((adapter as HomeInfoAdapter).getItemViewType(position)) {
                            HomeInfoAdapter.VIEWTYPE_NEARBY_STAY -> 1
                            else -> 2
                        }
                    }
                }
            }
            if (itemDecorationCount < 1) addItemDecoration(HomeListItemViewHolderDecoration())
        }


        viewModel.nearbyFcltList.observe(viewLifecycleOwner) { fcltList ->
            homeInfoList.clear()
            homeInfoList.add(HomeInfoAdapter.HomeInfoDTO.Menu)
            homeInfoList.add(HomeInfoAdapter.HomeInfoDTO.Title(title = resources.getString(R.string.all_stay_nearby)))
            homeInfoList.add(HomeInfoAdapter.HomeInfoDTO.Title(title = resources.getString(R.string.all_charge_nearby)))

            if (fcltList == null) {
                (binding.rvHome.adapter as HomeInfoAdapter).setDataList(homeInfoList)
                Log.d(TAG, "null near-hotel data")
                return@observe
            }
            fcltList.map { fclt ->
                when (fclt) {
                    is InfoSquareListDto.InfoSquareItemDto -> {
                        homeInfoList.add(
                            HomeInfoAdapter.HomeInfoDTO.InfoSquare(
                                addr = fclt.addr,
                                contentId = fclt.contentId,
                                contentTypeId = fclt.contentTypeId,
                                firstimg = fclt.firstimg,
                                like = fclt.like,
                                rating = fclt.rating,
                                tel = fclt.tel,
                                title = fclt.title
                            )
                        )
                    }

                    is InfoListDto.InfoListItemDto -> {
                        homeInfoList.add(
                            HomeInfoAdapter.HomeInfoDTO.InfoList(
                                id = fclt.id,
                                addr = fclt.addr,
                                like = fclt.like,
                                tel = fclt.tel,
                                title = fclt.title
                            )
                        )
                    }

                    else -> {}
                }
            }

            (binding.rvHome.adapter as HomeInfoAdapter).setDataList(homeInfoList)
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                loadingProgressBar.show()
            } else {
                loadingProgressBar.dismiss()
            }
        }
    }

    private val locationCallback : LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val previousLocation = currentLocation
            currentLocation = p0.lastLocation

            if (previousLocation != null) {
                val difference = currentLocation?.distanceTo(previousLocation) ?: 0f

                if (difference < 0.0001F) return
            }
            // 내 주변시설 API 호출
            viewModel.resetFacilityList()
            viewModel.getNearbyStayList(currentLocation?.longitude ?: 126.9778222, currentLocation?.latitude ?: 37.5664056)
            viewModel.getNearbyChargerList(currentLocation?.longitude ?: 126.9778222, currentLocation?.latitude ?: 37.5664056)
        }
    }

    fun startLocationUpdates() {
        val hasPermissionForCoarseLocation = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasPermissionForFineLocation = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasPermissionForCoarseLocation && hasPermissionForFineLocation) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).build()
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}