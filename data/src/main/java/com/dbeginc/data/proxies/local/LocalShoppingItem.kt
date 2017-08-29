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

import com.dbeginc.domain.entities.data.ShoppingItem
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
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
open class LocalShoppingItem (@PrimaryKey var uuid: String = UUID.randomUUID().toString(),
                              @Required var name: String = "",
                              @Required var itemOf: String = "Default",
                              var count: Long = 1,
                              var price: Double = 0.0,
                              var bought: Boolean = false,
                              var boughtBy: String = "",
                              var savedInServer: Boolean = false,
                              var image: LocalItemImage = LocalItemImage()) : RealmModel  {

    /**
     * Convert Local Item Proxy object
     * to domain entity
     * @return {@link com.dbeginc.domain.entities.data.ShoppingItem }
     */
    fun toDomainItem() : ShoppingItem {
        return ShoppingItem(uuid = uuid, name = name, itemOf = itemOf, count = count,
                price = price, bought = bought, boughtBy = boughtBy, image = image.toDomainImage()
        )
    }
}