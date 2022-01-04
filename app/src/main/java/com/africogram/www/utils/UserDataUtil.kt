package com.africogram.www.utils

import android.content.Context
import android.widget.EditText
import com.africogram.www.R
import com.africogram.www.firebase.FireAuthBase
import com.africogram.www.viewmodels.SignInViewModel
import com.africogram.www.viewmodels.SignUpViewModel
import java.util.*

/**
 * User Data Util :: contain every recurring task dealing with user data
 */
class UserDataUtil(val context: Context) {
    private val user = FireAuthBase(context).getCurrentUser()
    val lastOnlineTime = Date().time
    val lastOnlineDate = DateTimeUtil().getDateFromTimestampInMilliseconds(lastOnlineTime)

    /**
     * Get user profile info
     */
    private var _userEmail: String? = null
    val userEmail: String get() = _userEmail.toString()

    private var _userId: String? = null
    val userId: String get() = _userId.toString()

    init {
        user?.let {
            _userEmail = user.email
            _userId = user.uid
        }
    }

    /**
     * Check for user valid name input
     */
    private fun checkForUserValidName(userInputName: String, userInputNameEdt: EditText): Boolean {
        if (userInputName.isEmpty()) {
            // empty user input name
            userInputNameEdt.error = context.getString(R.string.empty_name_error_text)
            return false
        } else if (!MainUtil().isValidName(userInputName)) {
            // invalid user input name
            userInputNameEdt.error = context.getString(R.string.invalid_name_error_text)
            return false
        } else if (userInputName.length < 2) {
            // short user input name
            userInputNameEdt.error = context.getString(R.string.too_short_name_error_text)
            return false
        }
        return true
    }

    /**
     * Check for user valid email input
     */
    fun checkForUserValidEmailInput(userEmailInput: String, userEmailInputEdt: EditText): Boolean {
        if (userEmailInput.isEmpty()) {
            // empty user email input
            userEmailInputEdt.error = context.getString(R.string.empty_email_error)
            return false
        } else if (!MainUtil().isEmailValid(userEmailInput)) {
            // invalid user email input format
            userEmailInputEdt.error = context.getString(R.string.invalid_email_error)
            return false
        }
        return true
    }

    /**
     * Check for user valid password input
     */
    private fun checkForUserPasswordInput(userPasswordInput: String, userPasswordInputEdt: EditText): Boolean {
        if (userPasswordInput.isEmpty()) {
            // empty user password input
            userPasswordInputEdt.error = context.getString(R.string.empty_password_error)
            return false
        } else if(userPasswordInput.length < 6) {
            userPasswordInputEdt.error = context.getString(R.string.password_too_short_error)
            return false
        }
        return true
    }

    /**
     * Check for user full birthday input
     */
    private fun checkForUserFullBirthdayInput(
        birthMonthInput: String,
        birthMonthInputEdt: EditText,
        birthDayInput: String,
        birthDayInputEdt: EditText,
        birthYearInput: String,
        birthYearInputEdt: EditText
    ): Boolean {
        // check for birth month input
        val birthMonthInputValue = birthMonthInput.toIntOrNull()
        if (birthMonthInputValue == null) {
            // input month is empty
            birthMonthInputEdt.error = context.getString(R.string.empty_month_error)
            return false
        } else if (birthMonthInputValue < 1 || birthMonthInputValue > 12) {
            // input month is not between 1 and 12
            birthMonthInputEdt.error = context.getString(R.string.invalid_month_error)
            return false
        }
        // check for birth day input
        val birthDayInputValue = birthDayInput.toIntOrNull()
        if (birthDayInputValue == null) {
            // input day is empty
            birthDayInputEdt.error = context.getString(R.string.empty_day_error)
            return false
        } else if(birthDayInputValue < 1 || birthDayInputValue > 31) {
            // input day is not between 1 and 31
            birthDayInputEdt.error = context.getString(R.string.invalid_day_error)
            return false
        }
        // check for birth year input
        val birthYearInputValue = birthYearInput.toIntOrNull()
        if (birthYearInputValue == null) {
            // input year is empty
            birthYearInputEdt.error = context.getString(R.string.empty_year_error)
            return false
        }
        // compute user age for data checking...
        val userCurrentAge = Calendar.getInstance().get(Calendar.YEAR) - birthYearInputValue
        if (userCurrentAge < 13) {
            // user age is below 13 :: not allowed
            birthYearInputEdt.error = context.getString(R.string.under_13_age_error)
            return false
        } else if (userCurrentAge > 100) {
            // user age is above 100 :: not real
            birthYearInputEdt.error = context.getString(R.string.invalid_year_error)
            return false
        }
        return true
    }

