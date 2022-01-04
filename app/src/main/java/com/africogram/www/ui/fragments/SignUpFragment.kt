package com.africogram.www.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.africogram.www.R
import com.africogram.www.databinding.SignUpFragmentBinding
import com.africogram.www.firebase.FireAuthBase
import com.africogram.www.firebase.FireStoreBase
import com.africogram.www.models.User
import com.africogram.www.utils.*
import com.africogram.www.viewmodels.SignUpViewModel

class SignUpFragment : Fragment(), DialogUtil.ShowAlertDialogCallbackListener,
    FireAuthBase.SignUserUpProcessCallback {

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var signUpFragmentBinding: SignUpFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signUpFragmentBinding = SignUpFragmentBinding.inflate(layoutInflater, container, false)
        // bind lifecycle owner
        signUpFragmentBinding.lifecycleOwner = this
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        signUpFragmentBinding.signUpViewModel = signUpViewModel

        /**
         * If verification code has already been sent and user rotate device
         * somehow :: we show
         */
        if (signUpViewModel.verificationCode != null) {
            signUpFragmentBinding.verifyAccountButton.visibility = View.GONE
            signUpFragmentBinding.verifyAccountMessageTv.visibility = View.GONE
        }

        // sign up button tapped
        signUpFragmentBinding.signUpButton.setOnClickListener {
            // check for internet connection
            val networkStatusPrefValue = SharedPrefUtil().getDataStringFromSharedPreferences(requireContext(), NETWORK_STATUS_PREF_KEY)
            if (networkStatusPrefValue == NO_NETWORK) {
                MainUtil().showToastMessage(requireContext(),getString(R.string.no_internet_connection_text))
                return@setOnClickListener
            }
            // if there is no errors :: send verification account email
            if(!UserDataUtil(requireContext()).checkForUserInputDataErrorsBeforeSigningUp(
                    signUpFragmentBinding.firstNameEdt,
                    signUpFragmentBinding.lastNameEdt,
                    signUpFragmentBinding.emailEdt,
                    signUpFragmentBinding.passwordEdt,
                    signUpFragmentBinding.birthMonthEdt,
                    signUpFragmentBinding.birthDayEdt,
                    signUpFragmentBinding.birthYearEdt,
                    signUpViewModel
                )) {
                sendUserVerificationCodeByEmail()
            }
        }
        return signUpFragmentBinding.root
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

    // send new user verification code by email
    private fun sendUserVerificationCodeByEmail() {
        var verificationCode = signUpViewModel.verificationCode
        // check if code has already been sent
        if (verificationCode != null) {
            // hide loader bar
            signUpFragmentBinding.loaderBar.visibility = View.GONE
            MainUtil().showToastMessage(requireContext(),getString(R.string.verify_account_text))
            // sent verification account email is successful
            onSentEmailVerificationCodeSuccessful()
        } else {
            // generate a verification code
            verificationCode = MainUtil().getRandomNumber(10000, 30000)
            // save it to view model
            signUpViewModel.verificationCode = verificationCode
            val htmlTemp = getString(R.string.verification_code_is_text) + " " + signUpViewModel.verificationCode  + ".<br/>" + getString(
                R.string.verification_account_extra_text
            )
            // generate email template
            val emailHtmlTplData = getEmailHtmlTplWithData(
                arrayListOf(signUpViewModel.userEmail.toString()),
                getString(R.string.verify_account_text),
                htmlTemp,
                getString(R.string.verify_account_text)
            )
            // show loader bar
            signUpFragmentBinding.loaderBar.visibility = View.VISIBLE
            FireStoreBase(requireContext()).addDocDataToCollectionWithGeneratedId(
                MAIL_COLLECTION_NAME, emailHtmlTplData, onSentEmailVerificationCodeProcessListener
            )
        }
    }

    /**
     * Interface listener for when email verification code is sent
     * to new user after signing up
     */
    private val onSentEmailVerificationCodeProcessListener: FireStoreBase.AddDocDataWithGeneratedIdProcessCallback = object : FireStoreBase.AddDocDataWithGeneratedIdProcessCallback {
        override fun onAddedDocDataProcessStatus(
            isSuccessful: Boolean,
            errorMessage: String?,
            docRefId: String?
        ) {
            // hide loader bar
            signUpFragmentBinding.loaderBar.visibility = View.GONE
            if (isSuccessful) {
                // sent verification account email is successful
                onSentEmailVerificationCodeSuccessful()
            } else {
                // unsuccessful
                MainUtil().showToastMessage(requireContext(), errorMessage.toString())
            }
        }
    }

    /**
     * Email verification code sent is successful
     * Show verify account button and message
     */
    private fun onSentEmailVerificationCodeSuccessful() {
        signUpFragmentBinding.verifyAccountMessageTv.visibility = View.VISIBLE
        signUpFragmentBinding.verifyAccountButtonLayout.visibility = View.VISIBLE
        // verify account button tapped
        signUpFragmentBinding.verifyAccountButton.setOnClickListener {
            // Create an instance of the dialog fragment and show it
            val dialogUtil =  DialogUtil()
            dialogUtil.setDialogProperties(
                getString(R.string.verification_code_dialog_title),
                null,
                R.layout.verification_code_dialog,
                true,
                getString(R.string.verify_button_text),
                getString(R.string.cancel_button_text),
                this@SignUpFragment
            )
            dialogUtil.show(requireActivity().supportFragmentManager, "")
        }
    }

    /**
     * Listen when alert dialog positive button get tapped
     */
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val dialogView = dialog.dialog
        val verificationCodeEdt = dialogView?.findViewById(R.id.verification_code_edt) as EditText
        val verificationCodeInputValue = verificationCodeEdt.text.toString().trim().toIntOrNull()
        if (verificationCodeInputValue == signUpViewModel.verificationCode) {
            // verification code correct :: continue with registration
            signUpFragmentBinding.loaderBar.visibility = View.VISIBLE
            FireAuthBase(requireActivity()).signUpNewUserWithEmailAndPassword(
                signUpViewModel.userEmail.toString(),
                signUpViewModel.userPassword.toString(),
                this
            )
        } else {
            // code incorrect
            MainUtil().showToastMessage(
                requireContext(),
                getString(R.string.incorrect_verification_code)
            )
        }
    }

    // when alert negative button (cancel) gets tapped
    override fun onDialogNegativeClick(dialog: DialogFragment) {}

    /**
     * Interface listener for when new user data is added
     * to Users Firestore collection after signing up
     */
    private val onAddedNewUserDataProcessListener : FireStoreBase.AddDocDataWithSpecificIdProcessCallback =
        object : FireStoreBase.AddDocDataWithSpecificIdProcessCallback {
            override fun onAddedDocDataProcessStatus(isSuccessful: Boolean, errorMessage: String?) {
                // hide loader bar
                signUpFragmentBinding.loaderBar.visibility = View.GONE
                if (isSuccessful) {
                    // if new user data is added successfully to Users collection :: update userId to document id
                    signUpFragmentBinding.loaderBar.visibility = View.VISIBLE
                    FireStoreBase(requireContext()).updateDocDataField(USERS_COLLECTION_NAME,UserDataUtil(requireContext()).userId,"userId",UserDataUtil(requireContext()).userId,object: FireStoreBase.UpdateDocFieldDataProcessCallback {
                        override fun onUpdatedDocFieldDataProcessStatus(
                            isSuccessful: Boolean,
                            errorMessage: String?
                        ) {
                            // hide loader bar
                            signUpFragmentBinding.loaderBar.visibility = View.GONE
                            if(isSuccessful) {
                                // on updated userId to document id :: successful
                                // user successfully signed up
                                // navigate back to Main fragment screen
                                Navigation.findNavController(requireView()).navigateUp()
                            } else {
                                MainUtil().showToastMessage(requireContext(),errorMessage.toString())
                            }
                        }
                    })
                } else {
                    MainUtil().showToastMessage(requireContext(),errorMessage.toString())
                }
            }
        }

    /**
     * Listen for when new user create an account with email and password
     * Determines if successful or not
     */
    override fun onSignUserUpProcessStatus(isSuccessful: Boolean, errorMessage: String?) {
        // hide loader bar
        signUpFragmentBinding.loaderBar.visibility = View.GONE
        if (isSuccessful) {
            /**
             * If sign new user up is successful ::
             * Add new user data to firestore Users collection
             */
            val userDocData = User(
                "",
                signUpViewModel.userFirstName,
                signUpViewModel.userLastName,
                signUpViewModel.userEmail,
                signUpViewModel.userBirthDay,
                signUpViewModel.userBirthMonth,
                signUpViewModel.userBirthYear,
                signUpViewModel.userGender,
                "",
                lastOnlineDate = UserDataUtil(requireContext()).lastOnlineDate,
                lastOnlineTime = UserDataUtil(requireContext()).lastOnlineTime
            )
            signUpFragmentBinding.loaderBar.visibility = View.VISIBLE
            FireStoreBase(requireContext()).addDocDataToCollectionWithSpecificId(
                USERS_COLLECTION_NAME,
                UserDataUtil(requireContext()).userId,
                userDocData,
                onAddedNewUserDataProcessListener
            )
        } else {
            MainUtil().showToastMessage(requireContext(), errorMessage.toString())
            // if failed to create user with email and password :: hide verify account button and message
            signUpFragmentBinding.verifyAccountButtonLayout.visibility = View.GONE
            signUpFragmentBinding.verifyAccountMessageTv.visibility = View.GONE
            // reset verification code
            signUpViewModel.verificationCode = null
        }
    }
}