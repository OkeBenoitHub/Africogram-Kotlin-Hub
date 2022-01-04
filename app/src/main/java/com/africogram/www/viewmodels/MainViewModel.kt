package com.africogram.www.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    /**
     * Variable that tells the Fragment to navigate to sign in fragment
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _navigateToSignInFragmentEvt = MutableLiveData<Boolean?>()
    val navigateToSignInFragmentEvt: LiveData<Boolean?>
        get() = _navigateToSignInFragmentEvt

    fun onNavigateToSignInFragment() {
        _navigateToSignInFragmentEvt.value = true
    }
    fun onNavigateToSignInFragmentDone() {
        _navigateToSignInFragmentEvt.value = false
    }

    /**
     * Variable that tells the Fragment to navigate to sign up fragment
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _navigateToSignUpFragmentEvt = MutableLiveData<Boolean?>()
    val navigateToSignUpFragmentEvt: LiveData<Boolean?>
        get() = _navigateToSignUpFragmentEvt

    fun onNavigateToSignUpFragment() {
        _navigateToSignUpFragmentEvt.value = true
    }
    fun onNavigateToSignUpFragmentDone() {
        _navigateToSignUpFragmentEvt.value = false
    }
}