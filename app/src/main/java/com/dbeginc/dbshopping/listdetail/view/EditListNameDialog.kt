package com.dbeginc.dbshopping.listdetail.view

import android.app.Dialog
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.EditListNameLayoutBinding
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.extensions.getInputEvent
import com.dbeginc.dbshopping.helper.extensions.getValue
import com.dbeginc.dbshopping.listdetail.ListDetailContract
import com.dbeginc.dbshopping.viewmodels.ListModel
import io.reactivex.disposables.Disposable

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
 * Created by darel on 25.08.17.
 */
class EditListNameDialog : DialogFragment() {

    private lateinit var binding: EditListNameLayoutBinding
    private lateinit var name: String
    private lateinit var errorMessage: String
    private var disposable: Disposable? = null

    /********************************************************** Android Part Method **********************************************************/

    companion object {
        @JvmStatic fun newInstance(listModel: ListModel) : EditListNameDialog {
            val dialog = EditListNameDialog()
            val args = Bundle()
            args.putString(ConstantHolder.NAME, listModel.name)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectUserComponent(this)
        name = if (savedState == null) arguments.getString(ConstantHolder.NAME)
        else savedState.getString(ConstantHolder.NAME)

        errorMessage = getString(R.string.nameIncorrect)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(ConstantHolder.NAME, binding.editNameET.getValue())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.edit_list_name_layout, null, false)
        binding.editNameET.setText(name)
        disposable = binding.editNameET.getInputEvent()
                .subscribe(
                        { text -> checkUserInput(text) },
                        { error -> Log.e(ConstantHolder.TAG, error.localizedMessage, error) }
                )

        return AlertDialog.Builder(context)
                .setView(binding.root)
                .setPositiveButton(R.string.action_change_list_name) { dialog, _ -> dialog.dismiss() }
                .setNegativeButton(android.R.string.cancel) {dialog, _ -> dialog.cancel() }
                .create()
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        disposable?.dispose()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val container = activity

        if (container is ListDetailContract.ListDetailView) container.changeName(binding.editNameET.getValue())
    }

    private fun checkUserInput(text: String) {
        when {
            text.isEmpty() -> binding.editNameContainerTIL.error = errorMessage
            else -> binding.editNameContainerTIL.isErrorEnabled = false
        }
    }
}