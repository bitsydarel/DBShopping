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
import com.dbeginc.data.ConstantHolder
import com.dbeginc.domain.entities.data.CommentType

/**
 * Created by darel on 07.03.18.
 *
 * Local Item comment
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = ConstantHolder.LOCAL_ITEMS_COMMENTS)
data class LocalItemComment (
        @PrimaryKey @ColumnInfo(name="unique_id") val uniqueId: String,
        @ColumnInfo(name="publish_time") val publishTime: Long,
        val comment: String,
        @ColumnInfo(name="comment_type") val commentType: CommentType,
        @ColumnInfo(name="comment_arg") val commentArg: String?,
        @ColumnInfo(name="item_id") val itemId: String,
        @ColumnInfo(name = "user_id") val userId: String,
        @ColumnInfo(name = "user_name") val userName: String,
        @ColumnInfo(name = "user_avatar") val userAvatar: String
)