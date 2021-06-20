package com.example.chatto.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.chatto.adapters.AllDiscoveredNodesRecycleViewAdapter
import com.example.chatto.databinding.ActivityMainBinding
import com.example.chatto.model.DeviceNode
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import timber.log.Timber
import timber.log.Timber.DebugTree


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var discoveredNearbyNodes: HashMap<String, DeviceNode>
    private lateinit var pendingConnections: HashMap<String, DeviceNode>
    private lateinit var activeConnections: HashMap<String, DeviceNode>
    private lateinit var deviceConnectionLifecycleCallback: ConnectionLifecycleCallback
    private lateinit var connectionsClient: ConnectionsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        discoveredNearbyNodes = HashMap()
        pendingConnections = HashMap()
        activeConnections = HashMap()

        initConnectionLifecycleCallback()
        connectionsClient = Nearby.getConnectionsClient(this)

        Timber.plant(DebugTree())

        advertise()
        addOnClickListeners()
    }

    private fun initConnectionLifecycleCallback() {
        deviceConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                pendingConnections[endpointId] = DeviceNode(endpointId, connectionInfo.endpointName)
            }

            override fun onConnectionResult(
                endpointId: String,
                connectionResolution: ConnectionResolution
            ) {
                if (connectionResolution.status.isSuccess) {
                    val endpoint = pendingConnections[endpointId]
                    if (endpoint != null) {
                        activeConnections[endpointId] = endpoint
                        pendingConnections.remove(endpointId)
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                activeConnections.remove(endpointId)
            }

        }
    }

    private fun addOnClickListeners() {
        binding.findUsersNearbyButton.setOnClickListener {
            detectDevices()
        }
    }

    /**
     * Detecting nearby devices to connect
     */
    private fun detectDevices() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        discoveredNearbyNodes.clear()

        connectionsClient.startDiscovery(
            getServiceId(),
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(
                    endpointId: String,
                    endpointDiscovery: DiscoveredEndpointInfo
                ) {
                    if (applicationContext.packageName == endpointDiscovery.serviceId) {
                        val endpoint = discoveredNearbyNodes[endpointId]

                        if (endpoint == null) {
                            discoveredNearbyNodes[endpointId] =
                                DeviceNode(endpointId, endpointDiscovery.endpointName)
                        }
                    }
                }

                override fun onEndpointLost(endpointId: String) {
                    discoveredNearbyNodes.remove(endpointId)
                }

            }, discoveryOptions
        )

        binding.nodesList.adapter =
            AllDiscoveredNodesRecycleViewAdapter(discoveredNearbyNodes.map { it.value })
    }


    /**
     * To be detected by other devices
     */
    private fun advertise() {
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient.startAdvertising(
            getName(),
            getServiceId(),
            deviceConnectionLifecycleCallback,
            advertisingOptions
        )
    }

    private fun getName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return "$manufacturer - $model - ${getId()}"
    }

    @SuppressLint("HardwareIds")
    private fun getId(): String {
        return Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private fun getServiceId() = applicationContext.packageName

    companion object {
        val STRATEGY = Strategy.P2P_POINT_TO_POINT
    }
}