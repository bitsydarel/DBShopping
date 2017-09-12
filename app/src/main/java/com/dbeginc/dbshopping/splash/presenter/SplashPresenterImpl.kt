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

package com.dbeginc.dbshopping.splash.presenter

import com.dbeginc.dbshopping.mapper.user.toUserModel
import com.dbeginc.dbshopping.splash.SplashContract
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetUser
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 21.08.17.
 *
 * Splash Screen Presenter
 */
class SplashPresenterImpl(userRepo: IUserRepo) : SplashContract.SplashPresenter {
    private lateinit var view: SplashContract.SplashView
    private val getUser = GetUser(userRepo)
    private val tasks = CompositeDisposable()

    override fun bind(view: SplashContract.SplashView) {
        this.view = view
        this.view.setupView()
    }

    override fun loadUser() {
        if (view.isUserLogged()) {
            getUser.execute(UserRequestModel(view.getUserId(), Unit))
                    .subscribe(
                            { user -> view.setAppUser(user.toUserModel()); view.goToHome() },
                            { error -> view.logError(error); view.goToLogin() }
                    )
        } else view.goToLogin()
    }

    override fun unBind() {
        getUser.clean()
        tasks.clear()
    }
}