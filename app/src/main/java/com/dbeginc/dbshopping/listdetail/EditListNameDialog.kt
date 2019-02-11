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

package com.dbeginc.dbshopping.listdetail

import android.app.Dialog
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.databinding.EditListNameLayoutBinding
import com.dbeginc.dbshopping.utils.extensions.getInputEvent
import com.dbeginc.dbshopping.utils.extensions.getValue
import io.reactivex.disposables.Disposable

/**
 * Created by darel on 25.08.17.
 *
 * Edit List Name
 */
class EditListNameDialog : DialogFragment(), TextView.OnEditorActionListener {
    private lateinit var binding: EditListNameLayoutBinding
    private lateinit var disposable: Disposable

    interface ListNameChangeListener {
        fun onListNameChanged(newName: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context).cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.edit_list_name_layout,
                null,
                false
        )

        binding.editNameET.setOnEditorActionListener(this)

        binding.editNameET
                .getInputEvent()
                .subscribe(this::checkUserInput)
                .also { disposable = it }

        return AlertDialog.Builder(context!!)
                .setView(binding.root)
                .setPositiveButton(R.string.action_change) { dialog, _ -> dialog.dismiss() }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposable.dispose()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            if (changeName()) {
                dismiss()

                return true
            }
        }

        return false
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        changeName()
    }

    private fun changeName() : Boolean {
        val newName = binding.editNameET.getValue()

        return if (newName.isNotBlank()) {
            val nameChangeListener : ListNameChangeListener = parentFragment as ListNameChangeListener

            nameChangeListener.onListNameChanged(newName)
            true
        }
        else false
    }

    private fun checkUserInput(text: String) {
        when {
            text.isBlank() -> binding.editListNameContainer.error = getString(R.string.invalid_name)
            else -> binding.editListNameContainer.error = null
        }
    }

}