package com.dbeginc.dbshopping.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.AnimRes
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

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
object Navigator {
    fun startActivity(context: Context, intent: Intent) = context.startActivity(intent)

    fun startActivity(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
        activity.finish()
    }

    fun startFragment(fragmentManager: FragmentManager, @IdRes container: Int, fragment: Fragment,
                      @AnimRes enteringAnimation: Int = 0, @AnimRes exitingAnimation: Int = 0,
                      tag: String = "") {

        if (!fragment.isAdded) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(enteringAnimation, exitingAnimation)
                    .replace(container, fragment, tag)
                    .commitAllowingStateLoss()
        }
    }
}