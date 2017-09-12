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

package com.dbeginc.dbshopping.home.presenter

import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.home.HomeContract
import com.dbeginc.dbshopping.mapper.user.toAccountModel
import com.dbeginc.domain.entities.requestmodel.AccountRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetAccount
import com.dbeginc.domain.usecases.user.authentication.LogoutUser
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 22.08.17.
 *
 * Home Presenter Implementation
 */
class HomePresenterImpl(userRepo: IUserRepo) : HomeContract.HomePresenter {
    private lateinit var view: HomeContract.HomeView
    private val subscriptions = CompositeDisposable()
    private val logout = LogoutUser(userRepo)
    private val getAccount  = GetAccount(userRepo)

    override fun bind(view: HomeContract.HomeView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        logout.clean()
        subscriptions.clear()
    }

    override fun loadAccount() {
        getAccount.execute(AccountRequestModel(view.getAppUser().id, Unit))
                .subscribe(
                        { account -> view.showAccount(account.toAccountModel()) },
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                )
    }

    override fun onAddListClick() = view.displayAddListPage()

    override fun onUserListClick() = view.displayUserListPage()

    override fun onTimeLine() = view.displayTimeLine()

    override fun logout() {
        logout.execute(UserRequestModel(view.getAppUser().id, Unit))
                .subscribe({ view.goToLogin() }, { error -> view.displayErrorMessage(error.localizedMessage) })
                .addTo(subscriptions)
    }

    override fun onNavigationClick(itemId: Int) {
        when(itemId) {
            R.id.action_open_profile -> view.displayUserProfile()
            R.id.action_open_help -> view.displayHelp()
            R.id.action_send_comments -> view.sendComments()
            R.id.action_recommend_us -> view.recommendUs()
            R.id.action_logout_user -> view.confirmLogout()
        }
    }
}