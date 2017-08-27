package com.dbeginc.domain.usecases.data.list

import com.dbeginc.domain.entities.data.ShoppingList
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 20.08.17.
 */
class GetAllList(private val dataRepo: IDataRepo) : UseCase<List<ShoppingList>, Unit>() {

    override fun buildUseCaseObservable(params: Unit): Flowable<List<ShoppingList>> =
            dataRepo.getAllList()

    override fun dispose() {
        super.dispose()
        dataRepo.clean()
    }
}