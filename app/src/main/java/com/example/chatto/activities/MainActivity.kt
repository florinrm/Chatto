package com.example.chatto.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatto.databinding.ActivityMainBinding
import com.example.chatto.model.DeviceNode
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import timber.log.Timber
import timber.log.Timber.DebugTree


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var endpoints = HashMap<String, DeviceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(DebugTree())

        addOnClickListeners()
    }

    private fun addOnClickListeners() {
        binding.findUsersNearbyButton.setOnClickListener {
            detectDevices()
        }
    }

    private fun detectDevices() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        endpoints.clear()

        Nearby.getConnectionsClient(this)
                .startDiscovery(applicationContext.packageName, object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(endpointId: String, endpointDiscovery: DiscoveredEndpointInfo) {
                        if (applicationContext.packageName == endpointDiscovery.serviceId) {
                            val endpoint = endpoints[endpointId]

                            if (endpoint == null) {
                                endpoints[endpointId] = DeviceNode(endpointId, endpointDiscovery.endpointName)
                            }
                        }
                    }

                    override fun onEndpointLost(endpointId: String) {
                        endpoints.remove(endpointId)
                    }

                }, discoveryOptions)
    }

    companion object {
        val STRATEGY = Strategy.P2P_POINT_TO_POINT
    }
}