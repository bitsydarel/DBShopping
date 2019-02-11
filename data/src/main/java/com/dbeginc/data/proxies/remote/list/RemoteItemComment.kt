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

package com.dbeginc.data.proxies.remote.list

import android.support.annotation.Keep
import com.dbeginc.domain.entities.data.CommentType
import com.google.firebase.database.ServerValue

/**
 * Created by darel on 07.03.18.
 *
 * Remote representation of item comment
 */
@Keep
data class RemoteItemComment(
        var uniqueId: String = "",
        var publishTime: Any = ServerValue.TIMESTAMP,
        var comment: String = "",
        var commentType: CommentType = CommentType.TEXT,
        var commentArg: String? = null,
        var itemId: String = "",
        var userId: String = "",
        var userName: String = "",
        var userAvatar: String = ""
)