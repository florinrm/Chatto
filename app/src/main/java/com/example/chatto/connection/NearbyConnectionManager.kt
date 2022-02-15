package com.example.chatto.connection

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import com.example.chatto.domain.Endpoint
import com.google.android.gms.nearby.connection.*
import timber.log.Timber

object NearbyConnectionManager {
    lateinit var context: Context
    lateinit var connectionsClient: ConnectionsClient

    val foundDiscoveredEndpointLiveData = MutableLiveData<Endpoint>()
    val lostDiscoveredEndpointLiveData = MutableLiveData<Endpoint>()

    val discoveryLiveData = MutableLiveData<Boolean>()
    val advertisingLiveData = MutableLiveData<Boolean>()

    var isDiscovering = false
    var isAdvertising = false

    private const val serviceId = "com.example.chatto"
    private val strategy: Strategy = Strategy.P2P_POINT_TO_POINT

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            TODO("Not yet implemented")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            TODO("Not yet implemented")
        }

        override fun onDisconnected(endpointId: String) {
            TODO("Not yet implemented")
        }

    }

    private val discoveredEndpoints: MutableMap<String, Endpoint> = HashMap()

    fun startDiscovering() {
        discoveredEndpoints.clear()
        val discoveryOptions = DiscoveryOptions.Builder()
        discoveryOptions.setStrategy(strategy)
        isDiscovering = true

        connectionsClient.startDiscovery(
            serviceId,
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    if (serviceId == info.serviceId) {
                        val endpoint = discoveredEndpoints[endpointId]
                        if (endpoint != null) {
                            Timber.i("Found endpoint ${endpoint.id}: ${endpoint.name}")
                            val discoveredEndpoint = Endpoint(
                                id = endpointId,
                                name = endpoint.name
                            )
                            foundDiscoveredEndpointLiveData.postValue(discoveredEndpoint)
                            discoveredEndpoints[endpointId] = discoveredEndpoint
                        }
                    }
                }

                override fun onEndpointLost(endpointId: String) {
                    val lostEndpoint = discoveredEndpoints[endpointId]
                    if (lostEndpoint != null) {
                        lostDiscoveredEndpointLiveData.postValue(lostEndpoint)
                    }
                    discoveredEndpoints.remove(endpointId)
                }

            },
            discoveryOptions.build()
        ).addOnSuccessListener {
            discoveryLiveData.postValue(true)
        }.addOnFailureListener {
            isDiscovering = false
        }
    }

    fun stopDiscovering() {
        isDiscovering = false
        connectionsClient.stopDiscovery()
        discoveryLiveData.postValue(false)
    }

    fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder()
        advertisingOptions.setStrategy(strategy)
        isAdvertising = true

        connectionsClient.startAdvertising(
            getName(),
            serviceId,
            connectionLifecycleCallback,
            advertisingOptions.build()
        ).addOnSuccessListener {
            advertisingLiveData.postValue(true)
        }.addOnFailureListener {
            isAdvertising = false
        }
    }

    fun stopAdvertising() {
        isAdvertising = false
        connectionsClient.stopAdvertising()
        advertisingLiveData.postValue(false)
    }

    private fun getName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model + " " + getAndroidId()
        }
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}