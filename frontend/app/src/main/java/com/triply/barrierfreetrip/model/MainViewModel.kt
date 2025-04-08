package com.triply.barrierfreetrip.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.triply.barrierfreetrip.api.LocationInstance
import com.triply.barrierfreetrip.api.LoginInstance
import com.triply.barrierfreetrip.api.RetroInstance
import com.triply.barrierfreetrip.data.ChargerDetail
import com.triply.barrierfreetrip.data.InfoListDto
import com.triply.barrierfreetrip.data.InfoSquareListDto
import com.triply.barrierfreetrip.data.LogoutDto
import com.triply.barrierfreetrip.data.RegionListDto
import com.triply.barrierfreetrip.data.ReviewListDTO
import com.triply.barrierfreetrip.data.ReviewRegistrationDTO
import com.triply.barrierfreetrip.data.SearchRsltListDto
import com.triply.barrierfreetrip.data.TourFacilityDetail
import com.triply.barrierfreetrip.util.CONTENT_TYPE_CARE
import com.triply.barrierfreetrip.util.CONTENT_TYPE_CHARGER
import com.triply.barrierfreetrip.util.CONTENT_TYPE_RENTAL
import com.triply.barrierfreetrip.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel() : ViewModel() {
    private val retrofit = RetroInstance.createBFTApi("token")
    private val loginRetrofit = LoginInstance.getLoginApi()
    private val kakaoRetrofit = LocationInstance.getLocationApi()

    private val _nearbyStayList: MutableLiveData<List<InfoSquareListDto.InfoSquareItemDto>?> by lazy { MutableLiveData(emptyList()) }
    val nearbyStayList: LiveData<List<InfoSquareListDto.InfoSquareItemDto>?>
        get() = _nearbyStayList

    private val _nearbyChargerList: MutableLiveData<List<InfoListDto.InfoListItemDto>?> by lazy { MutableLiveData(emptyList()) }
    val nearbyChargerList: LiveData<List<InfoListDto.InfoListItemDto>?>
        get() = _nearbyChargerList

    private val _sidoCodes by lazy {
        MutableLiveData(listOf(RegionListDto.Region(code = "-1", name = "시도 선택")))
    }
    val sidoCodes: LiveData<List<RegionListDto.Region>>
        get() = _sidoCodes

    private val _sigunguCodes by lazy {
        MutableLiveData(
            listOf(
                RegionListDto.Region(
                    code = "-1",
                    name = "구군 선택"
                )
            )
        )
    }
    val sigunguCodes: LiveData<List<RegionListDto.Region>>
        get() = _sigunguCodes

    private val _locationList by lazy { MutableLiveData(listOf<InfoSquareListDto.InfoSquareItemDto>()) }
    val locationList: LiveData<List<InfoSquareListDto.InfoSquareItemDto>>
        get() = _locationList

    private val _locationDetail by lazy { MutableLiveData(TourFacilityDetail()) }
    val fcltDetail: LiveData<TourFacilityDetail>
        get() = _locationDetail

    private val _reviews by lazy { MutableLiveData(ReviewListDTO(0, emptyList())) }
    val reviews: LiveData<ReviewListDTO>
        get() = _reviews

    private val _isDataLoading by lazy { MutableLiveData(Event(false)) }
    val isDataLoading: LiveData<Event<Boolean>>
        get() = _isDataLoading

    private val _fcltList by lazy { MutableLiveData(listOf<InfoListDto.InfoListItemDto>()) }
    val fcltList: LiveData<List<InfoListDto.InfoListItemDto>>
        get() = _fcltList

    fun getNearbyStayList(userX: Double, userY: Double) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getStayList(userX = userX, userY = userY)

                if (response.isSuccessful) {
                    _nearbyStayList.value = if (response.body()?.respDocument is InfoSquareListDto) {
                        (response.body()?.respDocument as? InfoSquareListDto)?.items
                    } else {
                        null
                    }
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getNearbyChargerList(userX: Double, userY: Double) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getNearbyChargerList(userX = userX, userY = userY)

                if (response.isSuccessful) {
                    _nearbyChargerList.value = if (response.body()?.respDocument is InfoListDto) {
                        (response.body()?.respDocument as? InfoListDto)?.items
                    } else {
                        null
                    }
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSidoCode() {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getSidoCode()

                if (response.isSuccessful) {
                    _sidoCodes.value = if (response.body()!!.respDocument is RegionListDto) {
                        (response.body()?.respDocument as? RegionListDto)?.items ?: listOf(
                            RegionListDto.Region(
                                code = "-1",
                                name = "시도 선택"
                            )
                        )
                    } else {
                        listOf(
                            RegionListDto.Region(
                                code = "-1",
                                name = "시도 선택"
                            )
                        )
                    }
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSigunguCode(sidoCode: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getSigunguCode(sidoCode)

                if (response.isSuccessful) {
                    _sigunguCodes.value = if (response.body()!!.respDocument is RegionListDto) {
                        (response.body()?.respDocument as? RegionListDto)?.items ?: listOf(
                            RegionListDto.Region(
                                code = "-1",
                                name = "구군 선택"
                            )
                        )
                    } else {
                        listOf(
                            RegionListDto.Region(
                                code = "-1",
                                name = "구군 선택"
                            )
                        )
                    }
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getTourFcltList(type: String, areaCode: String, bigPlaceCode: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getTourFcltList(
                    typeId = type,
                    areaCode = areaCode,
                    bigPlaceCode = bigPlaceCode
                )

                if (response.isSuccessful) {
                    _locationList.value = if (response.body()?.respDocument is InfoSquareListDto) {
                        (response.body()?.respDocument as? InfoSquareListDto)?.items ?: emptyList()
                    } else emptyList()
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFcltDetail(contentId: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getTourFcltDetail(contentId)

                if (response.isSuccessful) {
                    _locationDetail.value = if (response.body()?.respDocument is TourFacilityDetail) {
                        (response.body()?.respDocument as? TourFacilityDetail) ?: TourFacilityDetail()
                    } else TourFacilityDetail()
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getReviews(contentId: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getReviews(contentId)

                if (response.isSuccessful) {
                    _reviews.value = if (response.body()?.respDocument is ReviewListDTO) {
                        (response.body()?.respDocument as? ReviewListDTO) ?: ReviewListDTO(0, emptyList())
                    } else ReviewListDTO(0, emptyList())
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postReview(contentId: String, rating: Double, content: String) {
        val review = ReviewRegistrationDTO(rating = rating, content = content)

        viewModelScope.launch {
            try {
                val response = bftRetrofit.postReview(contentId = contentId, body = review)

                if (response.isSuccessful) {
                    //
                    _isDataLoading.value = Event(true)
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCareTourList(sidoName: String, sigunguName: String) {
        viewModelScope.launch {
            try {
                val response =
                    bftRetrofit.getCareTourList(bigPlaceCode = sidoName, smallPlaceCode = sigunguName)

                if (response.isSuccessful) {
                    _fcltList.value = if (response.body()?.respDocument is InfoListDto) {
                        (response.body()?.respDocument as? InfoListDto)?.items ?: emptyList()
                    } else emptyList()
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getChargerList(sidoName: String, sigunguName: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getChargerList(sido = sidoName, sigungu = sigunguName)

                if (response.isSuccessful) {
                    _fcltList.value = if (response.body()?.respDocument is InfoListDto) {
                        (response.body()?.respDocument as? InfoListDto)?.items ?: emptyList()
                    } else emptyList()
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRentalServiceList(sidoName: String, sigunguName: String) {
        viewModelScope.launch {
            try {
                val response = bftRetrofit.getRentalServiceList(
                    bigPlaceCode = sidoName,
                    smallPlaceCode = sigunguName
                )

                if (response.isSuccessful) {
                    _fcltList.value = if (response.body()?.respDocument is InfoListDto) {
                        (response.body()?.respDocument as? InfoListDto)?.items ?: emptyList()
                    } else emptyList()
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _chargerInfo by lazy { MutableLiveData<ChargerDetail>() }
    val chargerInfo: LiveData<ChargerDetail>
        get() = _chargerInfo

    fun getChargerInfo(contentId: Long) {
        viewModelScope.launch {
            try {
                var longitude = 0.0
                var latitude = 0.0

                val response = bftRetrofit.getChargerDetail(contentId = contentId)

                if (response.isSuccessful) {
                    if (!(response.body()?.respDocument as? ChargerDetail)?.addr.isNullOrEmpty()) {
                        val locationCoordinate = withContext(Dispatchers.IO) {
                            kakaoRetrofit.getLocationCoordinate(address = (response.body()!!.respDocument as ChargerDetail).addr)
                                .body()?.documents?.get(0)
                        }
                        longitude = locationCoordinate?.longitude?.toDouble() ?: 0.0
                        latitude = locationCoordinate?.latitude?.toDouble() ?: 0.0
                    }
                    _chargerInfo.value = (response.body()?.respDocument as? ChargerDetail).apply {
                        this?.latitude = latitude
                        this?.longitude = longitude
                    } ?: ChargerDetail(
                        addr = "",
                        air = "",
                        holidayClose = "",
                        holidayOpen = "",
                        like = 0,
                        phoneCharge = "",
                        possible = "",
                        tel = "",
                        title = "",
                        weekdayClose = "",
                        weekdayOpen = "",
                        weekendClose = "",
                        weekendOpen = ""
                    ).apply {
                        this.latitude = latitude
                        this.longitude = longitude
                    }
                } else {
                    when (response.code()) {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postLikes(contentType: String, contentId: String, likes: Int) {
        val type = when (contentType) {
            CONTENT_TYPE_CHARGER -> 1
            CONTENT_TYPE_CARE -> 2
            CONTENT_TYPE_RENTAL -> 3
            else -> 0
        }
        viewModelScope.launch {
            try {
                _isDataLoading.value = Event(true)

                val response = bftRetrofit.postLikes(type = type, contentId = contentId, likes = likes)
                if (type == 1 && response.isSuccessful) {
                    val chargerInfoResponse =
                        bftRetrofit.getChargerDetail(contentId = contentId.toLong())
                    if (chargerInfoResponse.isSuccessful) {
                        _chargerInfo.value = _chargerInfo.value?.copy(
                            like = (chargerInfoResponse.body()?.respDocument as? ChargerDetail)?.like ?: 0
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isDataLoading.value = Event(false)
            }
        }
    }

    private val _searchResult by lazy { MutableLiveData<List<InfoSquareListDto.InfoSquareItemDto>?>(null) }
    val searchResult: LiveData<List<InfoSquareListDto.InfoSquareItemDto>?>
        get() = _searchResult

    fun getSearchResult(keyword: String) {
        viewModelScope.launch {
            try {
                _isDataLoading.value = Event(true)

                val response = bftRetrofit.getSearchResult(keyword = keyword)
                if (response.isSuccessful) {
                    _searchResult.value = if (response.body()?.respDocument is SearchRsltListDto) {
                        val respItem = (response.body()?.respDocument as? SearchRsltListDto)?.items ?: emptyList()
                        respItem.map {
                            InfoSquareListDto.InfoSquareItemDto(
                                addr = it.addr,
                                contentId = it.id.toString(),
                                contentTypeId = it.type.toString(),
                                firstimg = it.firstImage ?: "",
                                like = false,
                                rating = it.rating ?: "0.0",
                                tel = it.tel,
                                title = it.title
                            )
                        }
                    } else emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isDataLoading.value = Event(false)
            }
        }
    }

    fun clearSearchResult() {
        _searchResult.value = null
    }

    private val _logoutResult by lazy { MutableLiveData(false) }
    val logoutResult: LiveData<Boolean>
        get() = _logoutResult

    fun logout() {
        viewModelScope.launch {
            try {
                _isDataLoading.value = Event(true)

                val response = loginRetrofit.logout(LogoutDto("RefreshToken"))
                if (response.isSuccessful) {
                    _logoutResult.value = response.body()?.status == "success"
                } else {
                    _logoutResult.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isDataLoading.value = Event(false)
            }
        }
    }
}