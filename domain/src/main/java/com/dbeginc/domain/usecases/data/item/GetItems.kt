package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 21.08.17.
 */
class GetItems(private val dataRepo: IDataRepo) : UseCase<List<ShoppingItem>, ItemRequestModel<Unit>>() {

    override fun buildUseCaseObservable(params: ItemRequestModel<Unit>): Flowable<List<ShoppingItem>> =
            dataRepo.getItems(params)

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}