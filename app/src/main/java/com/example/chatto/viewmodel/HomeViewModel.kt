package com.example.chatto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.chatto.connection.NearbyConnectionManager
import com.example.chatto.domain.Device

class HomeViewModel(application: Application): AndroidViewModel(application) {
    fun foundDiscoveredEndPoints() = NearbyConnectionManager.foundDiscoveredEndpointLiveData
    fun lostDiscoveredEndPoints() = NearbyConnectionManager.lostDiscoveredEndpointLiveData

    fun discoveryStatus() = NearbyConnectionManager.discoveryLiveData
    fun advertisingStatus() = NearbyConnectionManager.advertisingLiveData

    fun onSwitchDiscovery() {
        if (NearbyConnectionManager.isDiscovering) {
            NearbyConnectionManager.startDiscovering()
        } else {
            NearbyConnectionManager.stopDiscovering()
        }
    }

    fun onSwitchAdvertising() {
        if (NearbyConnectionManager.isAdvertising) {
            NearbyConnectionManager.startAdvertising()
        } else {
            NearbyConnectionManager.stopAdvertising()
        }
    }
}