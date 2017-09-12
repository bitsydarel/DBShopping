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

package com.dbeginc.dbshopping.authentication.signup.presenter

import com.dbeginc.dbshopping.authentication.signup.SignUpContract
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.fieldvalidator.IFormValidator
import com.dbeginc.dbshopping.helper.extensions.addTo
import com.dbeginc.dbshopping.mapper.user.toAccount
import com.dbeginc.dbshopping.mapper.user.toUserModel
import com.dbeginc.dbshopping.viewmodels.AccountModel
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.GoogleRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetUser
import com.dbeginc.domain.usecases.user.authentication.CheckIfUserExist
import com.dbeginc.domain.usecases.user.authentication.CreateNewUser
import com.dbeginc.domain.usecases.user.authentication.CreateNewUserWithGoogle
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.subscribers.DisposableSubscriber

class SignUpPresenterImpl(userRepo: IUserRepo, private val authErrorManager: IErrorManager, private val fieldValidator: IFormValidator) : SignUpContract.SignUpPresenter {
    private lateinit var view: SignUpContract.SignUpView
    private val createUser = CreateNewUser(userRepo)
    private val createUserWithGoogle = CreateNewUserWithGoogle(userRepo)
    private val getUser = GetUser(userRepo)
    private val checkUserExist = CheckIfUserExist(userRepo)
    private val subscription = CompositeDisposable()

    override fun bind(view: SignUpContract.SignUpView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        createUser.clean()
        createUserWithGoogle.clean()
        getUser.clean()
        checkUserExist.clean()
        subscription.clear()
    }

    override fun onUserSignUp() {
        when {
            view.getNickname().isEmpty() -> {
                view.displayNicknameInvalidMessage()
                return
            }

            view.getEmail().isEmpty() -> {
                view.displayEmailInvalidMessage()
                return
            }

            view.getPassword().isEmpty() -> {
                view.displayPasswordInvalidMessage()
                return
            }
            else -> {
                view.displaySignUpProgressMessage()
                createUser.execute(AuthRequestModel(view.getEmail(), view.getPassword(), view.getNickname()))
                        .doOnSubscribe { view.displaySignUpProgressMessage() }
                        .doOnTerminate { view.hideSignUpProgressMessage() }
                        .subscribe({ view.goToLoginPage() }, { error -> view.displayErrorMessage(authErrorManager.translateError(error)) })
                        .addTo(subscription)
            }
        }
    }

    override fun onUserSignUpWithGoogle(userId: String, account: AccountModel, idToken: String) {
        view.displaySignUpProgressMessage()
        checkUserExist.execute(UserRequestModel(userId, Unit))
                .doOnSubscribe { view.displaySignUpProgressMessage() }
                .flatMapCompletable { userExit ->
                    if (userExit) Completable.complete().doOnComplete { view.displayUserAlreadyExist() }
                    else createUserWithGoogle.execute(GoogleRequestModel(userId, account.toAccount(),idToken)).doOnComplete { view.onSignUpSuccess() }

                }.doOnTerminate { view.hideSignUpProgressMessage() }
                .subscribe({ /*Not Needed*/ }, { error -> view.displayErrorMessage(authErrorManager.translateError(error)) })
                .addTo(subscription)
    }

    override fun onUserSignUpWithFacebook() {
        view.requestFacebookAccount()
    }

    override fun loadUser(userId: String) {
        getUser.execute(UserRequestModel(userId, Unit))
                .subscribeWith(UserObserver())
                .addTo(subscription)
    }

    override fun getUserGoogleAccount() = view.requestGoogleAccount()

    override fun getUserFacebookAccount() = view.requestFacebookAccount()

    override fun whenUserHasAccount() = view.goToLoginPage()

    override fun onNicknameInputEvent() {
        view.getNicknameInputEvent()
                .map { text -> fieldValidator.validateNickname(text) }
                .subscribe(
                        { valid -> if (valid) view.removeNicknameInvalidMessage() else view.displayNicknameInvalidMessage() },
                        { error -> view.displayErrorMessage(error.localizedMessage) }

                ).addTo(subscription)
    }

    override fun onEmailInputEvent() {
        view.getEmailInputEvent()
                .map { text -> fieldValidator.validateEmailForm(text) }
                .subscribe(
                        { valid -> if (valid) view.removeEmailInvalidMessage() else view.displayEmailInvalidMessage() },
                        { error -> view.displayErrorMessage(error.localizedMessage) }

                ).addTo(subscription)
    }

    override fun onPasswordInputEvent() {
        view.getPasswordInputEvent()
                .map { text -> fieldValidator.validatePasswordForm(text) }
                .subscribe(
                        { valid -> if (valid) view.removePasswordInvalidMessage() else view.displayPasswordInvalidMessage() },
                        { error -> view.displayErrorMessage(error.localizedMessage) }

                ).addTo(subscription)
    }

    override fun onUserInputEvent() {
        Flowable.combineLatest(
                view.getNicknameInputEvent(), view.getEmailInputEvent(), view.getPasswordInputEvent(),
                Function3<String, String, String, Boolean> {
                    nickname, email, password -> fieldValidator.validateEmailPasswordAndNickname(nickname = nickname, email = email, password = password)
                }
        ).subscribe(
                { valid -> if (valid) view.activateSignUp() else view.disableSignUp() },
                { error -> view.displayErrorMessage(error.localizedMessage) }
        ).addTo(subscription)
    }

    private inner class UserObserver : DisposableSubscriber<User>() {
        override fun onNext(user: User) {
            view.setUser(user.toUserModel())
            view.hideSignUpProgressMessage()
            view.goToHomePage()
            dispose()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }
}