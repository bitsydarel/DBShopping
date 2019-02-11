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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.FriendRequestModel
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.entities.user.FriendRequest
import com.dbeginc.domain.repositories.IUserRepo
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 05.03.18.
 *
 * FriendProfile Request ViewModel
 */
class FriendRequestViewModel @Inject constructor(private val userRepo: IUserRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _friendRequests : MutableLiveData<List<FriendRequest>> = MutableLiveData()
    private val _friendRequestsResponseListener = BehaviorRelay.create<List<FriendRequest>>()
    val presenter = FriendRequestsPresenter()

    init {
        _friendRequestsResponseListener.subscribe(_friendRequests::postValue)
    }

    fun getFriendRequests() : LiveData<List<FriendRequest>> = _friendRequests

    fun getFriendRequests(currentUserId: String) {
        userRepo.getFriendRequests(UserRequestModel(currentUserId, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .doAfterSuccess {
                    requestState.postValue(RequestState.COMPLETED)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(_friendRequestsResponseListener)
                .addTo(subscriptions)
    }

    fun acceptFriendRequest(userId: String, email: String, nickname: String, avatar: String, listId: String, listName: String, requesterId: String) {
        userRepo.acceptFriendRequest(
                FriendRequestModel(
                        userId,
                        email,
                        nickname,
                        avatar,
                        listId,
                        listName,
                        requesterId
                )
        ).doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doOnComplete { getModifiableLastRequest().onNext(RequestType.UPDATE) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

    fun declineFriendRequest(userId: String, email: String, nickname: String, avatar: String, listId: String, listName: String, requesterId: String) {
        userRepo.declineFriendRequest(
                FriendRequestModel(
                        userId,
                        email,
                        nickname,
                        avatar,
                        listId,
                        listName,
                        requesterId
                )
        ).doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doOnComplete { getModifiableLastRequest().onNext(RequestType.UPDATE) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }

}