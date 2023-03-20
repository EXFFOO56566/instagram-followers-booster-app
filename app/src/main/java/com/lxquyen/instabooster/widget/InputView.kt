package com.lxquyen.instabooster.widget

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.core.widget.doOnTextChanged
import com.lxquyen.instabooster.R
import com.lxquyen.instabooster.databinding.ViewInputBinding
import com.ohayo.core.ui.extensions.customViewInflate
import com.ohayo.core.ui.extensions.showKeyboard

class InputView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val binding: ViewInputBinding = customViewInflate(ViewInputBinding::inflate)

    var label: String? = ""
        set(value) {
            field = value
            binding.tvLabel.text = value
        }

    var hint: String? = ""
        set(value) {
            field = value
            binding.editText.hint = value
        }

    var text: String
        get() = binding.editText.text.toString()
        set(value) = binding.editText.setText(value)

    var doOnTextChanged: ((String) -> Unit)? = null
        set(value) {
            field = value
            value?.let { safeCallback ->
                binding.editText.doOnTextChanged { text, _, _, _ ->
                    safeCallback.invoke(text.toString())
                }
            }
        }

    init {
        setupAttrs(context, attrs)
    }

    fun showKeyboard() {
        binding.editText.requestFocus()
        context.showKeyboard(binding.editText)
    }

    private fun setupAttrs(context: Context?, attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.InputView)
            ?.apply {
                this.getString(R.styleable.InputView_iv_label).also {
                    this@InputView.label = it
                }

                this.getString(R.styleable.InputView_iv_hint).also {
                    this@InputView.hint = it
                }

                this.getInt(R.styleable.InputView_android_inputType, EditorInfo.TYPE_NULL).let {
                    if (it != EditorInfo.TYPE_NULL) {
                        binding.editText.inputType = it
                    }
                }
            }
            ?.recycle()
    }
}