package com.example.chatto.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.chatto.adapters.DevicesDiscoveryAdapter
import com.example.chatto.databinding.ActivityHomeBinding
import com.example.chatto.domain.Device
import com.example.chatto.services.NearbyConnectionService
import com.example.chatto.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var discoveredDevicesList: RecyclerView
    private lateinit var advertisingDevice: SwitchCompat
    private lateinit var discoveringDevices: SwitchCompat

    private lateinit var devicesAdapter: DevicesDiscoveryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        devicesAdapter = DevicesDiscoveryAdapter(mutableListOf())

        initUi()
        initViewModelObserving()
    }

    private fun initUi() {
        advertisingDevice = binding.switchAdvertisingHome
        discoveringDevices = binding.switchDiscoveryHome
        discoveredDevicesList = binding.recycleViewDevices
    }

    private fun initViewModelObserving() {
        viewModel.discoveryStatus().observe(this, {
            discoveringDevices.isChecked = it
        })

        viewModel.advertisingStatus().observe(this, {
            advertisingDevice.isChecked = it
        })

        viewModel.foundDiscoveredEndPoints().observe(this, { endpoint ->
            val device = Device(deviceID = endpoint.id, deviceName = endpoint.name)
            devicesAdapter.addDeviceInList(device)
        })

        viewModel.lostDiscoveredEndPoints().observe(this, { endpoint ->
            devicesAdapter.removeDevice(endpoint.id)
        })
    }
}