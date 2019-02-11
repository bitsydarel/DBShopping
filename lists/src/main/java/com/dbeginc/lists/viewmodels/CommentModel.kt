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

package com.dbeginc.lists.viewmodels

import com.dbeginc.domain.entities.data.CommentType

/**
 * Created by darel on 09.03.18.
 *
 * Comment model
 */
data class CommentModel (
        val uniqueId: String,
        val publishTime: String,
        val timestamp: Long,
        val comment: String,
        val commentType: CommentType,
        val commentArg: String?,
        val itemId: String,
        val userId: String,
        val userName: String,
        val userAvatar: String
)