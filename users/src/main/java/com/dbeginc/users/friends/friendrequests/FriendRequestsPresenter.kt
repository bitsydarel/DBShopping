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

package com.dbeginc.users.friends.friendrequests

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.domain.entities.user.FriendRequest
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * Created by darel on 06.03.18.
 *
 * FriendProfile Requests Presenter
 */
class FriendRequestsPresenter : MVPVPresenter<FriendRequestsView> {
    private val requestsJail : BlockingQueue<Pair<Int, FriendRequest>> = ArrayBlockingQueue(1, true)

    override fun bind(view: FriendRequestsView) = view.setupView()

    fun onFriendRequestReplied(position: Int, friendRequest: FriendRequest) = requestsJail.offer(position to friendRequest)

    fun onFailedToDeliveryFriendRequestResponse() : Pair<Int, FriendRequest> = requestsJail.poll()
}