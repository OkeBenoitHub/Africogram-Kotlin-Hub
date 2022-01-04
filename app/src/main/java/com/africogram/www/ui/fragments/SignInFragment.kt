package com.africogram.www.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.africogram.www.R
import com.africogram.www.databinding.SignInFragmentBinding
import com.africogram.www.firebase.FireAuthBase
import com.africogram.www.utils.*
import com.africogram.www.viewmodels.SignInViewModel

class SignInFragment : Fragment(), DialogUtil.ShowAlertDialogCallbackListener,
    FireAuthBase.SendUserPasswordResetEmailCallback {
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var signInFragmentBinding: SignInFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInFragmentBinding = SignInFragmentBinding.inflate(layoutInflater, container, false)
        // bind lifecycle owner
        signInFragmentBinding.lifecycleOwner = this
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInFragmentBinding.signInViewModel = signInViewModel

        // Done button pressed to sign in
        signInFragmentBinding.passwordEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signInExistingUserWithEmailAndPassword()
            }
            false
        }

        // sign in button tapped
        signInFragmentBinding.signInButton.setOnClickListener {
            signInExistingUserWithEmailAndPassword()
        }

        // forgot password link tapped
        signInFragmentBinding.forgotPasswordTv.setOnClickListener {
            // Create an instance of the dialog fragment and show it
            val dialogUtil =  DialogUtil()
            dialogUtil.setDialogProperties(
                getString(R.string.reset_password_title_text),
                null,
                R.layout.reset_password_dialog,
                true,
                getString(R.string.reset_button_text),
                getString(R.string.cancel_button_text),
                this
            )
            dialogUtil.show(requireActivity().supportFragmentManager, "")
        }
        return signInFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        // check for user authentication
        if (FireAuthBase(requireContext()).checkIfCurrentUserIsSignedIn()) {
            // user is logged in
            // navigate back to Main fragment screen
            Navigation.findNavController(requireView()).navigateUp()
        }
    }

    /**
     * Sign in existing user with email and password
     */
    private fun signInExistingUserWithEmailAndPassword() {
        // check for internet connection
        val networkStatusPrefValue = SharedPrefUtil().getDataStringFromSharedPreferences(
            requireContext(),
            NETWORK_STATUS_PREF_KEY
        )
        if (networkStatusPrefValue == NO_NETWORK) {
            MainUtil().showToastMessage(
                requireContext(),
                getString(R.string.no_internet_connection_text)
            )
            return
        }
        // if there is no errors :: trying to sign in existing user
        if (!UserDataUtil(requireContext()).checkForUserInputDataErrorsBeforeSigningIn(
                signInFragmentBinding.emailEdt,
                signInFragmentBinding.passwordEdt,
                signInViewModel
            )) {
            // show loader bar
            signInFragmentBinding.loaderBar.visibility = View.VISIBLE
            // try to sign existing user to his or her account
            FireAuthBase(requireContext()).signInUserWithEmailAndPassword(
                signInViewModel.userEmail.toString(),
                signInViewModel.userPassword.toString(),
                onSignUserInProcessListener
            )
        }
    }

    /**
     * Listener for when user try to sign in
     * to his or her account
     */
    private val onSignUserInProcessListener: FireAuthBase.SignUserInProcessCallback = object : FireAuthBase.SignUserInProcessCallback {
        override fun onSignUserInProcessStatus(isSuccessful: Boolean, errorMessage: String?) {
            // hide loader bar
            signInFragmentBinding.loaderBar.visibility = View.GONE
            if (isSuccessful) {
                // user is logged in
                // navigate back to Main fragment screen
                Navigation.findNavController(requireView()).navigateUp()
            } else {
                MainUtil().showToastMessage(requireContext(), errorMessage.toString())
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val dialogView = dialog.dialog
        val resetEmailEdt = dialogView?.findViewById(R.id.reset_email_edt) as EditText
        val resetEmailInputValue = resetEmailEdt.text.toString().trim()
        // check for reset user email input
        if (!UserDataUtil(requireContext()).checkForUserValidEmailInput(resetEmailInputValue,resetEmailEdt)) {
            if (resetEmailInputValue.isEmpty()) {
                MainUtil().showToastMessage(requireContext(),getString(R.string.empty_email_error))
            } else {
                MainUtil().showToastMessage(requireContext(),getString(R.string.invalid_email_error))
            }
            return
        }
        // check for internet connection
        val networkStatusPrefValue = SharedPrefUtil().getDataStringFromSharedPreferences(requireContext(), NETWORK_STATUS_PREF_KEY)
        if (networkStatusPrefValue == NO_NETWORK) {
            MainUtil().showToastMessage(requireContext(),getString(R.string.no_internet_connection_text))
            return
        }
        // try to send email reset password
        FireAuthBase(requireContext()).sendUserPasswordResetEmail(resetEmailInputValue,this)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}

    override fun onUserPasswordResetEmailSent(isSuccessful: Boolean, errorMessage: String?) {
        if (isSuccessful) {
            signInFragmentBinding.forgotPasswordMessageTv.visibility = View.VISIBLE
        } else {
            MainUtil().showToastMessage(requireContext(),errorMessage.toString())
        }
    }
}