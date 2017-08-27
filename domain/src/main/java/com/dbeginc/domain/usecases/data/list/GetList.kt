package com.dbeginc.domain.usecases.data.list

import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 20.08.17.
 */
class GetList(private val dataRepo: IDataRepo) : UseCase<ShoppingList, ListRequestModel<Unit>>() {

    override fun buildUseCaseObservable(params: ListRequestModel<Unit>): Flowable<ShoppingList> =
            dataRepo.getList(params)

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}