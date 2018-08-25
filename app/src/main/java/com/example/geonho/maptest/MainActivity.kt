package com.example.geonho.maptest

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks{

    private lateinit var daumMapView :MapView
    private lateinit var mapContainer: ViewGroup
    private lateinit var marker : MapPOIItem

    var lat:Double = 0.0
    var lon:Double = 0.0

    private lateinit var locationManager: LocationManager

    private val mLocationListener : LocationListener = object : LocationListener{
        override fun onLocationChanged(location: Location) {
            lat = location.latitude
            lon = location.longitude
            Log.d("test",location.latitude.toString())
            Log.d("test",location.longitude.toString())
            daumMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat,lon),true)

            //마커
            val MARKER_POINT = MapPoint.mapPointWithGeoCoord(lat,lon) //위치 좌표값
            marker = MapPOIItem()//객체
            marker.itemName = "현재 위치"
            marker.tag = 0
            marker.mapPoint = MARKER_POINT
            marker.markerType = MapPOIItem.MarkerType.BluePin //기본으로 제공하는 BluePin 마커 모양.
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin //마커를 클릭했을 때, 기본으로 제공하는 RedPin 마커 모양.

            daumMapView.removeAllPOIItems()//마커 1개만 생성하도록
            daumMapView.addPOIItem(marker)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(EasyPermissions.hasPermissions(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            setMapView()
        }else{
            requestPermission()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        requestPermission()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        setMapView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults, this)
    }

    private fun requestPermission(){
        EasyPermissions.requestPermissions(this@MainActivity,"현재 위치 획득을 위해서는 권한이 필요합니다",300,android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setMapView(){
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,1f,mLocationListener)
        }catch (e: SecurityException){
            requestPermission()
        }
        daumMapView = MapView(this)
        mapContainer = mapView as ViewGroup
        mapContainer.addView(daumMapView)

    }
}
