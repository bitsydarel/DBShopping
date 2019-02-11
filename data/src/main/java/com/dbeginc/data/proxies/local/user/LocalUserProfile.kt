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

package com.dbeginc.data.proxies.local.user

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.LOCAL_USERS_TABLE


@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName= LOCAL_USERS_TABLE)
data class LocalUserProfile(@PrimaryKey @ColumnInfo(name = "unique_id") val uniqueId: String,
                            var nickname: String,
                            var email: String,
                            var avatar: String,
                            @ColumnInfo(name = "creation_date") val creationDate: Long,
                            val birthday: String?
)