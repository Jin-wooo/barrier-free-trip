package com.triply.barrierfreetrip

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.triply.barrierfreetrip.databinding.ActivityMainBinding
import com.triply.barrierfreetrip.feature.ApikeyStoreModule
import kotlinx.coroutines.launch

// Main Activity로서의 기능 + 로그인 기능을 수행합니다.
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var apikeyStoreModule: ApikeyStoreModule
    private val hasPermissionForCoarseLocation by lazy { ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED }
    private val hasPermissionForFineLocation by lazy { ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DataBinding Setting
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        apikeyStoreModule = BFTApplication.getInstance().getKeyStore()


        // FragmentContainerView를 사용하여 Nav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        binding.bnvMain.apply {
            setupWithNavController(navHostFragment.navController)
            itemIconTintList = null
            setOnItemSelectedListener { item ->
                navHostFragment.navController.navigate(item.itemId)
                true
            }
        }

        navHostFragment.navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                val isBottomNavigationViewVisible = destination.id in listOf(
                    R.id.homeFragment,
                    R.id.searchFragment,
//                    R.id.mapFragment,
                    R.id.wishListFragment,
                    R.id.myPageFragment,
                    R.id.staylistFragment,
                    R.id.pickFragment,
                    R.id.appInfoFragment
                )
                binding.bnvMain.visibility = if (isBottomNavigationViewVisible) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

        })


        apikeyStoreModule = BFTApplication.getInstance().getKeyStore()


        if (!hasPermissionForCoarseLocation || !hasPermissionForFineLocation) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
        }

//        // meta-data에 넣어둔 데이터 접근하는 방법
//        // 대박 오래 걸렸으니까 제발 날려먹지마라
//        applicationContext.packageManager.getApplicationInfo(
//            applicationContext.packageName, PackageManager.GET_META_DATA
//        ).apply {
//            apikey = metaData.getString("com.google.android.geo.API_KEY").toString()
//        }
//        Log.d("AAA", apikey)



    }

    private fun getApikey() {
        lifecycleScope.launch {
            apikeyStoreModule.getApiKey.collect {

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_COARSE_LOCATION -> {
                if (hasPermissionForFineLocation) return

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
            }
            REQUEST_FINE_LOCATION -> {
                if (hasPermissionForFineLocation) {
                    val navHostFragment = (supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment)
                    val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                    if (currentFragment is HomeFragment) {
                        currentFragment.startLocationUpdates()
                    }
                }
            }
            else -> {}
        }
    }

    companion object {
        const val CONTENT_ID = "content_id"
        const val CONTENT_TITLE = "content_title"
        const val CONTENT_TYPE = "type"
        private const val REQUEST_FINE_LOCATION = 0
        private const val REQUEST_COARSE_LOCATION = 1
    }
}