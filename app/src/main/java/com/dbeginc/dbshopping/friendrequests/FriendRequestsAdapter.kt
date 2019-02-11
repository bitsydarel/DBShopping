/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping.friendrequests

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseAdapter
import com.dbeginc.dbshopping.databinding.FriendRequestLayoutBinding
import com.dbeginc.domain.entities.user.FriendRequest
import java.util.*

/**
 * Created by darel on 06.03.18.
 *
 * FriendProfile requests adapter
 */
class FriendRequestsAdapter(override var data: LinkedList<FriendRequest> = LinkedList(), private val bridge: FriendRequestBridge) : BaseAdapter<FriendRequest, FriendRequestsAdapter.FriendRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FriendRequestViewHolder(
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.friend_request_layout,
                        parent,
                        false
                ),
                bridge
        )
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val friendRequest = data[position]

        holder.bind(friendRequest)
    }

    @Synchronized
    override fun updateData(newData: List<FriendRequest>) {
        val diffResult = DiffUtil.calculateDiff(FriendRequestDiff(data, newData))

        data = LinkedList(newData)

        diffResult.dispatchUpdatesTo(this)
    }

    fun addAt(position: Int, friendRequest: FriendRequest) {
        data.add(position, friendRequest)

        notifyItemInserted(position)
    }

    fun removeAt(position: Int) {
        data.removeAt(position)

        notifyItemRemoved(position)
    }

    inner class FriendRequestViewHolder(private val binding: FriendRequestLayoutBinding, private val actionBridge: FriendRequestBridge) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.acceptFriendRequest.setOnClickListener {
                actionBridge.onFriendRequestAccepted(adapterPosition, binding.friendRequest!!)
            }

            binding.declineFriendRequest.setOnClickListener {
                actionBridge.onFriendRequestDeclined(adapterPosition, binding.friendRequest!!)
            }
        }

        internal fun bind(friendRequest: FriendRequest) {
            binding.friendRequest = friendRequest

            binding.executePendingBindings()
        }
    }
}