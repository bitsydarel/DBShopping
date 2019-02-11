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

package com.dbeginc.data.proxies.local.list

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.data.ConstantHolder.LOCAL_ITEMS_TABLE

/**
 * Created by darel on 20.08.17.
 *
 * Proxy Object representing a item of an shoppingList
 * For a local storage
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = LOCAL_ITEMS_TABLE,
        indices = [(Index(value = ["name"], unique = true))]
)
data class LocalShoppingItem (@PrimaryKey @ColumnInfo(name="unique_id") var uniqueId: String,
                              var name: String,
                              @ColumnInfo(name="item_of") val itemOf: String,
                              @ColumnInfo(name="item_owner") var itemOwner: String,
                              var count: Long,
                              var price: Double,
                              var bought: Boolean,
                              @ColumnInfo(name="bought_by") val boughtBy: String,
                              @ColumnInfo(name="image_url") var imageUrl: String
)