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

package com.dbeginc.dbshopping.authentication.login.presenter

import com.dbeginc.dbshopping.authentication.login.LoginContract
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.fieldvalidator.IFormValidator
import com.dbeginc.dbshopping.mapper.user.toUserModel
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetUser
import com.dbeginc.domain.usecases.user.authentication.CheckIfUserCanLoginWithAccount
import com.dbeginc.domain.usecases.user.authentication.LoginUser
import com.dbeginc.domain.usecases.user.authentication.LoginWithGoogle
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber

class LoginPresenterImpl(userRepo: IUserRepo, private val authErrorManager: IErrorManager, private val fieldValidator: IFormValidator) : LoginContract.LoginPresenter {
    private lateinit var view: LoginContract.LoginView
    private val subscription = CompositeDisposable()
    private val login = LoginUser(userRepo)
    private val loginWithGoogle = LoginWithGoogle(userRepo)
    private val checkIfUserCanLoginWithAccount = CheckIfUserCanLoginWithAccount(userRepo)
    private val getUser = GetUser(userRepo)

    override fun bind(view: LoginContract.LoginView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        login.dispose()
        loginWithGoogle.dispose()
        getUser.dispose()
        subscription.clear()
    }

    override fun loadUser(userId: String) {
        getUser.execute(UserObserver(), UserRequestModel(userId, Unit))
    }

    override fun onUserLogin() {
        if (view.getEmail().isEmpty()) {
            view.displayEmailInvalidMessage()
            return

        } else if (view.getPassword().isEmpty()){
            view.displayPasswordInvalidMessage()
            return
        }

        view.displayLoginInProgressMessage()
        login.execute(LoginObserver(), AuthRequestModel(view.getEmail(), view.getPassword(), Unit))
    }


    override fun loginWithGoogle(userId: String, accountProvider: String, idToken: String) {
        view.displayLoginInProgressMessage()
        checkIfUserCanLoginWithAccount.execute(LoginWithAccountResponseObserver(userId, idToken), UserRequestModel(userId, accountProvider))
    }

    override fun loginWithFacebook() = view.requestFacebookAccount()

    override fun getGoogleAccount() = view.requestGoogleAccount()

    override fun getFacebookAccount() = view.requestFacebookAccount()

    override fun whenUserHasNoAccount() = view.goToSignUpScreen()

    override fun onUserEmailInputEvent() {
        subscription.add(
                view.getEmailInputEvent().map { text -> fieldValidator.validateEmailForm(text) }
                        .subscribeWith(EmailObserver())
        )
    }

    override fun onUserPasswordInputEvent() {
        subscription.add(
                view.getPasswordInputEvent().map { text -> fieldValidator.validatePasswordForm(text) }
                        .subscribeWith(PasswordObserver())
        )
    }

    override fun onUserInputEvent() {
        subscription.add(
                Flowable.combineLatest(
                        view.getEmailInputEvent(), view.getPasswordInputEvent(),
                        BiFunction<String, String, Boolean> { email, password -> fieldValidator.validateEmailAndPassword(email, password)}
                ).subscribeWith(LoginButtonObserver())
        )
    }

    private inner class EmailObserver : DisposableSubscriber<Boolean>() {
        override fun onNext(isValid: Boolean) {
            if (isValid) view.removeEmailInvalidMessage()
            else view.displayEmailInvalidMessage()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }

    private inner class PasswordObserver : DisposableSubscriber<Boolean>() {
        override fun onNext(isValid: Boolean) {
            if (isValid) view.removePasswordInvalidMessage()
            else view.displayPasswordInvalidMessage()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }

    private inner class LoginButtonObserver : DisposableSubscriber<Boolean>() {
        override fun onNext(areValid: Boolean) {
            if (areValid) view.activateLogin()
            else view.disableLogin()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }

    private inner class LoginWithAccountResponseObserver(val userId: String, val userToken: String) : DisposableSingleObserver<Boolean>() {
        override fun onSuccess(canUserLogin: Boolean) {
            if (canUserLogin) loginWithGoogle.execute(GoogleAccountProvider(), UserRequestModel(userId, userToken))
            else {
                view.hideLoginInProgressMessage()
                view.displayUserDoesNotExist()
            }
            dispose()
        }
        override fun onError(e: Throwable) {
            view.hideLoginInProgressMessage()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class LoginObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.onLoginSuccess()
            dispose()
        }
        override fun onError(e: Throwable) {
            view.hideLoginInProgressMessage()
            view.displayErrorMessage(authErrorManager.translateError(e))
        }
    }

    private inner class GoogleAccountProvider : DisposableCompletableObserver() {
        override fun onComplete() {
            view.onLoginSuccess()
            dispose()
        }
        override fun onError(error: Throwable) {
            view.hideLoginInProgressMessage()
            view.displayErrorMessage(authErrorManager.translateError(error))
        }
    }

    private inner class UserObserver : DisposableSubscriber<User>() {
        override fun onNext(user: User) {
            view.setUser(user.toUserModel())
            view.hideLoginInProgressMessage()
            view.goToHomePage()
            dispose()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }
}