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


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentFriendRequestsBinding
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.users.friends.friendrequests.FriendRequestViewModel
import com.dbeginc.users.friends.friendrequests.FriendRequestsView


/**
 * A FriendProfile request screen [FriendRequestsFragment] subclass.
 */
class FriendRequestsFragment : BaseFragment(), FriendRequestsView, FriendRequestBridge {
    private lateinit var viewModel: FriendRequestViewModel
    private lateinit var binding : FragmentFriendRequestsBinding
    private val friendRequestsAdapter by lazy { FriendRequestsAdapter(bridge = this) }
    private val stateObserver = Observer<RequestState> {
        onStateChanged(it!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[FriendRequestViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //TODO implement test of this
        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getFriendRequests().observe(this, Observer {
            friendRequestsAdapter.updateData(it!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_friend_requests,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as?MainActivity)?.setSupportActionBar(binding.friendRequestsToolbar)

        viewModel.presenter.bind(this)
    }

    override fun setupView() {
        binding.friendRequestsProgressBar.hideWithAnimation()

        binding.friendRequestsToolbar.setNavigationOnClickListener {
            MainNavigator.goToListsScreen(activity as MainActivity)
        }

        binding.friendRequestsLists.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.friendRequestsLists.adapter = friendRequestsAdapter

        viewModel.getFriendRequests(preferences.getString(ConstantHolder.USER_ID, ""))
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.friendRequestsProgressBar.showWithAnimation()
            RequestState.COMPLETED -> binding.root.postDelayed(binding.friendRequestsProgressBar::hideWithAnimation, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onRequestFailed, LOADING_PERIOD)
        }
    }

    private fun onRequestFailed() {
        binding.friendRequestsProgressBar.hideWithAnimation()

        if (viewModel.getLastRequest() == RequestType.UPDATE) {
            binding.friendRequestsLayout.snack(resId = R.string.could_not_sent_response)

            val positionWithFriendRequest = viewModel.presenter.onFailedToDeliveryFriendRequestResponse()

            friendRequestsAdapter.addAt(positionWithFriendRequest.first, positionWithFriendRequest.second)

        } else {
            Snackbar.make(binding.friendRequestsLayout, R.string.could_not_load_requests, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_retry, { viewModel.getFriendRequests(preferences.getString(ConstantHolder.USER_ID, "")) })
                    .show()
        }
    }

    override fun onFriendRequestAccepted(position: Int, friendRequest: FriendRequest) {
        currentUser?.let {
            viewModel.presenter.onFriendRequestReplied(position, friendRequest)

            friendRequestsAdapter.removeAt(position)

            viewModel.acceptFriendRequest(
                    it.uniqueId,
                    it.email,
                    it.nickname,
                    it.avatar,
                    friendRequest.requesterListId,
                    friendRequest.requesterListName,
                    friendRequest.requesterId
            )
        }
    }

    override fun onFriendRequestDeclined(position: Int, friendRequest: FriendRequest) {
        currentUser?.let {
            viewModel.presenter.onFriendRequestReplied(position, friendRequest)

            friendRequestsAdapter.removeAt(position)

            viewModel.declineFriendRequest(
                    it.uniqueId,
                    it.email,
                    it.nickname,
                    it.avatar,
                    friendRequest.requesterListId,
                    friendRequest.requesterListName,
                    friendRequest.requesterId
            )
        }
    }
}
