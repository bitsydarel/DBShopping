package com.dbeginc.dbshopping.domain.shopping.services

import com.dbeginc.dbshopping.domain.shopping.entities.ListId
import com.dbeginc.dbshopping.domain.shopping.entities.UserId
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingListUser

expect class RemoteShoppingService {
    fun getShoppingLists(userId: UserId): List<ShoppingList>

    fun getCurrentlyShoppingUser(listId: ListId): List<ShoppingListUser>
}
