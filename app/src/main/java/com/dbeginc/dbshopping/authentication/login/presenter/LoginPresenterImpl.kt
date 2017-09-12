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
import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.mapper.user.toUserModel
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetUser
import com.dbeginc.domain.usecases.user.authentication.CheckIfUserCanLoginWithAccount
import com.dbeginc.domain.usecases.user.authentication.LoginUser
import com.dbeginc.domain.usecases.user.authentication.LoginWithGoogle
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
        login.clean()
        loginWithGoogle.clean()
        checkIfUserCanLoginWithAccount.clean()
        getUser.clean()
        subscription.clear()
    }

    override fun loadUser(userId: String) {
        getUser.execute(UserRequestModel(userId, Unit))
                .subscribeWith(UserObserver())
                .addTo(subscription)
    }

    override fun onUserLogin() {
        if (view.getEmail().isEmpty()) {
            view.displayEmailInvalidMessage()
            return

        } else if (view.getPassword().isEmpty()){
            view.displayPasswordInvalidMessage()
            return
        }

        login.execute(AuthRequestModel(view.getEmail(), view.getPassword(), Unit))
                .doOnSubscribe { view.displayLoginInProgressMessage() }
                .doOnTerminate { view.hideLoginInProgressMessage() }
                .subscribe({ view.onLoginSuccess() }, { error -> view.displayErrorMessage(authErrorManager.translateError(error)) })
                .addTo(subscription)
    }


    override fun loginWithGoogle(userId: String, accountProvider: String, idToken: String) {
        checkIfUserCanLoginWithAccount.execute(UserRequestModel(userId, accountProvider))
                .doOnSubscribe { _ -> view.displayLoginInProgressMessage() }
                .flatMapCompletable { canUserLogin ->
                    if (canUserLogin) loginWithGoogle.execute(UserRequestModel(userId, idToken)).doOnComplete { view.onLoginSuccess() }
                    else Completable.complete().doOnComplete { view.displayUserDoesNotExist() }

                }.doOnTerminate { view.hideLoginInProgressMessage() }
                .subscribe({ /*Nothing to do*/ }, { error -> view.displayErrorMessage(authErrorManager.translateError(error)) })
                .addTo(subscription)
    }

    override fun loginWithFacebook() = view.requestFacebookAccount()

    override fun getGoogleAccount() = view.requestGoogleAccount()

    override fun getFacebookAccount() = view.requestFacebookAccount()

    override fun whenUserHasNoAccount() = view.goToSignUpScreen()

    override fun onUserEmailInputEvent() {
        view.getEmailInputEvent().map { text -> fieldValidator.validateEmailForm(text) }
                .subscribe(
                        { valid -> if (valid) view.removeEmailInvalidMessage() else view.displayEmailInvalidMessage() },
                        { error -> view.displayErrorMessage(error.localizedMessage)
                })
                .addTo(subscription)
    }

    override fun onUserPasswordInputEvent() {
        view.getPasswordInputEvent().map { text -> fieldValidator.validatePasswordForm(text) }
                .subscribe (
                        { valid -> if (valid) view.removePasswordInvalidMessage() else view.displayPasswordInvalidMessage()},
                        { error -> view.displayErrorMessage(error.localizedMessage) }
                ).addTo(subscription)
    }

    override fun onUserInputEvent() {
        Flowable.combineLatest(
                view.getEmailInputEvent(), view.getPasswordInputEvent(),
                BiFunction<String, String, Boolean> { email, password -> fieldValidator.validateEmailAndPassword(email, password)}
        ).subscribe(
                { valid -> if (valid) view.activateLogin() else view.disableLogin() },
                { error -> view.displayErrorMessage(error.localizedMessage) }
        ).addTo(subscription)
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