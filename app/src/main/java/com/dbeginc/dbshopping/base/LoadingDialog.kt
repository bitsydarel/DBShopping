package com.dbeginc.dbshopping.base

import android.app.Dialog
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.LoadingDialogBinding
import com.dbeginc.dbshopping.helper.Injector
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
 *
 * Created by darel on 22.08.17.
 *
 * Loading Progress Dialog
 */
class LoadingDialog : DialogFragment() {
    @Inject lateinit var resource: Resources
    private lateinit var message: String
    private lateinit var binding: LoadingDialogBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectInAppComponent(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.loading_dialog, null, false)

        binding.loadingMessage.text = message

        return AlertDialog.Builder(context)
                .setView(binding.root)
                .setCancelable(false)
                .create()
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setMessage(@StringRes message: Int) {
        this.message = resource.getString(message)
    }

}