    /**
     * Check for user input errors before
     * signing up new user
     */
    fun checkForUserInputDataErrorsBeforeSigningUp(
        firstNameEdt: EditText,
        lastNameEdt: EditText,
        emailEdt: EditText,
        passwordEdt: EditText,
        birthMonthEdt: EditText,
        birthDayEdt: EditText,
        birthYearEdt: EditText,
        signUpViewModel: SignUpViewModel
    ): Boolean {
        // check for user first name value
        var userFirstNameValue = firstNameEdt.text.toString().trim()
        if (!checkForUserValidName(
                userFirstNameValue,
                firstNameEdt
            )) {
            return true
        } else {
            // capitalize first name and store in view model
            userFirstNameValue = MainUtil().capitalizeEachWordFromString(userFirstNameValue)
            signUpViewModel.userFirstName = userFirstNameValue
        }

        // check for user last name value
        var userLastNameValue = lastNameEdt.text.toString().trim()
        if (!checkForUserValidName(
                userLastNameValue,
                lastNameEdt
            )) {
            return true
        } else {
            // capitalize last name and store in view model
            userLastNameValue = MainUtil().capitalizeEachWordFromString(userLastNameValue)
            signUpViewModel.userLastName = userLastNameValue
        }

        // check for user valid email value
        val userEmailValue = emailEdt.text.toString().trim()
        if (!checkForUserValidEmailInput(
                userEmailValue,
                emailEdt
            )) {
            return true
        } else {
            // store user email in view model
            signUpViewModel.userEmail = userEmailValue
        }

        // check for user valid password value
        val userPasswordValue = passwordEdt.text.toString()
        if (!checkForUserPasswordInput(
                userPasswordValue,
                passwordEdt
            )) {
            return true
        } else {
            // store user password in view model
            signUpViewModel.userPassword = userPasswordValue
        }

        // check for user full birthday value
        val userBirthMonthValue = birthMonthEdt.text.toString().trim().trimStart(
            '0'
        )
        val userBirthDayValue = birthDayEdt.text.toString().trim().trimStart(
            '0'
        )
        val userBirthYearValue = birthYearEdt.text.toString().trim().trimStart(
            '0'
        )
        if (!checkForUserFullBirthdayInput(
                userBirthMonthValue,
                birthMonthEdt,
                userBirthDayValue,
                birthDayEdt,
                userBirthYearValue,
                birthYearEdt
            )) {
            return true
        } else {
            // store user full birthday in view model
            signUpViewModel.userBirthMonth = userBirthMonthValue.toInt()
            signUpViewModel.userBirthDay = userBirthDayValue.toInt()
            signUpViewModel.userBirthYear = userBirthYearValue.toInt()
        }

        // check for user gender
        if (signUpViewModel.userGender == null) {
            MainUtil().showToastMessage(
                context,
                context.getString(R.string.not_selected_gender_error)
            )
            return true
        }

        // check for user terms and privacy checked
        if (!signUpViewModel.termsPrivacyChecked) {
            MainUtil().showToastMessage(
                context,
                context.getString(R.string.agree_terms_privacy_error)
            )
            return true
        }
        return false
    }

    /**
     * Check for user input errors before
     * signing in existing user
     */
    fun checkForUserInputDataErrorsBeforeSigningIn(
        emailEdt: EditText,
        passwordEdt: EditText,
        signInViewModel: SignInViewModel
    ): Boolean {
        // check for user valid email value
        val userEmailValue = emailEdt.text.toString().trim()
        if (!checkForUserValidEmailInput(
                userEmailValue,
                emailEdt
            )) {
            return true
        } else {
            // store user email in view model
            signInViewModel.userEmail = userEmailValue
        }

        // check for user valid password value
        val userPasswordValue = passwordEdt.text.toString()
        if (!checkForUserPasswordInput(
                userPasswordValue,
                passwordEdt
            )) {
            return true
        } else {
            // store user password in view model
            signInViewModel.userPassword = userPasswordValue
        }
        return false
    }
}