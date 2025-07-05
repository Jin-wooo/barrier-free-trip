package com.triply.barrierfreetrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.triply.barrierfreetrip.api.LoginInstance
import com.triply.barrierfreetrip.data.LoginParameter
import com.triply.barrierfreetrip.databinding.ActivityLoginBinding
import com.triply.barrierfreetrip.feature.ApikeyStoreModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var binding : ActivityLoginBinding

    private val splashScreen by lazy { installSplashScreen() }
    private val progressDialog by lazy { ProgressDialogFragment() }
    private val loginApi by lazy { LoginInstance.loginAPI }
    private lateinit var apikeyStoreModule: ApikeyStoreModule

    private val LifecycleOwner.lifecycleScope : LifecycleCoroutineScope
        get() = lifecycle.coroutineScope

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()

        when (throwable) {
            is SocketException -> Log.d("logFish", "SocketException")
            is HttpException -> {
                Log.d("logFish", "HttpException: " +
                        "code = ${throwable.code()}, " +
                        "message = ${throwable.message()}, " +
                        "error = ${throwable.response()?.errorBody()?.string()}")
            }
            is UnknownHostException -> Log.d("logFish", "UnknownHostException")
            else -> Log.d("logFish", "Exception")
        }
    }

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공dsa ${token.accessToken}")
            UserApiClient.instance.me { user, error ->
                if (user == null) {
                    Log.e(TAG, "user == null : $error")
                    return@me
                }
                lifecycleScope.launch(exceptionHandler) {
                    val result = loginApi.getToken(
                        LoginParameter(
                            user.id.toString(),
                            user.kakaoAccount?.email!!,
                            user.kakaoAccount?.profile?.nickname!!
                        )
                    )

                    if (result.isSuccessful) {
                        val mainIntent = Intent(applicationContext, MainActivity::class.java)
                        val realToken = result.body()!!.respDocument?.accessToken
                        val refreshToken = result.body()!!.respDocument?.refreshToken
                        if (realToken != null) { apikeyStoreModule.setAccessToken(realToken) }
                        if (refreshToken != null) { apikeyStoreModule.setRefreshToken(refreshToken) }

                        mainIntent.putExtra("token", realToken)
                        startActivity(mainIntent)
                        progressDialog.dismiss()
                    }
                }
            }

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private suspend fun goMainScreen() {

    }

    private var lastClickTime = 0L
    private var clickTime = 0L

    // 자꾸 카카오톡 자체를 실행시키는 게 아니라 카카오톡 웹페이지로 넘어감
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        splashScreen.setKeepOnScreenCondition {true}
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.btnLoginKakao.setOnClickListener {
            clickTime = System.currentTimeMillis()
            val elapsedTime = clickTime - lastClickTime
            lastClickTime = clickTime
            if (elapsedTime < 3000) {
                return@setOnClickListener
            }
            progressDialog.show(supportFragmentManager)
            // 카카오톡 설치 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    // 로그인 실패 부분
                    if (error != null) {
                        Log.e(TAG, "로그인 실패(사용자 취소) $error")
                        // 사용자가 취소
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 다른 오류
                        else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                this, callback = mCallback) // 카카오 이메일 로그인
                        }
                    }

                    // 로그인 성공 부분
                    else if (token != null) {
                        Log.d(TAG, "로그인 성공 ${token.accessToken}")

                        UserApiClient.instance.me { user, error ->
                            if (user == null) {
                                Log.e(TAG, "user == null : $error")
                                return@me
                            }
                            lifecycleScope.launch(exceptionHandler) {
                                val result = loginApi.getToken(
                                    LoginParameter(
                                        serviceUserId = user.id?.toString() ?: "",
                                        email = user.kakaoAccount?.email!!,
                                        nickname = user.kakaoAccount?.profile?.nickname!!
                                    )
                                )

                                if (result.isSuccessful && result.body()?.status == "success") {
                                    val mainIntent = Intent(applicationContext, MainActivity::class.java)
                                    val realToken = result.body()!!.respDocument?.accessToken
                                    val refreshToken = result.body()!!.respDocument?.refreshToken
                                    mainIntent.putExtra("token", realToken)
                                    if (realToken != null) {
                                        apikeyStoreModule.setAccessToken(realToken)
                                    }
                                    if (refreshToken != null) { apikeyStoreModule.setRefreshToken(refreshToken) }
                                    progressDialog.dismiss()
                                    startActivity(mainIntent)
                                } else {
                                    progressDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
            }
        }
//        binding.btnLoginKakao.setOnClickListener {
//            job = lifecycleScope.launch {
//                val response = kakaoApi.getToken(
//                    instance.KAKAO_KEY, instance.KAKAO_REDIRECT_URL, "code")
//                if (response.isSuccessful) {
//                    // Datastore든 SharedPreference에 키값 저장
//                    // 그걸 메인으로 넘기면서 인텐트에 키값 같이 넘겨주기
//                    response.body()?.let {
//                            it1 -> apikeyStoreModule.setApiKey(it1.accessToken)
//                    }
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(applicationContext, "로그인에 실패했습니다.",
//                        Toast.LENGTH_LONG).show()
//                }
//            }
//        }

        apikeyStoreModule = BFTApplication.getInstance().getKeyStore()

        setContentView(binding.root)
    }

//    private fun setApiKey() {
//        lifecycleScope.launch {
//            apikeyStoreModule.set
//        }
//    }

//    private fun loadApiKey() {
//        lifecycleScope.launch {
//            apikeyStoreModule.setApiKey()
//        }
//    }

}