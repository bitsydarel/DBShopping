package com.dbeginc.data.proxies.remote

import com.dbeginc.data.ConstantHolder
import com.dbeginc.domain.entities.user.User
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 20.08.17.
 *
 * Remote User
 */
class RemoteUser(var uuid: String = UUID.randomUUID().toString(), var name: String = ConstantHolder.ANONYMOUS, var email: String = "", var joinedAt: Any = ServerValue.TIMESTAMP) {

    /**
     * Convert a remote user pojo
     * to a application domain entities User
     * @return {@link User } domain user
     */
    fun toDomainUser() : User {

        val createdAt = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(joinedAt as Long))

        return User(uuid = uuid, name = name, email = email, joinedAt = createdAt)
    }
}