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

package com.dbeginc.data.implementations.datasources.local.user

import android.support.annotation.RestrictTo
import com.dbeginc.data.implementations.datasources.local.LocalUserSource
import com.dbeginc.data.implementations.datasources.local.user.room.LocalUserDatabase
import com.dbeginc.data.proxies.local.mapper.toDomain
import com.dbeginc.data.proxies.local.mapper.toLocalProxy
import com.dbeginc.data.proxies.local.user.LocalUserPendingRequest
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.UserProfile
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

@RestrictTo(RestrictTo.Scope.LIBRARY)
class LocalUserSourceImpl(private val db: LocalUserDatabase) : LocalUserSource {

    override fun defineCurrentUser(requestModel: UserRequestModel<UserProfile>): Completable {
        return Completable.fromCallable {
            db.userDao().insertUser(requestModel.arg.toLocalProxy())
        }
    }

    override fun addFriend(requestModel: UserRequestModel<FriendProfile>): Completable =
            Completable.fromCallable { db.userDao().insertFriend(requestModel.arg.toLocalProxy()) }

    override fun addFriends(requestModel: UserRequestModel<List<FriendProfile>>): Completable {
        return Completable.fromCallable {
            db.userDao().insertFriends(requestModel.arg.map {
                friend -> friend.toLocalProxy() }
            )
        }
    }

    override fun getCurrentUser(requestModel: UserRequestModel<Unit>): Flowable<UserProfile> = db.userDao().getUser(requestModel.userId).map { localUser -> localUser.toDomain() }

    override fun getFriend(requestModel: UserRequestModel<String>): Flowable<FriendProfile> = db.userDao().getFriend(requestModel.arg).map { localFriend -> localFriend.toDomain() }

    override fun getUserHisFriends(requestModel: UserRequestModel<Unit>): Flowable<List<FriendProfile>> {
        return db.userDao()
                .getAllUserFriends()
                .map { localFriends -> localFriends.map { friend -> friend.toDomain() } }
    }

    override fun updateUser(requestModel: UserRequestModel<UserProfile>): Completable {
        return Completable.fromCallable {
            db.userDao().updateUserInfo(requestModel.arg.toLocalProxy())
        }
    }

    override fun deleteAll(): Completable = Completable.fromCallable {
        db.userDao().deleteAll()
    }

    override fun addPendingRequest(request: LocalUserPendingRequest) : Completable {
        return Completable.create {
            db.userDao().addPendingRequest(request)
        }
    }

    override fun getAllPendingRequest() : Maybe<List<LocalUserPendingRequest>> = db.userDao().getAllPendingRequest()

    override fun deletePendingRequest(request: LocalUserPendingRequest) : Completable = Completable.create { db.userDao().deletePendingRequest(request) }

}