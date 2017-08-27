package com.dbeginc.dbshopping.authentication.signup.presenter

import com.dbeginc.dbshopping.authentication.signup.SignUpContract
import com.dbeginc.dbshopping.exception.IErrorManager
import com.dbeginc.dbshopping.fieldvalidator.IFormValidator
import com.dbeginc.domain.entities.requestmodel.AuthRequestModel
import com.dbeginc.domain.entities.requestmodel.UserRequestModel
import com.dbeginc.domain.entities.user.User
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.domain.usecases.user.GetUser
import com.dbeginc.domain.usecases.user.authentication.CheckIfUserExist
import com.dbeginc.domain.usecases.user.authentication.CreateNewUser
import com.dbeginc.domain.usecases.user.authentication.CreateNewUserWithGoogle
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber

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
class SignUpPresenterImpl(userRepo: IUserRepo, private val authErrorManager: IErrorManager, private val fieldValidator: IFormValidator) : SignUpContract.SignUpPresenter {

    private lateinit var view: SignUpContract.SignUpView
    private val createNewUser = CreateNewUser(userRepo)
    private val authenticateWithGoogle = CreateNewUserWithGoogle(userRepo)
    private val getUser = GetUser(userRepo)
    private val checkUserExist = CheckIfUserExist(userRepo)
    private val subscription = CompositeDisposable()

    override fun bind(view: SignUpContract.SignUpView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        createNewUser.dispose()
        authenticateWithGoogle.dispose()
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
                createNewUser.execute(SignUpObserver(), AuthRequestModel(view.getEmail(), view.getPassword(), view.getNickname()))
            }
        }
    }

    override fun onUserSignUpWithGoogle(userId: String, idToken: String) {
        view.displaySignUpProgressMessage()
        checkUserExist.execute(CheckIfUserExist(userId, idToken), UserRequestModel(userId, Unit))
    }

    override fun onUserSignUpWithFacebook() {

    }

    override fun loadUser(userId: String) = getUser.execute(UserObserver(), UserRequestModel(userId, Unit))

    override fun getUserGoogleAccount() = view.requestGoogleAccount()

    override fun getUserFacebookAccount() = view.requestFacebookAccount()

    override fun whenUserHasAccount() = view.goToLoginPage()

    override fun onNicknameInputEvent() {
        subscription.add(
                view.getNicknameInputEvent().map { text -> fieldValidator.validateNickname(text) }
                        .subscribeWith(NicknameObserver())
        )
    }

    override fun onEmailInputEvent() {
        subscription.add(
                view.getEmailInputEvent().map { text -> fieldValidator.validateEmailForm(text) }
                        .subscribeWith(EmailObserver())
        )
    }

    override fun onPasswordInputEvent() {
        subscription.add(
                view.getPasswordInputEvent().map { text -> fieldValidator.validatePasswordForm(text) }
                        .subscribeWith(PasswordObserver())
        )
    }

    override fun onUserInputEvent() {
        subscription.add(
                Flowable.combineLatest(
                        view.getNicknameInputEvent(), view.getEmailInputEvent(), view.getPasswordInputEvent(),
                        Function3<String, String, String, Boolean> {
                            nickname, email, password ->
                            fieldValidator.validateEmailPasswordAndNickname(nickname = nickname, email = email, password = password)
                        }
                ).subscribeWith(LoginButtonObserver())
        )
    }

    private inner class NicknameObserver : DisposableSubscriber<Boolean>() {
        override fun onNext(isValid: Boolean) {
            if (isValid) view.removeNicknameInvalidMessage()
            else view.displayNicknameInvalidMessage()
        }

        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
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
            if (areValid) view.activateSignUp()
            else view.disableSignUp()
        }

        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }

    private inner class CheckIfUserExist(val userId: String, val userIdToken: String) : DisposableSingleObserver<Boolean>() {
        override fun onSuccess(exist: Boolean) {
            if (exist) {
                view.hideSignUpProgressMessage()
                view.displayUserAlreadyExist()

            } else authenticateWithGoogle.execute(GoogleAuthObserver(), UserRequestModel(userId, userIdToken))
        }

        override fun onError(e: Throwable) {
            view.hideSignUpProgressMessage()
            view.displayErrorMessage(e.localizedMessage)
        }
    }

    private inner class GoogleAuthObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.onSignUpSuccess()
            dispose()
        }

        override fun onError(error: Throwable) {
            view.hideSignUpProgressMessage()
            view.displayErrorMessage(authErrorManager.translateError(error))
        }
    }

    private inner class SignUpObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            view.hideSignUpProgressMessage()
            view.goToLoginPage()
            dispose()
        }

        override fun onError(e: Throwable) {
            view.hideSignUpProgressMessage()
            view.displayErrorMessage(authErrorManager.translateError(e))
        }
    }

    private inner class UserObserver : DisposableSubscriber<User>() {

        override fun onNext(user: User) {
            view.setUser(user)
            view.hideSignUpProgressMessage()
            view.goToHomePage()
            dispose()
        }
        override fun onError(error: Throwable) = view.displayErrorMessage(error.localizedMessage)
        override fun onComplete() = dispose()
    }
}