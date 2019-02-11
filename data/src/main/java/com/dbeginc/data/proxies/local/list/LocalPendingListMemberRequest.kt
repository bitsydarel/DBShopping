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
import com.dbeginc.data.ConstantHolder.LIST_MEMBER_PENDING_TABLE

/**
 * Created by darel on 08.03.18.
 *
 * Local pending list member request
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = LIST_MEMBER_PENDING_TABLE)
data class LocalPendingListMemberRequest(
        @PrimaryKey @ColumnInfo(name = "user_id") val userId: String,
        @ColumnInfo(name = "list_id") val listId: String
)