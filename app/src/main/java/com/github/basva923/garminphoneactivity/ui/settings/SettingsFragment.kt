package com.github.basva923.garminphoneactivity.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.github.basva923.garminphoneactivity.R
import com.github.basva923.garminphoneactivity.model.CyclingPosition
import com.github.basva923.garminphoneactivity.model.TrackSurface
import com.github.basva923.garminphoneactivity.settings.Settings

class SettingsFragment : Fragment() {
    private lateinit var _totalMassEdit: EditText
    private lateinit var _ftpEdit: EditText
    private lateinit var _ftpHeartRateEdit: EditText
    private val TAG = "SettingsFragment"
    private lateinit var _surfaceSpinner: Spinner
    private lateinit var _positionSpinner: Spinner
    private lateinit var _root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        _root = root
        setupMassText()
        setupPositionSpinner()
        setupSurfaceSpinner()

        setupFTPText()
        setupFTPHeartRateText()

        return root
    }

    private fun setupMassText() {
        _totalMassEdit = _root.findViewById(R.id.settings_total_mass)
        _totalMassEdit.setText(Settings.totalMass.toString())
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.toString().length >= 2 && text.toString().toInt() > 20) {
                    Settings.totalMass = text.toString().toInt()
                    Log.d(TAG, "Changed total mass to " + Settings.totalMass)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        _totalMassEdit.addTextChangedListener(textWatcher)
    }

    private fun setupFTPText() {
        _ftpEdit = _root.findViewById(R.id.settings_ftp)
        _ftpEdit.setText(Settings.ftp.toString())
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.toString().length >= 3) {
                    Settings.ftp = text.toString().toInt()
                    Log.d(TAG, "Changed ftp to " + Settings.ftp)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        _ftpEdit.addTextChangedListener(textWatcher)
    }

    private fun setupFTPHeartRateText() {
        _ftpHeartRateEdit = _root.findViewById(R.id.settings_ftp_heart_rate)
        _ftpHeartRateEdit.setText(Settings.ftpHeartRate.toString())
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.toString().length >= 3) {
                    Settings.ftpHeartRate = text.toString().toInt()
                    Log.d(TAG, "Changed ftp heart rate to " + Settings.ftpHeartRate)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        _ftpHeartRateEdit.addTextChangedListener(textWatcher)
    }

    private fun setupSurfaceSpinner() {
        _surfaceSpinner = _root.findViewById(R.id.settings_trackSurface)
        _surfaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                Settings.trackSurface = TrackSurface.values()[index]
                Log.d(TAG, "Selected ${Settings.trackSurface.name}")
            }

        }
        val dataAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TrackSurface.values().map { it.name.toLowerCase().capitalize() })
        _surfaceSpinner.adapter = dataAdapter
        _surfaceSpinner.setSelection(TrackSurface.values().indexOf(Settings.trackSurface))

    }

    private fun setupPositionSpinner() {
        _positionSpinner = _root.findViewById(R.id.settings_cyclingPosition)
        _positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                Settings.cyclingPosition = CyclingPosition.values()[index]
                Log.d(TAG, "Selected ${Settings.cyclingPosition.name}")
            }

        }
        val dataAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            CyclingPosition.values().map { it.name.toLowerCase().capitalize() })
        _positionSpinner.adapter = dataAdapter
        _positionSpinner.setSelection(CyclingPosition.values().indexOf(Settings.cyclingPosition))

    }


}
