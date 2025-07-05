package com.triply.barrierfreetrip.api
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.triply.barrierfreetrip.data.ChargerDetail
import com.triply.barrierfreetrip.data.ErrorDto
import com.triply.barrierfreetrip.data.InfoListDto
import com.triply.barrierfreetrip.data.InfoSquareListDto
import com.triply.barrierfreetrip.data.LoginDto
import com.triply.barrierfreetrip.data.MetaResponse
import com.triply.barrierfreetrip.data.RefreshResponse
import com.triply.barrierfreetrip.data.RegionListDto
import com.triply.barrierfreetrip.data.RespDocument
import com.triply.barrierfreetrip.data.ReviewListDTO
import com.triply.barrierfreetrip.data.SearchRsltListDto
import com.triply.barrierfreetrip.data.TourFacilityDetail
import java.lang.reflect.Type
import kotlin.reflect.full.primaryConstructor

class RetrofitDeserializer : JsonDeserializer<MetaResponse<*>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MetaResponse<*> {
        val jsonObject = json?.asJsonObject
        val status = jsonObject?.get("status")?.asString ?: "error"
        return if (status == "success") {
            val data = jsonObject?.getAsJsonObject("data")
            when (data?.size()) {
                0 -> MetaResponse(status = status, respDocument = null, message = null)
                1 -> {
                    if (data.has("items")) {
                        // 응답 결과가 리스트일 경우
                        if (data.getAsJsonArray("items")?.isEmpty == true) {
                            // 응답 결과가 비어있을 경우 null
                            MetaResponse(status = status, respDocument = null, message = null)
                        } else {
                            // 응답 결과 리스트가 비어있지 않을 경우
                            val itemData = data.getAsJsonArray("items")?.get(0)?.asJsonObject
                            when (itemData?.keySet()) {
                                InfoListDto.InfoListItemDto::class.primaryConstructor?.parameters?.map {it.name}?.toSet() -> {
                                    val parsedData = context?.deserialize<InfoListDto>(data, InfoListDto::class.java)
                                    MetaResponse(status = status, respDocument = parsedData, message = null)
                                }
                                InfoSquareListDto.InfoSquareItemDto::class.primaryConstructor?.parameters?.map {it.name}?.toSet() -> {
                                    val parsedData = context?.deserialize<InfoSquareListDto>(data, InfoSquareListDto::class.java)
                                    MetaResponse(status = status, respDocument = parsedData, message = null)
                                }
                                SearchRsltListDto.SearchRsltItem::class.primaryConstructor?.parameters?.map {it.name}?.toSet() -> {
                                    val parsedData = context?.deserialize<SearchRsltListDto>(data, SearchRsltListDto::class.java)
                                    MetaResponse(status = status, respDocument = parsedData, message = null)
                                }
                                RegionListDto.Region::class.primaryConstructor?.parameters?.map {it.name}?.toSet() -> {
                                    val parsedData = context?.deserialize<RegionListDto>(data, RegionListDto::class.java)
                                    MetaResponse(status = status, respDocument = parsedData, message = null)
                                }
                                ReviewListDTO.ReviewDTO::class.primaryConstructor?.parameters?.map {it.name}?.toSet() -> {
                                    val parsedData = context?.deserialize<ReviewListDTO>(data, ReviewListDTO::class.java)
                                    MetaResponse(status = status, respDocument = parsedData, message = null)
                                }
                                else -> {
                                    throw JsonParseException("Json 파싱 오류")
                                }
                            }
                        }
                    } else {
//                        // 응답 결과가 없을 경우("emptyResponse")
//                        MetaResponse(status = status, respDocument = ErrorDto(errorCode = ""), message = null)

                        if (data.has("accessToken")) { // 응답 결과가 Login 결과값인 경우
                            val parsedData = context?.deserialize<LoginDto>(data, LoginDto::class.java)
                            MetaResponse(status = status, respDocument = parsedData, message = null)
                        } else { // 응답 결과가 없을 경우("emptyResponse")
                            MetaResponse(status = status, respDocument = ErrorDto(errorCode = ""), message = null)
                        }
                    }
                }
                else -> {
                    // 응답 결과가 단건인 경우
                    val keySet = data?.keySet() ?: emptySet()
                    when {
                        keySet.containsAll(TourFacilityDetail::class.primaryConstructor?.parameters?.map { it.name }?.toSet() ?: emptySet()) -> {
                            val parsedData = context?.deserialize<TourFacilityDetail>(data, TourFacilityDetail::class.java)
                            MetaResponse(status = status, respDocument = parsedData, message = null)
                        }
                        keySet.containsAll(ChargerDetail::class.primaryConstructor?.parameters?.map { it.name }?.toSet() ?: emptySet()) -> {
                            val parsedData = context?.deserialize<ChargerDetail>(data, ChargerDetail::class.java)
                            MetaResponse(status = status, respDocument = parsedData, message = null)
                        }
                        keySet.containsAll(RefreshResponse::class.primaryConstructor?.parameters?.map { it.name }?.toSet() ?: emptySet()) -> {
                            val parsedData = context?.deserialize<RefreshResponse>(data, RefreshResponse::class.java)
                            MetaResponse(status = status, respDocument = parsedData, message = null)
                        }
                        keySet.containsAll(LoginDto::class.primaryConstructor?.parameters?.map { it.name }?.toSet() ?: emptySet()) -> {
                            val parsedData = context?.deserialize<LoginDto>(data, LoginDto::class.java)
                            MetaResponse(status = status, respDocument = parsedData, message = null)
                        }
                        else -> {
                            throw JsonParseException("Json 파싱 오류")
                        }
                    }
                }
            }
        } else {
            val message = jsonObject?.get("message")?.asString ?: ""
            val data = jsonObject?.getAsJsonObject("data")
            val parsedData = context?.deserialize<RespDocument>(data, ErrorDto::class.java) ?: ErrorDto("404")
            if (parsedData is ErrorDto) {
                MetaResponse(status = status, respDocument = parsedData, message = message)
            } else {
                throw JsonParseException("Json 파싱 오류")
            }
        }
    }
}