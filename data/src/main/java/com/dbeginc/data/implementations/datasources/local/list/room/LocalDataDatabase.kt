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

package com.dbeginc.data.implementations.datasources.local.list.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.data.proxies.local.list.*
import com.dbeginc.data.proxies.local.mapper.DatabaseColumnConverter

/**
 * Created by darel on 08.03.18.
 *
 * Local data database
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Database(entities = [
    LocalShoppingList::class, LocalShoppingItem::class,
    LocalItemComment::class, LocalShoppingUser::class,
    LocalPendingListMemberRequest::class, LocalPendingDataRequest::class
], version = 1)
@TypeConverters(DatabaseColumnConverter::class)
abstract class LocalDataDatabase : RoomDatabase() {

    abstract fun listDao() : LocalListDao

    companion object {
        private const val DATABASE_NAME = "shopping_data_db"

        @JvmStatic fun create(appContext: Context): LocalDataDatabase = Room.databaseBuilder(appContext, LocalDataDatabase ::class.java, DATABASE_NAME).build()
    }
}