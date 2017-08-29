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

import com.dbeginc.domain.entities.data.ShoppingList
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
 */
class RemoteShoppingList(var uuid: String = UUID.randomUUID().toString(),
                         var name: String = "",
                         var ownerName: String = "Anonymous",
                         var userShopping: Map<String, Boolean> = mapOf(),
        /* Last Change as 'Any' because at deserialization time it's a Long but when
          i push to firebase, it's a Map of <String, String> and
          kotlin wildcards behavior make the code so this is the fix i did */
                         var lastChange: Any = ServerValue.TIMESTAMP) {


    /**
     * Help to convert a firebase Shopping list Pojo
     * to a application domain entities Shopping List
     * @return {@link ShoppingList } domain list
     */
    fun toDomainList() : ShoppingList {

        val timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(lastChange as Long))

        return ShoppingList(uuid = uuid, name = name, ownerName = ownerName,
                usersShopping = userShopping.keys.toList(), lastChange = timestamp)
    }

}