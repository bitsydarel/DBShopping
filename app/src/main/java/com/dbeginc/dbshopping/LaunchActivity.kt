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

package com.dbeginc.dbshopping

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dbeginc.dbshopping.authentication.forgotpassword.ForgotPasswordFragment
import com.dbeginc.dbshopping.authentication.login.LoginFragment
import com.dbeginc.dbshopping.authentication.register.RegisterFragment
import com.dbeginc.dbshopping.splash.SplashFragment
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.SHOULD_SHOW_REGISTER


/**
 * Authentication Activity
 *
 * Main Container for authentication views
 */
class LaunchActivity : AppCompatActivity() {
    private val splashScreen by lazy { SplashFragment() }
    private val loginScreen by lazy { LoginFragment() }
    private val registerScreen by lazy { RegisterFragment() }
    private val forgotPasswordScreen by lazy { ForgotPasswordFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        if (intent.getBooleanExtra(SHOULD_SHOW_REGISTER, false)) goToLoginScreen()
        else if (savedInstanceState == null)  goToSplashScreen()

    }

    fun goToLoginScreen() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.launchContent, loginScreen, LoginFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun goToRegisterScreen() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.launchContent, registerScreen, RegisterFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun goToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goToSplashScreen() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.launchContent, splashScreen, SplashFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    fun goToForgotPasswordScreen() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.launchContent, forgotPasswordScreen, ForgotPasswordFragment::class.java.simpleName)
        fragmentTransaction.commit()
    }
}
