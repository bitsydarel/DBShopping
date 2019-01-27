package com.dbeginc.dbshopping.domain.shopping.usecases

import com.bitsydarel.cleanarch.core.usecases.UseCaseWithParams
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList
import com.dbeginc.dbshopping.domain.shopping.services.LocalShoppingService
import com.dbeginc.dbshopping.domain.shopping.services.RemoteShoppingService

class GetShoppingLists(
    private val localShoppingService: LocalShoppingService?,
    private val remoteShoppingService: RemoteShoppingService
) : UseCaseWithParams<GetShoppingListsParam, List<ShoppingList>>() {

    override fun buildUseCase(params: GetShoppingListsParam): List<ShoppingList> {
        return if (localShoppingService == null) {
            if (params.localOnly) {
                throw IllegalStateException("Can't get local shopping list, no local service available")
            } else {
                remoteShoppingService.getShoppingLists(params.userId)
                    .sortedBy(params.sorter)
            }
        } else {
            localShoppingService.getShoppingLists(params.userId)
                .sortedBy(params.sorter)
        }
    }

    private fun getShoppingListsFromRemote(params: GetShoppingListsParam): List<ShoppingList> {
        return remoteShoppingService.getShoppingLists(params.userId)
    }
}
