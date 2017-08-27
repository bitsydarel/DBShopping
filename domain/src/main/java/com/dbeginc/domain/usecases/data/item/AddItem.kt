package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.data.ShoppingItem
import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 20.08.17.
 */
class AddItem(private val dataRepo: IDataRepo) : UseCaseCompletable<ItemRequestModel<ShoppingItem>>() {

    override fun buildUseCaseCompletableObservable(params: ItemRequestModel<ShoppingItem>): Completable =
            dataRepo.addItem(params)

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}