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
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.USER_ID
import com.dbeginc.domain.Logger
import com.dbeginc.domain.entities.request.UserRequestModel
import com.dbeginc.domain.repositories.IDataRepo
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Lazy
import dagger.android.AndroidInjection
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by darel on 17.03.18.
 *
 * User Availability Job
 */
class UserAvailabilityJob @Inject constructor() : JobService() {
    @Inject lateinit var userRepo: Lazy<IUserRepo>
    @Inject lateinit var dataRepo: Lazy<IDataRepo>
    @Inject lateinit var preferences: Lazy<SharedPreferences>
    @Inject lateinit var logger: Lazy<Logger>
    private lateinit var task: Disposable

    override fun onCreate() {
        super.onCreate()

        AndroidInjection.inject(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        userRepo.get()
                .verifyIfUserStillIn()
                .flatMapCompletable { isUserStillValid ->
                    if (isUserStillValid) Completable.complete()
                    else {
                        val userId = preferences.get().getString(USER_ID, "")

                        userRepo.get()
                                .logoutUser(UserRequestModel(userId, Unit))
                                .andThen(Completable.fromAction { preferences.get().edit().putBoolean(ConstantHolder.IS_USER_LOGGED, false).apply() })
                                .andThen(dataRepo.get().deleteAll())
                    }
                }
                .subscribe(
                        {
                            logger.get().logEvent("User availability check done in jobService")
                            jobFinished(params, true)
                        },
                        {
                            logger.get().logError(it)
                            jobFinished(params, true)
                        }
                ).also { task = it }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        if (!task.isDisposed) task.dispose()

        return true
    }

}