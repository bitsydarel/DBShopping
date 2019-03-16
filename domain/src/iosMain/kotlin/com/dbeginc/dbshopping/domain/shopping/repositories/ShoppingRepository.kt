package com.dbeginc.dbshopping.domain.shopping.repositories

import com.dbeginc.dbshopping.domain.shopping.entities.ListId
import com.dbeginc.dbshopping.domain.shopping.entities.UserId
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingListUser

actual class ShoppingRepository {
    actual fun getShoppingLists(userId: UserId): List<ShoppingList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getLatestShoppingLists(userId: UserId): List<ShoppingList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun getCurrentlyShoppingUser(listId: ListId): List<ShoppingListUser> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
