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

package com.dbeginc.dbshopping.authentication.signup

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
interface SignUpContract {

    interface SignUpView : IView {

        /**
         * Get user nickname
         * @return password
         */
        fun getNickname() : String

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
         * Get nickname input event
         * @return event of user his password
         */
        fun getNicknameInputEvent() : Flowable<String>

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
         * display nickname invalid message
         */
        fun displayNicknameInvalidMessage()

        /**
         * remove nickname invalid message
         */
        fun removeNicknameInvalidMessage()

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
         * activate Sign Up
         */
        fun activateSignUp()

        /**
         * disable Sign Up
         */
        fun disableSignUp()

        /**
         * display login in progress message
         */
        fun displaySignUpProgressMessage()

        /**
         * hide login in progress message
         */
        fun hideSignUpProgressMessage()

        /**
         * Sign Up Succeed
         */
        fun onSignUpSuccess()

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
         * Go to home screen
         */
        fun goToHomePage()

        /**
         * Go to login screen
         */
        fun goToLoginPage()

        fun displayUserAlreadyExist()
    }

    interface SignUpPresenter : IPresenter<SignUpView> {

        /**
         * On user Sign Up in the application
         */
        fun onUserSignUp()

        /**
         * On user Sign Up with google in the application
         */
        fun onUserSignUpWithGoogle(userId: String, idToken: String)

        /**
         * On user Sign Up with facebook in the application
         */
        fun onUserSignUpWithFacebook()

        /**
         * Get User google account
         */
        fun getUserGoogleAccount()

        /**
         * Get User facebook account
         */
        fun getUserFacebookAccount()

        /**
         * On nickname input event
         */
        fun onNicknameInputEvent()

        /**
         * On email input event
         */
        fun onEmailInputEvent()

        /**
         * On email input event
         */
        fun onPasswordInputEvent()

        fun onUserInputEvent()

        fun loadUser(userId: String)

        /**
         * When User already has an account
         */
        fun whenUserHasAccount()

    }

}