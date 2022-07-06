package org.helfoome.presentation

import android.os.Bundle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import org.helfoome.R
import org.helfoome.databinding.ActivityMainBinding
import org.helfoome.util.binding.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNaverMap()
    }

    private fun initNaverMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
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

    companion object {
        lateinit var naverMap: NaverMap
    }
}
