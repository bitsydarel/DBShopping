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

package com.dbeginc.users.viewmodels

import com.dbeginc.domain.entities.user.FriendProfile
import com.dbeginc.domain.entities.user.UserProfile

/**
 * Created by darel on 23.02.18.
 *
 * File containing mapper function to
 * transform domain object to UI object and reverse
 */


fun UserProfileModel.toDomain() : UserProfile = UserProfile(
        uniqueId = uniqueId,
        nickname = nickname,
        email = email,
        avatar = avatar,
        creationDate = creationDate,
        birthday = birthday
)

fun UserProfile.toUI() : UserProfileModel = UserProfileModel(
        uniqueId = uniqueId,
        nickname = nickname,
        email = email,
        avatar = avatar,
        creationDate = creationDate,
        birthday = birthday
)

fun FriendProfile.toUI() : FriendModel = FriendModel(
        uniqueId = uniqueId,
        isFriendWithCurrentUser = isFriendWithCurrentUser,
        nickname = nickname,
        email = email,
        avatar = avatar,
        birthday = birthday
)