package com.lxquyen.instabooster.ui.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ohayo.core.ui.base.CoreActivity
import com.ohayo.core.ui.extensions.setWindowStatusNav

/**
 * Created by Furuichi on 4/7/2022
 */
abstract class BaseActivity<VB : ViewBinding>(_binding: (LayoutInflater) -> VB) : CoreActivity<VB>(_binding) {

    protected val scope: LifecycleCoroutineScope
        get() {
            return lifecycleScope
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStatusNav(Color.TRANSPARENT, flagLight = false)
    }
}