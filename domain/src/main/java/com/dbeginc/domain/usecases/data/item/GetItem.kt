package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 21.08.17.
 */
class GetItem(private val dataRepo: IDataRepo) : UseCase<ShoppingItem, ItemRequestModel<String>>() {

    override fun buildUseCaseObservable(params: ItemRequestModel<String>): Flowable<ShoppingItem> =
            dataRepo.getItem(params)

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}