package com.dbeginc.domain.usecases.data.item

import com.dbeginc.domain.entities.requestmodel.ItemRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 21.08.17.
 */
class DeleteItem(private val dataRepo: IDataRepo) : UseCaseCompletable<ItemRequestModel<String>>() {

    override fun buildUseCaseCompletableObservable(params: ItemRequestModel<String>): Completable =
            dataRepo.deleteItem(params)

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}