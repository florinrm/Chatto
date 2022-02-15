package com.example.chatto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatto.databinding.DeviceItemListBinding
import com.example.chatto.domain.Device

class DevicesDiscoveryAdapter(
    private val devices: MutableList<Device>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DeviceItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllDiscoveredDevicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DevicesDiscoveryAdapter.AllDiscoveredDevicesViewHolder).bind(position)
    }

    override fun getItemCount() = devices.size

    internal inner class AllDiscoveredDevicesViewHolder(private val viewBinding: DeviceItemListBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(position: Int) {
            val device = devices[position]
            viewBinding.inputDeviceName.text = device.deviceName
            viewBinding.inputUserName.text = device.deviceID
        }
    }

    fun addDeviceInList(device: Device) {
        this.devices.add(device)
        val lastIndex = devices.size - 1
        this.notifyItemChanged(lastIndex)
    }

    fun removeDevice(deviceId: String) {
        var index = -1
        for (i in 0 until devices.size) {
            if (deviceId == devices[i].deviceID) {
                index = i
                break
            }
        }

        if (index != -1) {
            devices.removeAt(index)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, devices.size)
        }
    }
}