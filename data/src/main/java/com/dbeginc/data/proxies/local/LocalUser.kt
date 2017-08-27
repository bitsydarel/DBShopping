package com.dbeginc.data.proxies.local

import com.dbeginc.data.ConstantHolder
import com.dbeginc.domain.entities.user.User
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
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
 */
@RealmClass
open class LocalUser(@PrimaryKey var uuid: String = UUID.randomUUID().toString(), @Required var name: String = ConstantHolder.ANONYMOUS, var email: String = "", var joinedAt: Long = 0) : RealmModel {

    fun toDomainUser() : User {

        val creationDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(joinedAt))

        return User(uuid = uuid, name = name, email = email, joinedAt = creationDate)
    }
}