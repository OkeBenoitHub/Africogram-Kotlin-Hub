package com.africogram.www.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.africogram.www.R
import com.google.android.material.snackbar.Snackbar
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainUtil {
    private var mSharedPreferences: SharedPreferences? = null

    /**
     * Detect Night mode
     */
    private fun detectNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    /**
     * Show toast message
     */
    fun showToastMessage(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(0, 0, 0)
        toast.show()
    }

    /**
     * Set bottom bar navigation background color
     */
    fun setBottomBarNavigationBackgroundColor(
        window: Window,
        context: Context,
        defaultSystemBgColor: Int,
        darkModeSystemColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(context, defaultSystemBgColor)
        }


        if (detectNightMode(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(
                context,
                darkModeSystemColor
            )
        }
    }

    /**
     * Set Action Bar background color
     * @param context :: context
     * @param actionBar :: action bar
     * @param bgColorRes :: color int from resources
     */
    fun setActionBarBackgroundColor(context: Context, actionBar: ActionBar?, bgColorRes: Int) {
        // Define ActionBar object;
        if (actionBar == null) return
        // Define ColorDrawable object and color res int
        // with color int res code as its parameter
        val colorDrawable = ColorDrawable(ContextCompat.getColor(context, bgColorRes))

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable)
    }

    /**
     * Capitalize each word from string
     * @param stringInput :: the string to transform
     * @return new string with each word capitalized
     */
    fun capitalizeEachWordFromString(stringInput: String): String {
        val strArray = stringInput.toLowerCase(Locale.getDefault()).split(" ".toRegex()).toTypedArray()
        val builder = StringBuilder()
        for (s in strArray) {
            val cap = s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1)
            builder.append(cap).append(" ")
        }
        return builder.toString()
    }

    /**
     * This method is used for checking valid email id format.
     *
     * @param email to check for
     * @return boolean true for valid false for invalid
     */
    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * Get random number from range
     * @param min :: minimum
     * @param max :: maximum
     */
    fun getRandomNumber(min: Int, max: Int): Int {
        return (min..max).random()
    }

    /**
     * Compose email intent
     * @param addresses :: address to send email to
     * @param subject :: email subject
     */
    fun composeEmail(
        context: Context,
        addresses: Array<String?>?,
        subject: String?,
        message: String?,
        sharerTitle: String?
    ) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val chooser = Intent.createChooser(intent, sharerTitle)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(chooser)
        }
    }

    /*
     * This method checks for a valid name :: contains only letters
     * @param name :: name input
     * @return true or false
     */
    fun isValidName(name: String): Boolean {
        val nameRegX = Regex("^[\\p{L} .'-]+$")
        return name.matches(nameRegX)
    }

    /**
     * Get number of column width for Grid layout auto fit
     */
    fun getNumbColumnsForGridLayoutAutoFit(context: Context, columnWidthDp: Float): Int {
        // For example columnWidthDp=180
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    /**
     * Share Text data through app
     * @param shareAboutTitle :: title of share dialog
     * @param textToShare :: text data to share
     */
    fun shareTextData(context: Context, shareAboutTitle: String?, textToShare: String?) {
        val mimeType = "text/plain"

        // Use ShareCompat.IntentBuilder to build the Intent and start the chooser
        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */ShareCompat.IntentBuilder /* The from method specifies the Context from which this share is coming from */
            .from((context as Activity?)!!)
            .setType(mimeType)
            .setChooserTitle(shareAboutTitle)
            .setText(textToShare)
            .startChooser()
    }

    /**
     * Open web page
     * url: page url to load or open
     */
    fun openWebPage(context: Context, url: String) {
        val webPage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }


    /**
     * Display snack bar message
     * @param contextView :: coordinatorLayout root view
     * @param messageResId :: message resource id string
     */
    fun displaySnackBarMessage(
        contextView: CoordinatorLayout?,
        messageResId: Int,
        snackBarDuration: Int
    ): Snackbar {
        val snackBar = Snackbar.make(
            contextView!!,
            messageResId, snackBarDuration
        )
        snackBar.show()
        return snackBar
    }
}