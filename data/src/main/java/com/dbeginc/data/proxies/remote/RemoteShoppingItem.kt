package com.dbeginc.data.proxies.remote

import com.dbeginc.domain.entities.data.ShoppingItem
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
data class RemoteShoppingItem(val uuid: String = UUID.randomUUID().toString(),
                              var name: String = "",
                              val itemOf: String = "Default",
                              var count: Long = 1,
                              var price: Double = 0.0,
                              var brought: Boolean = false,
                              var image: RemoteItemImage = RemoteItemImage()) {

    /**
     * Convert Remote Item Proxy object
     * to domain item entity
     * @return {@link ShoppingItem }
     */
    fun toDomainItem() : ShoppingItem {
        return ShoppingItem(uuid = uuid, name = name, itemOf = itemOf, count = count,
                price = price, brought = brought, image = image.toDomainImage())
    }

}