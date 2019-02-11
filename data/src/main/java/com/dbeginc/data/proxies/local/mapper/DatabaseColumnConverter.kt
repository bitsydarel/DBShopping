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

package com.dbeginc.data.proxies.local.mapper

import android.arch.persistence.room.TypeConverter
import android.support.annotation.RestrictTo
import com.dbeginc.domain.entities.data.CommentType

/**
 * Created by darel on 10.03.18.
 *
 * Database Column
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class DatabaseColumnConverter {
    @TypeConverter
    fun nameToCommentType(name: String): CommentType = CommentType.valueOf(name)

    @TypeConverter
    fun commentTypeToName(commentType: CommentType): String = commentType.name
}