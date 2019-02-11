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

package com.dbeginc.data.implementations.datasources.local

import com.dbeginc.data.proxies.local.user.LocalUserPendingRequest
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.UserProfile
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface LocalUserSource {
    fun defineCurrentUser(requestModel: UserRequestModel<UserProfile>) : Completable

    fun addFriend(requestModel: UserRequestModel<FriendProfile>) : Completable

    fun addFriends(requestModel: UserRequestModel<List<FriendProfile>>) : Completable

    fun getCurrentUser(requestModel: UserRequestModel<Unit>) : Flowable<UserProfile>

    fun getFriend(requestModel: UserRequestModel<String>) : Flowable<FriendProfile>

    fun getUserHisFriends(requestModel: UserRequestModel<Unit>) : Flowable<List<FriendProfile>>

    fun updateUser(requestModel: UserRequestModel<UserProfile>) : Completable

    fun deleteAll() : Completable

    fun addPendingRequest(request: LocalUserPendingRequest) : Completable

    fun getAllPendingRequest() : Maybe<List<LocalUserPendingRequest>>

    fun deletePendingRequest(request: LocalUserPendingRequest) : Completable
}