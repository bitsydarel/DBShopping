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
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.INSERT_REQUEST
import com.dbeginc.data.ConstantHolder.LIST_PENDING_TABLE

/**
 * Created by darel on 22.02.18.
 *
 * Pending Request
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = LIST_PENDING_TABLE)
data class LocalPendingDataRequest(
        @PrimaryKey @ColumnInfo(name = "item_unique_id") val itemUniqueId: String,
        @ColumnInfo(name = "parent_id") val parentId: String?,
        @ColumnInfo(name = "request_type") val requestType: Short = INSERT_REQUEST
)