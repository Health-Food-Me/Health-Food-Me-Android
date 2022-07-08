package org.helfoome.presentation

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import org.helfoome.R
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.util.binding.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requirePermission()
    }

    private fun requirePermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    initNaverMap()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    initNaverMap()
                }
                else -> {
                    initNaverMap()
                    Toast.makeText(this, "위치 권한이 없어 현재 위치를 알 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun initNaverMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.commit {
                    add<MapFragment>(R.id.fragment_naver_map)
                    setReorderingAllowed(true)
                }
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker_red)
        marker.map = map

        val marker2 = Marker()
        marker2.position = LatLng(37.5570135, 126.9783740)
        marker2.icon = OverlayImage.fromResource(R.drawable.ic_marker_green)
        marker2.map = map

        val initialPosition = LatLng(37.5670135, 126.9783740)
        val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
        naverMap.moveCamera(cameraUpdate)
    }
}
