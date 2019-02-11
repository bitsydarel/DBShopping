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

package com.dbeginc.users.friends.sharelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.request.FriendRequestModel
import com.dbeginc.domain.entities.request.ListRequestModel
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.entities.user.UserProfile
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.users.viewmodels.FriendModel
import com.dbeginc.users.viewmodels.UserProfileModel
import com.dbeginc.users.viewmodels.toUI
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 04.03.18.
 *
 * Friends view model
 */
class ShareListViewModel @Inject constructor(private val userRepo: IUserRepo, private val dataRepo: IDataRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _friends : MutableLiveData<List<FriendModel>> = MutableLiveData()
    private val _members : MutableLiveData<List<String>> = MutableLiveData()
    private val _friendsResponseListener = BehaviorRelay.create<List<FriendModel>>()
    private val _membersResponseListener = BehaviorRelay.create<List<String>>()
    val presenter: ShareListPresenter = ShareListPresenter()

    init {
        _friendsResponseListener.subscribe(_friends::postValue)
        _membersResponseListener.subscribe(_members::postValue)
    }

    fun getFriends() : LiveData<List<FriendModel>> = _friends

    fun getMembers() : LiveData<List<String>> = _members

    fun loadFriends(userId: String) {
        userRepo.getUserHisFriends(UserRequestModel(userId, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { domainFriends -> domainFriends.map { friend -> friend.toUI() } }
                .observeOn(threads.UI)
                .doAfterNext {
                    requestState.postValue(RequestState.COMPLETED)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)

                }.subscribe(_friendsResponseListener)
                .addTo(subscriptions)
    }

    fun loadMembers(listId: String) {
        dataRepo.getListMembers(ListRequestModel(listId, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { members -> members.map { friend -> friend.uniqueId } }
                .observeOn(threads.UI)
                .doAfterNext {
                    requestState.postValue(RequestState.COMPLETED)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)

                }.subscribe(_membersResponseListener)
                .addTo(subscriptions)
    }

    fun findFriends(currentUserId: String, friendName: String) {
        userRepo.findFriends(UserRequestModel(currentUserId, friendName))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .map { domainFriends -> domainFriends.map { friend -> friend.toUI() } }
                .observeOn(threads.UI)
                .doAfterSuccess {
                    requestState.postValue(RequestState.COMPLETED)
                    getModifiableLastRequest().onNext(RequestType.GET)
                }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }.subscribe(_friendsResponseListener)
                .addTo(subscriptions)
    }

    fun shareListWithFriends(listId: String, listName: String, currentUser: UserProfileModel, newMembers: List<FriendModel>) {
        userRepo.sendFriendRequests(newMembers.map { friend ->
            FriendRequestModel(
                    currentUser.uniqueId,
                    currentUser.email,
                    currentUser.nickname,
                    currentUser.avatar,
                    listId,
                    listName,
                    friend.uniqueId
            )
        }).doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .doOnComplete { getModifiableLastRequest().onNext(RequestType.UPDATE) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            getModifiableLastRequest().onNext(RequestType.UPDATE)
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                ).addTo(subscriptions)
    }
}