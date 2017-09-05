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

package com.dbeginc.data.proxies.remote

import com.dbeginc.domain.entities.user.Account
import java.util.*

/**
 * Created by darel on 03.09.17.
 *
 * Remote User Account
 */
data class RemoteAccount(var userId: String = UUID.randomUUID().toString(), var name: String = "", var profileImage: String = "", var accountProviders: Map<String, Boolean> = mapOf()) {

    /**
     * Convert a firebase user account pojo
     * to a domain entities user account
     * @return [Account] domain account
     */
    fun toDomainAccount() : Account =
            Account(userId, name, profileImage,  accountProviders.keys.toList())
}