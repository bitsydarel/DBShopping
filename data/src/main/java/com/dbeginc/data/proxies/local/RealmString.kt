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

import io.realm.RealmModel
import io.realm.annotations.RealmClass

/**
 * Created by darel on 29.08.17.
 *
 * Realm Implementation of string
 * Since it's not supported by the api
 */
@RealmClass
open class RealmString(var value: String = "") : RealmModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealmString) return false

        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}