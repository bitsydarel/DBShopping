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

package com.dbeginc.data.proxies.local.list

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.LOCAL_LISTS_TABLE

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = LOCAL_LISTS_TABLE,
        indices = [(Index(value = ["name"], unique = true))]
)
data class LocalShoppingList(@PrimaryKey @ColumnInfo(name="unique_id") val uniqueId: String,
                             var name: String,
                             @ColumnInfo(name = "owner_id") var ownerId: String,
                             @ColumnInfo(name = "owner_name") val ownerName: String,
                             @ColumnInfo(name="last_change") var lastChange: Long
)