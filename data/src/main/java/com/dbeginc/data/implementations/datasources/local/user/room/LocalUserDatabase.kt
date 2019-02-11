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

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.data.proxies.local.user.LocalFriend
import com.dbeginc.data.proxies.local.user.LocalUserPendingRequest
import com.dbeginc.data.proxies.local.user.LocalUserProfile

/**
 * Created by darel on 08.03.18.
 *
 * Local user database
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Database(entities = [
    LocalUserProfile::class,
    LocalFriend::class,
    LocalUserPendingRequest::class
], version = 1)
abstract class LocalUserDatabase : RoomDatabase() {
    abstract fun userDao() : LocalUserDao

    companion object {
        private const val DATABASE_NAME = "shopping_user_db"

        @JvmStatic fun create(appContext: Context) = Room.databaseBuilder(appContext, LocalUserDatabase::class.java, DATABASE_NAME).build()
    }
}