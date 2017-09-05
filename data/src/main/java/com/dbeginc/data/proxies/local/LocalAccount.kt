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

package com.dbeginc.data.proxies.local

import com.dbeginc.domain.entities.user.Account
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*

/**
 * Created by darel on 02.09.17.
 *
 * Local Proxy for account
 */
@RealmClass
open class LocalAccount(@PrimaryKey var uuid: String = UUID.randomUUID().toString(), @Required var name: String = "", var profileImage: String = "", var accountProviders: RealmList<RealmString> = RealmList()) : RealmModel {

    /**
     * Convert a local user account Object
     * to a domain entities user account
     * @return [Account] domain user account
     */
    fun toDomainAccount() : Account = Account(uuid, name, profileImage, accountProviders.map { provider -> provider.value })
}