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

package com.dbeginc.dbshopping.splash.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.authentication.login.view.LoginActivity
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.databinding.ActivitySplashBinding
import com.dbeginc.dbshopping.di.user.module.UserModule
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.home.view.HomeActivity
import com.dbeginc.dbshopping.splash.SplashContract
import com.dbeginc.dbshopping.viewmodels.UserModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

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
 */

class SplashActivity : BaseActivity(), SplashContract.SplashView {
    @Inject lateinit var presenter: SplashContract.SplashPresenter
    private lateinit var binding: ActivitySplashBinding

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectInAppComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.unBind()
    }

    /********************************************************** Splash View Part Method **********************************************************/

    override fun setupView() = presenter.loadUser()

    override fun cleanState() = presenter.unBind()

    override fun isUserLogged(): Boolean = FirebaseAuth.getInstance().currentUser != null

    override fun getUserId(): String {
        val email: String = FirebaseAuth.getInstance().currentUser?.email!!
        return Base64.encodeToString(email.toByteArray(), Base64.NO_WRAP)
    }

    override fun setAppUser(user: UserModel) {
        Injector.userComponent = Injector.appComponent.plus(UserModule(user))
    }

    override fun logError(error: Throwable) {
        Log.e(ConstantHolder.TAG, error.localizedMessage, error)
    }

    override fun goToHome() = Navigator.startActivity(this, Intent(this, HomeActivity::class.java))

    override fun goToLogin() = Navigator.startActivity(this, Intent(this, LoginActivity::class.java))
}
