package com.github.basva923.garminphoneactivity.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.LiveTrackInfo
import com.github.basva923.garminphoneactivity.model.Model
import com.github.basva923.garminphoneactivity.model.ModelUpdateReceiver
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.*
import com.mapbox.mapboxsdk.utils.ColorUtils
import org.joda.time.DateTime


class MapFragment : Fragment(), ModelUpdateReceiver {
    private lateinit var _trackView: Line
    private lateinit var _lineManager: LineManager
    private lateinit var _locationMarker: Circle
    private lateinit var _locationManager: CircleManager
    private lateinit var _mapBoxStyle: Style
    private lateinit var _mapBoxMap: MapboxMap
    private val TAG = "MapFragment"

    private lateinit var mapView: MapView

    private lateinit var root: View
    private val liveTracking: LiveTrackInfo = Model.track.liveTrackInfo
    private var lastMove = DateTime(0)
    private var firstLocation = true
    private var startLocation = LatLng(48.85819, 2.29458)
    private val _track = mutableListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this.requireContext(), getString(R.string.mapbox_access_token))

        root = inflater.inflate(R.layout.fragment_map, container, false)

        firstLocation = true
        if (liveTracking.hasLocation()) {
            startLocation = locToLatLng(liveTracking.getLocation())
        }


        setupMap(savedInstanceState)
//        setupTouchListener()
//        setupTrack()
        Model.modelUpdateReceivers.add(this)

        return root
    }

    private fun setupTrack() {
        _lineManager = LineManager(mapView, _mapBoxMap, _mapBoxStyle)

        for (sample in Model.track.samples) {
            _track.add(LatLng(sample.latitude, sample.longitude))
        }

        val lineOptions = LineOptions()
            .withLatLngs(_track)
            .withLineColor(ColorUtils.colorToRgbaString(Color.BLACK))
            .withLineWidth(5.0f)
        _trackView = _lineManager.create(lineOptions)
    }

    private fun setupTouchListener() {
        _mapBoxMap.addOnMoveListener(object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                lastMove = DateTime.now()
            }

            override fun onMove(detector: MoveGestureDetector) {
                lastMove = DateTime.now()
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                lastMove = DateTime.now()
            }
        })
    }

    private fun setupLocation() {
        _locationManager = CircleManager(mapView, _mapBoxMap, _mapBoxStyle)
        // create a fixed circle

        // create a fixed circle
        val circleOptions = CircleOptions()
            .withLatLng(startLocation)
            .withCircleColor(ColorUtils.colorToRgbaString(Color.BLUE))
            .withCircleRadius(6f)

        _locationMarker = _locationManager.create(circleOptions)

        _mapBoxMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .zoom(15.0)
                    .target(startLocation)
                    .build()
            )
        )
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        mapView = root.findViewById(R.id.mapView)!!
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            _mapBoxMap = mapboxMap


            mapboxMap.setStyle(Style.OUTDOORS) {
                _mapBoxStyle = it

                setupLocation()
                setupTrack()
                setupTouchListener()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
        firstLocation = true
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        firstLocation = true
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        if (outState != null) {
//            mapbox.onSaveInstanceState(outState)
//        }
//    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    private fun updateLocation() {
        val location = locToLatLng(liveTracking.getLocation())

        try {
            _track.add(location)
            _trackView.latLngs = _track
            _lineManager.update(_trackView)

            _locationMarker.latLng = location
            _locationManager.update(_locationMarker)

            if (lastMove.isBefore(DateTime.now().minusSeconds(10))) {
                if (firstLocation) {
                    _mapBoxMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    firstLocation = false
                } else
                    _mapBoxMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
        } catch (ex: Exception) {

        }

    }

    private fun locToLatLng(location: Pair<Double, Double>): LatLng {
        return LatLng(location.first, location.second)
    }

    override fun onModelUpdate() {
        activity?.runOnUiThread {
            updateLocation()
        }
    }
}
