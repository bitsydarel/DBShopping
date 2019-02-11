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

package com.dbeginc.data.implementations.datasources.local.user.room

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.LOCAL_FRIENDS_TABLE
import com.dbeginc.data.ConstantHolder.LOCAL_USERS_TABLE
import com.dbeginc.data.ConstantHolder.USER_PENDING_TABLE
import com.dbeginc.data.proxies.local.user.LocalFriend
import com.dbeginc.data.proxies.local.user.LocalUserProfile
import com.dbeginc.data.proxies.local.user.LocalUserPendingRequest
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by darel on 21.02.18.
 *
 * Local UserProfile data access object
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Dao
abstract class LocalUserDao {

    @Query("SELECT * FROM $LOCAL_USERS_TABLE WHERE unique_id LIKE :userId")
    abstract fun getUser(userId: String) : Flowable<LocalUserProfile>

    @Query("SELECT * FROM $LOCAL_FRIENDS_TABLE WHERE unique_id LIKE :friendId")
    abstract fun getFriend(friendId: String) : Flowable<LocalFriend>

    @Query("SELECT * FROM $LOCAL_FRIENDS_TABLE WHERE is_friend_with_current_user LIKE :friendWithCurrentUser")
    abstract fun getAllUserFriends(friendWithCurrentUser: Boolean = true): Flowable<List<LocalFriend>>
/*
    @Query("SELECT * FROM $LOCAL_FRIENDS_TABLE WHERE nickname LIKE :name || '%'")
    abstract fun findFriends(name: String): Flowable<List<LocalFriend>>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUser(user: LocalUserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFriend(friend: LocalFriend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFriends(friends: List<LocalFriend>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateUserInfo(user: LocalUserProfile)

    @Transaction
    open fun deleteAll() {
        deleteUsersTable()
        deleteFriendsTable()
        deletePendingTable()
    }

    @Query("DELETE FROM $LOCAL_USERS_TABLE")
    abstract fun deleteUsersTable()

    @Query("DELETE FROM $LOCAL_FRIENDS_TABLE")
    abstract fun deleteFriendsTable()

    @Query("DELETE FROM $USER_PENDING_TABLE")
    abstract fun deletePendingTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addPendingRequest(request: LocalUserPendingRequest)

    @Query("SELECT * FROM $USER_PENDING_TABLE")
    abstract fun getAllPendingRequest() : Maybe<List<LocalUserPendingRequest>>

    @Delete
    abstract fun deletePendingRequest(request: LocalUserPendingRequest)
}