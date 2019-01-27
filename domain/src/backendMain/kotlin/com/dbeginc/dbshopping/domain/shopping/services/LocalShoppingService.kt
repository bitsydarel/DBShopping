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

package com.dbeginc.dbshopping.domain.shopping.services

import com.dbeginc.dbshopping.domain.shopping.entities.ListId
import com.dbeginc.dbshopping.domain.shopping.entities.UserId
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingListUser

actual class LocalShoppingService {
    actual fun getShoppingLists(userId: UserId): List<ShoppingList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getCurrentlyShoppingUser(listId: ListId): List<ShoppingListUser> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
