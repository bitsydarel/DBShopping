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

package com.dbeginc.dbshopping.di.application.module

import com.dbeginc.dbshopping.splash.SplashContract
import com.dbeginc.dbshopping.splash.presenter.SplashPresenterImpl
import com.dbeginc.domain.repositories.IUserRepo
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 27.08.17.
 */
@Module
class PresentationModule {
    @Provides
    internal fun provideSplashPresenter(userRepo: IUserRepo) : SplashContract.SplashPresenter = SplashPresenterImpl(userRepo)
}