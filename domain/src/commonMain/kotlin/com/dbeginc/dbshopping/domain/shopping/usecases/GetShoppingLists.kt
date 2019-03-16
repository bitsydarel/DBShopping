package com.dbeginc.dbshopping.domain.shopping.usecases

import com.bitsydarel.cleanarch.core.usecases.UseCaseWithParams
import com.dbeginc.dbshopping.domain.shopping.entities.lists.ShoppingList
import com.dbeginc.dbshopping.domain.shopping.repositories.ShoppingRepository

class GetShoppingLists(
    private val shoppingRepository: ShoppingRepository
) : UseCaseWithParams<GetShoppingListsParam, List<ShoppingList>>() {

    override fun buildUseCase(params: GetShoppingListsParam): List<ShoppingList> {
        val request = if (params.localOnly) {
            shoppingRepository.getShoppingLists(params.userId)
        } else {
            shoppingRepository.getLatestShoppingLists(params.userId)
        }

        return request.sortedBy(params.sorter)
    }
}
