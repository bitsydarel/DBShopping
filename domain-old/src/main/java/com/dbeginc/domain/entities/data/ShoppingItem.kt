/*
 *
 *  * Copyright (C) 2019 Darel Bitsy
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

package com.dbeginc.domain.entities.data

import java.util.*

/**
 * Pojo representing a item of an shopping list
 */
data class ShoppingItem(var uuid: String = UUID.randomUUID().toString(),
                        var name: String,
                        var itemOf: String,
                        var itemOwner: String,
                        var count: Long = 1,
                        var price: Double = 0.0,
                        var bought: Boolean = false,
                        var boughtBy: String = "",
                        var image: ItemImage = ItemImage())
