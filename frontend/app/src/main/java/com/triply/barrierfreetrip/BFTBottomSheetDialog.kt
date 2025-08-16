package com.triply.barrierfreetrip

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewPropertyAnimator
import androidx.constraintlayout.widget.ConstraintLayout
import com.triply.barrierfreetrip.databinding.DialogChargerInfoBinding

class BFTBottomSheetDialog : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var _binding: DialogChargerInfoBinding? = null
    private val binding: DialogChargerInfoBinding
        get() = _binding!!

    init {
        _binding = DialogChargerInfoBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
    }

    /**
     * @param title 타이틀
     * @param officeHour 시설 운영 시간. 형식: "Open 00:00 | Close 23:59"
     * @param location 주소
     * @param callNumber 시설 전화번호
     * @param multiCharger (시설이 충전소인 경우) 동시충전개수. 충전소가 아니면 빈값으로 채워야 함
     * @param airChargerCapability (시설이 충전소인 경우) 공기주입가능여부. 충전소가 아니면 빈값으로 채워야 함
     * @param phoneChargerCapability (시설이 충전소인 경우) 휴대전화 충전가능여부. 충전소가 아니면 빈값으로 채워야 함
     * @param like 찜 현황
     */
    fun setDialogInfo(
        title: String = "",
        officeHour: String = "",
        location: String = "",
        callNumber: String = "",
        multiCharger: String = "1",
        airChargerCapability: String = "불가",
        phoneChargerCapability: String = "불가",
        like: Boolean = false
    ) {
        with(binding) {
            tvChargerinfoTitle.text = title
            tvChargerinfoOfficeHours.text = officeHour
            tvChargerinfoLocation.text = location
            tvChargerinfoCall.text = callNumber
            if (multiCharger.isEmpty()) llChargerinfoMultichargerContainer.visibility = GONE else tvChargerinfoMulticharger.text = multiCharger
            if (airChargerCapability.isEmpty()) llChargerinfoAirchargerContainer.visibility = GONE else tvChargerinfoAircharger.text = airChargerCapability
            if (phoneChargerCapability.isEmpty()) llChargerinfoPhonechargerContainer.visibility = GONE else tvChargerinfoPhonecharger.text = phoneChargerCapability
            tbSquareLike.isPressed = like
        }
    }

    fun updateLike(like: Boolean) {
        binding.tbSquareLike.isPressed = like
    }

    fun setOnLikeClick(
        onClickListener: OnClickListener
    ) {
        binding.tbSquareLike.setOnClickListener(onClickListener)
    }
}