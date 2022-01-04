package com.africogram.www.firebase

import android.content.Context
import com.africogram.www.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Firebase Authentication :: contain every recurring task dealing with authentication
 */
class FireAuthBase(val context: Context) {

    /**
     * Get current user
     *
     * @return void
     */
    fun getCurrentUser(): FirebaseUser? {
        return getAuthInstance().currentUser
    }

    /**
     * Get current auth state
     *
     * @return void
     */
    private fun getAuthInstance(): FirebaseAuth {
        return Firebase.auth
    }

    /**
     * Check if current user is signed in
     *
     * @return true or false
     */
    fun checkIfCurrentUserIsSignedIn(): Boolean {
        return getCurrentUser() != null
    }

    // interface that will check for sign in process status
    interface SignUserInProcessCallback {
        fun onSignUserInProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Sign User into his or her account
     * @param userEmail    :: user email
     * @param userPassword :: user password
     */
    fun signInUserWithEmailAndPassword(
        userEmail: String,
        userPassword: String,
        SignUserInProcessCallback: SignUserInProcessCallback
    ) {
        getAuthInstance().signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    SignUserInProcessCallback.onSignUserInProcessStatus(true, null)
                } else {
                    // If sign in fails, generate the appropriate error message.
                    when (val e = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            when (e.errorCode) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    SignUserInProcessCallback.onSignUserInProcessStatus(
                                        false,
                                        context.getString(R.string.email_not_found_for_user_error)
                                    )
                                }
                                "ERROR_USER_DISABLED" -> {
                                    SignUserInProcessCallback.onSignUserInProcessStatus(
                                        false,
                                        context.getString(R.string.account_disabled_text)
                                    )
                                }
                            }
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            SignUserInProcessCallback.onSignUserInProcessStatus(
                                false,
                                context.getString(R.string.incorrect_password_error)
                            )
                        }
                        else -> {
                            SignUserInProcessCallback.onSignUserInProcessStatus(
                                false,
                                e?.localizedMessage
                            )
                        }
                    }
                }
            }
    }

    // interface that will check for sign up process status
    interface SignUserUpProcessCallback {
        fun onSignUserUpProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }
    /**
     * Sign new user with email and password
     * @param userEmail    :: user email
     * @param userPassword :: user password
     */
    fun signUpNewUserWithEmailAndPassword(userEmail: String, userPassword: String,SignUserUpProcessCallback: SignUserUpProcessCallback) {
        getAuthInstance().createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    SignUserUpProcessCallback.onSignUserUpProcessStatus(true, null)
                } else {
                    val e = task.exception
                    if (e is FirebaseAuthInvalidUserException) {
                        when(e.errorCode) {
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                SignUserUpProcessCallback.onSignUserUpProcessStatus(false,context.getString(R.string.email_not_found_for_user_error))
                            }
                        }
                    } else {
                        SignUserUpProcessCallback.onSignUserUpProcessStatus(false,e?.localizedMessage)
                    }
                }
            }
    }

    // interface that will check for sign in process status
    interface SendUserPasswordResetEmailCallback {
        fun onUserPasswordResetEmailSent(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Send password reset email to user
     * @param userEmail :: user email
     * @param SendUserPasswordResetEmailCallback :: callback
     */
    fun sendUserPasswordResetEmail(
        userEmail: String,
        SendUserPasswordResetEmailCallback: SendUserPasswordResetEmailCallback
    ) {
        getAuthInstance().useAppLanguage()
        getAuthInstance().sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    SendUserPasswordResetEmailCallback.onUserPasswordResetEmailSent(
                        task.isSuccessful,
                        null
                    )
                } else {
                    val e = task.exception
                    if (e is FirebaseAuthInvalidUserException) {
                        when (e.errorCode) {
                            "ERROR_USER_NOT_FOUND" -> {
                                SendUserPasswordResetEmailCallback.onUserPasswordResetEmailSent(
                                    false,
                                    context.getString(R.string.email_not_found_for_user_error)
                                )
                            }
                            "ERROR_USER_DISABLED" -> {
                                SendUserPasswordResetEmailCallback.onUserPasswordResetEmailSent(
                                    false,
                                    context.getString(R.string.account_disabled_text)
                                )
                            }
                        }
                    } else {
                        SendUserPasswordResetEmailCallback.onUserPasswordResetEmailSent(
                            false,
                            e?.localizedMessage
                        )
                    }
                }
            }
    }

    // interface that will check for update user email process status
    interface UpdateUserEmailProcessCallback {
        fun onUpdateEmailProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Update user email address
     * @param newEmail :: new user email
     */
    fun updateUserEmail(newEmail: String, UpdateUserEmailProcessCallback: UpdateUserEmailProcessCallback) {
        getCurrentUser()!!.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UpdateUserEmailProcessCallback.onUpdateEmailProcessStatus(true,null)
                }
            }.addOnFailureListener { e ->
                UpdateUserEmailProcessCallback.onUpdateEmailProcessStatus(false,e.localizedMessage)
            }
    }

    // interface that will check for update user password process status
    interface UpdateUserPasswordProcessCallback {
        fun onUpdatePasswordProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Update user password
     * @param newPassword :: new user password
     */
    fun updateUserPassword(newPassword: String,UpdateUserPasswordProcessCallback: UpdateUserPasswordProcessCallback) {
        getCurrentUser()!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UpdateUserPasswordProcessCallback.onUpdatePasswordProcessStatus(true, null)
                }
            }.addOnFailureListener { e ->
                UpdateUserPasswordProcessCallback.onUpdatePasswordProcessStatus(false, e.localizedMessage)
            }
    }

    // interface that will check for delete current user process status
    interface DeleteUserAccountProcessCallback {
        fun onDeleteUserAccountProcessStatus(isSuccessful: Boolean, errorMessage: String?)
    }

    /**
     * Delete current user from our system
     */
    fun deleteUserAccount(DeleteUserAccountProcessCallback: DeleteUserAccountProcessCallback) {
        getCurrentUser()!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    DeleteUserAccountProcessCallback.onDeleteUserAccountProcessStatus(true,null)
                }
            }.addOnFailureListener { e ->
                DeleteUserAccountProcessCallback.onDeleteUserAccountProcessStatus(false,e.localizedMessage)
            }
    }

    /**
     * Sign user out
     */
    fun signUserOut() {
        getAuthInstance().signOut()
    }
}