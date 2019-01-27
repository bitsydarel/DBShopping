package com.dbeginc.dbshopping.domain.shopping.usecases

import com.dbeginc.dbshopping.domain.shopping.entities.UserId
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList

class GetShoppingListsParam(
    val userId: UserId,
    val sorter: (ShoppingList) -> Comparable<Any>?,
    val localOnly: Boolean
)
