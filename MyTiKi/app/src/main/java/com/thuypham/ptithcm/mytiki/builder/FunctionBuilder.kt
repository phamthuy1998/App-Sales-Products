package com.thuypham.ptithcm.mytiki.builder

import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

inline fun popupFunctionQueue(init: FunctionBuilder.PopupBuilder.() -> Unit) =
    FunctionBuilder.PopupBuilder().apply(init).build()

inline fun toolbarFunctionQueue(init: FunctionBuilder.ToolbarBuilder.() -> Unit) =
    FunctionBuilder.ToolbarBuilder().apply(init).build()

inline fun recyclerViewFunctionQueue(init: FunctionBuilder.RecyclerViewBuilder.() -> Unit): ArrayList<(View?) -> Unit>? =
    FunctionBuilder.RecyclerViewBuilder().apply(init).build()

inline fun adapterFunctionQueue(init: FunctionBuilder.AdapterBuilder.() -> Unit): ((RecyclerView.ViewHolder?, Int) -> Unit)? =
    FunctionBuilder.AdapterBuilder().apply(init).build()

interface FunctionBuilder {
    class PopupBuilder {
        private var funcQueue: ArrayList<((popupBinding: ViewDataBinding?, view: View?, dialog: Dialog?) -> Unit)>? =
            arrayListOf()

        fun func(messageFunc: ((popupBinding: ViewDataBinding?, view: View?, dialog: Dialog?) -> Unit)): PopupBuilder {
            this.funcQueue?.plusAssign(messageFunc)
            return this
        }

        fun build(): ArrayList<((popupBinding: ViewDataBinding?, view: View?, dialog: Dialog?) -> Unit)> {
            return this.funcQueue ?: arrayListOf()
        }
    }

    class ToolbarBuilder {
        private var funcQueue: ArrayList<((activity: AppCompatActivity?, toolbar: Toolbar?) -> Unit)>? = arrayListOf()

        fun func(messageFunc: ((activity: AppCompatActivity?, toolbar: Toolbar?) -> Unit)): ToolbarBuilder {
            this.funcQueue?.plusAssign(messageFunc)
            return this
        }

        fun build(): ArrayList<((activity: AppCompatActivity?, toolbar: Toolbar?) -> Unit)> {
            return this.funcQueue ?: arrayListOf()
        }
    }

    class RecyclerViewBuilder {
        private var funcQueue: ArrayList<((view: View?) -> Unit)>? = arrayListOf()

        fun func(messageFunc: ((view: View?) -> Unit)): RecyclerViewBuilder {
            this.funcQueue?.plusAssign(messageFunc)
            return this
        }

        fun build(): ArrayList<((view: View?) -> Unit)> {
            return this.funcQueue ?: arrayListOf()
        }
    }

    class AdapterBuilder {
        private var funcQueue: (((holder: RecyclerView.ViewHolder?, Int) -> Unit))? = null

        fun func(messageFunc: ((holder: RecyclerView.ViewHolder?, Int) -> Unit)): AdapterBuilder {
            this.funcQueue = messageFunc
            return this
        }

        fun build(): ((holder: RecyclerView.ViewHolder?, Int) -> Unit)? {
            return this.funcQueue
        }
    }
}