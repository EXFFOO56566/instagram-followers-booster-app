package com.ohayo.core.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ohayo.core.ui.extensions.autoCleared

/**
 * Created by Furuichi on 4/7/2022
 */
abstract class CoreDialogFragment<VM : CoreViewModel, VB : ViewBinding>(private val _binding: (LayoutInflater, ViewGroup?, Boolean) -> VB) : DialogFragment() {

    protected var viewBinding by autoCleared<VB>()

    protected val scope: LifecycleCoroutineScope
        get() {
            return viewLifecycleOwner.lifecycleScope
        }

    abstract val viewModel: VM

    abstract fun onFragmentCreated(view: View, savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("🤣 ${this.javaClass.simpleName} #${this.hashCode()} onCreateView()")
        viewBinding = _binding(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("🤩 ${this.javaClass.simpleName} #${this.hashCode()}  onViewCreated() view: $view")
        onFragmentCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("🥰 ${this.javaClass.simpleName} #${this.hashCode()}   onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("😀 ${this.javaClass.simpleName} #${this.hashCode()}  onCreate()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("🥵 ${this.javaClass.simpleName} #${this.hashCode()}  onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("🥶 ${this.javaClass.simpleName} #${this.hashCode()}  onDestroy()")
    }

    override fun onDetach() {
        super.onDetach()
        println("💀 ${this.javaClass.simpleName} #${this.hashCode()} onDetach()")
    }

    override fun onResume() {
        super.onResume()
        println("🎃 ${this.javaClass.simpleName} #${this.hashCode()} onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("😱 ${this.javaClass.simpleName} #${this.hashCode()} onPause()")
    }
}