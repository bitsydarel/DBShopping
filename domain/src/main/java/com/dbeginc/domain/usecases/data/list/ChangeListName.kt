package com.dbeginc.domain.usecases.data.list

import com.dbeginc.domain.entities.requestmodel.ListRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 20.08.17.
 */
class ChangeListName(private val dataRepo: IDataRepo) : UseCaseCompletable<ListRequestModel<String>>() {

    override fun buildUseCaseCompletableObservable(params: ListRequestModel<String>): Completable =
            dataRepo.changeListName(params)
}