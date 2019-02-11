/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping.utils.sync

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.SharedPreferences
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.IS_USER_LOGGED
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.USER_ID
import com.dbeginc.domain.Logger
import com.dbeginc.domain.entities.data.ShoppingUser
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Lazy
import dagger.android.AndroidInjection
import io.reactivex.disposables.Disposable
import javax.inject.Inject


/**
 * Created by darel on 14.03.18.
 *
 * Data Push Job
 */
class DataPushJob @Inject constructor() : JobService() {
    @Inject lateinit var dataRepo: Lazy<IDataRepo>
    @Inject lateinit var userRepo: Lazy<IUserRepo>
    @Inject lateinit var preferences: Lazy<SharedPreferences>
    @Inject lateinit var logger: Lazy<Logger>
    private var task: Disposable? = null

    override fun onCreate() {
        super.onCreate()

        AndroidInjection.inject(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        if (preferences.get().getBoolean(IS_USER_LOGGED, false)) {
            val userId = preferences.get().getString(USER_ID, "")

            userRepo.get()
                    .getUser(UserRequestModel(userId, Unit))
                    .flatMapCompletable {
                        dataRepo.get().publishPendingChanges(ShoppingUser(
                                it.uniqueId,
                                it.email,
                                it.nickname,
                                it.avatar,
                                false
                        ))
                    }
                    .andThen(userRepo.get().publishPendingUserChanges())
                    .subscribe(
                            {
                                logger.get().logEvent("Updated data in jobService")
                                jobFinished(params, true)
                            },
                            {
                                logger.get().logError(it)
                                jobFinished(params, true)
                            }
                    )
                    .also { task = it }

            return true
        }
        else return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        task?.dispose()

        return true
    }
}