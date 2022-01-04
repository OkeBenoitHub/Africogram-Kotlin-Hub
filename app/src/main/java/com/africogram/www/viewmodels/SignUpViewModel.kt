package com.africogram.www.viewmodels

import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    var verificationCode: Int? = null

    // user first name
    var userFirstName: String? = null

    // user last name
    var userLastName: String? = null

    // user email
    var userEmail: String? = null

    // user password
    var userPassword: String? = null

    // user birth month
    var userBirthMonth: Int? = null

    // user birth day
    var userBirthDay: Int? = null

    // user password
    var userBirthYear: Int? = null

    // user gender
    var userGender: String?  = null

    fun onGenderOptionSelected(userGenderChar: Char) {
        userGender = userGenderChar.toString()
    }

    // terms and privacy
    var termsPrivacyChecked: Boolean  = false
    fun onTermsPrivacyChecked() {
        termsPrivacyChecked = !termsPrivacyChecked
    }
}