@file:Suppress("DEPRECATION")

package com.ohayo.core.ui.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import java.io.BufferedReader
import kotlin.math.roundToInt


/**
 * Create by QuyenLX
 */

fun Context.readAsset(fileName: String): String =
    this
        .assets
        .open(fileName)
        .bufferedReader()
        .use(BufferedReader::readText)

fun Context.showToast(message: String?, isShort: Boolean = true) {
    val duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, message, duration).show()
}

fun Context?.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this?.resources?.displayMetrics
    ).roundToInt()
}

fun Context.getDimension(@DimenRes id: Int): Float {
    return resources.getDimension(id)
}

fun Context.getColorAttr(attr: Int): Int {
    val theme = theme
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val color = typedArray.getColor(0, Color.WHITE)
    typedArray.recycle()
    return color
}

fun Context.getDimensionAttr(attr: Int): Int {
    val theme = theme
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val dimen = typedArray.getDimensionPixelSize(0, 0)
    typedArray.recycle()
    return dimen
}

fun Context.getDrawableAttr(attr: Int): Drawable? {
    val theme = theme
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val drawable = typedArray.getDrawable(0)
    typedArray.recycle()
    return drawable
}

fun Context?.showKeyboard(editText: EditText) {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity?.hideSoftKeyboard() {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    var view = this?.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun Activity?.hideKeyboardWhenTouchOutside(view: View?, callback: (() -> Unit)? = null) {
    if (view !is EditText) {
        view?.setOnTouchListener { _, _ ->
            this?.hideSoftKeyboard()
            callback?.invoke()
            return@setOnTouchListener false
        }
    }

    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            hideKeyboardWhenTouchOutside(innerView)
        }
    }
}

fun Activity.setWindowStatusNav(
    @ColorInt statusBarColor: Int? = null,
    @ColorInt navBarColor: Int? = null,
    flagLight: Boolean? = null
) {
    val flags =
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    val uiVisibility: Int =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    window.decorView.systemUiVisibility = uiVisibility

    window.attributes.flags = window.attributes.flags and flags.inv()
    statusBarColor?.let {
        window.statusBarColor = statusBarColor
    }
    navBarColor?.let {
        window.navigationBarColor = navBarColor
    }
    flagLight?.let {
        updateLightStatusBar(flagLight)
    }
}

fun Activity.updateLightStatusBar(flagLight: Boolean = false) {
    val view = this.window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (flagLight) {
            view.systemUiVisibility =
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            view.systemUiVisibility =
                view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

fun Activity.getNavGraph(navController: NavController?, navigation: Int): NavGraph? {
    return try {
        navController?.graph
    } catch (e: IllegalStateException) {
        val inflater = navController?.navInflater
        return inflater?.inflate(navigation)
    }
}

fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun Context.composeEmail(addresses: Array<String?>?, subject: String?) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun Context.dimen(@DimenRes dimen: Int) = resources.getDimensionPixelSize(dimen)