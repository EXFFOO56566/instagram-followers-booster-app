package com.ohayo.core.ui.extensions

import android.app.Activity
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Furuichi on 05/07/2022
 */
fun TextView.fillColorAndFontBy(@FontRes fontId: Int, @ColorRes color: Int, onClicked: (() -> Unit)? = null, vararg arrStr: String) {

    val spannable = SpannableStringBuilder(text)
        .apply {
            arrStr.forEach {
                val start = text.indexOf(it)
                if (start != -1) {
                    val end = start + it.length
                    setFontSpan(context, fontId, start, end)
                    setColorSpan(context, color, start, end)
                    onClicked?.let { safeClicked ->
                        setClickableSpan(start, end,
                            onClickListener = safeClicked,
                            updateDrawStateBlock = { ds ->
                                ds.isUnderlineText = false
                                ds.bgColor = Color.TRANSPARENT
                            }
                        )
                    }
                }
            }
        }
    this.text = spannable
    if (onClicked != null) {
        this.movementMethod = LinkMovementMethod.getInstance()
    }
}

fun EditText.hideKeyboard(clearFocus: Boolean = true) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
    if (clearFocus) {
        this.clearFocus()
        isCursorVisible = false
    }
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun ImageView.setAvatarImage(path: String?) {
    val requestOptions = RequestOptions()
    requestOptions.circleCrop()
    Glide
        .with(context)
        .load(path)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
}

fun ImageView.setImage(path: String?) {
    Glide
        .with(context)
        .load(path)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}
