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

package com.dbeginc.dbshopping.home

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
import com.dbeginc.dbshopping.viewmodels.AccountModel
import com.dbeginc.dbshopping.viewmodels.UserModel

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * Created by darel on 22.08.17.
 */
interface HomeContract {

    interface HomeView : IView {

        fun getAppUser() : UserModel

        fun displayAddListPage()

        fun displayUserListPage()

        fun displayTimeLine()

        fun displayUserProfile()

        fun displayHelp()

        fun sendComments()

        fun recommendUs()

        fun confirmLogout()

        fun goToLogin()

        fun showAccount(accountModel: AccountModel)

        fun displayErrorMessage(error: String)
    }

    interface HomePresenter : IPresenter<HomeView> {

        fun onAddListClick()

        fun onUserListClick()

        fun onTimeLine()

        fun onNavigationClick(itemId: Int)

        fun logout()

        fun loadAccount()
    }
}