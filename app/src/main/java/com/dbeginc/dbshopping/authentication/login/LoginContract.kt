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

package com.dbeginc.dbshopping.authentication.login

import com.dbeginc.dbshopping.base.IPresenter
import com.dbeginc.dbshopping.base.IView
import com.dbeginc.dbshopping.viewmodels.UserModel
import io.reactivex.Flowable

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
 * Created by darel on 21.08.17.
 */
interface LoginContract {

    interface LoginView : IView {
        /**
         * Get user email
         * @return {@link String}
         */
        fun getEmail() : String

        /**
         * Get user password
         * @return password
         */
        fun getPassword() : String

        /**
         * Get email input event
         * @return event of user his password
         */
        fun getEmailInputEvent() : Flowable<String>

        /**
         * get password input event
         * @return event of user his password
         */
        fun getPasswordInputEvent() : Flowable<String>

        /**
         * display email invalid message
         */
        fun displayEmailInvalidMessage()

        /**
         * remove email invalid message
         */
        fun removeEmailInvalidMessage()

        /**
         * display password invalid message
         */
        fun displayPasswordInvalidMessage()

        /**
         * remove password invalid message
         */
        fun removePasswordInvalidMessage()

        /**
         * activate login
         */
        fun activateLogin()

        /**
         * disable login
         */
        fun disableLogin()

        /**
         * display login in progress message
         */
        fun displayLoginInProgressMessage()

        /**
         * hide login in progress message
         */
        fun hideLoginInProgressMessage()

        /**
         * Request the current
         */
        fun onLoginSuccess()

        /**
         * set the current user in the application
         */
        fun setUser(user: UserModel)

        /**
         * Request the user google account
         * to login in the application
         */
        fun requestGoogleAccount()

        /**
         * Request the user facebook account
         * to login in the application
         */
        fun requestFacebookAccount()

        /**
         * Display error message
         */
        fun displayErrorMessage(error: String)

        /**
         * Go to home view
         */
        fun goToHomePage()

        /**
         * Go to home view
         */
        fun goToSignUpScreen()
    }

    interface LoginPresenter : IPresenter<LoginView> {

        fun loadUser(userId: String)

        /**
         * On user Login action
         */
        fun onUserLogin()

        /**
         * When user select sign up
         */
        fun whenUserHasNoAccount()

        /**
         * Get user google account
         */
        fun getGoogleAccount()

        /**
         * Get user facebook account
         */
        fun getFacebookAccount()

        /**
         * On user Login with google in the application
         */
        fun loginWithGoogle(userId: String, idToken: String)

        /**
         * On user Login with facebook in the application
         */
        fun loginWithFacebook()

        /**
         * Check if user already exist in database
         */
        fun checkIfUserExist(userId: String)


        /**
         * On email input event
         */
        fun onUserEmailInputEvent()

        /**
         * On email input event
         */
        fun onUserPasswordInputEvent()

        fun onUserInputEvent()
    }

}