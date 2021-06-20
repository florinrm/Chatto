package com.example.chatto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatto.databinding.ListItemBinding
import com.example.chatto.model.DeviceNode

class AllDiscoveredNodesRecycleViewAdapter(val nodes: List<DeviceNode>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllDiscoveredNodesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AllDiscoveredNodesViewHolder).bind(position)
    }

    override fun getItemCount() = nodes.size

    private inner class AllDiscoveredNodesViewHolder(private val viewBinding: ListItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
            fun bind(position: Int) {
                val node = nodes[position]
                viewBinding.nodeId.text = node.id
                viewBinding.nodeName.text = node.name
            }
        }
}