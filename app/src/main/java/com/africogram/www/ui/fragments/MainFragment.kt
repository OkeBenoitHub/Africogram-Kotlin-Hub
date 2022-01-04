package com.africogram.www.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.africogram.www.R
import com.africogram.www.databinding.MainFragmentBinding
import com.africogram.www.firebase.FireAuthBase
import com.africogram.www.utils.MainUtil
import com.africogram.www.viewmodels.MainViewModel


class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainFragmentBinding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // check for authentication
        if (!FireAuthBase(requireContext()).checkIfCurrentUserIsSignedIn()) {
            // user is not signed in
            mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
            mainFragmentBinding = MainFragmentBinding.inflate(layoutInflater, container, false)
            mainFragmentBinding.mainViewModel = mainViewModel
            // bind lifecycle owner
            mainFragmentBinding.lifecycleOwner = this

            // listen to sign in fragment navigation event
            mainViewModel.navigateToSignInFragmentEvt.observe(viewLifecycleOwner, { isNavigated ->
                isNavigated?.let {
                    if (isNavigated) {
                        // navigate to Sign In fragment
                        this.findNavController().navigate(
                            MainFragmentDirections.actionMainFragmentToSignInFragment()
                        )
                        mainViewModel.onNavigateToSignInFragmentDone()
                    }
                }
            })

            // listen to sign up fragment navigation event
            mainViewModel.navigateToSignUpFragmentEvt.observe(viewLifecycleOwner, { isNavigated ->
                isNavigated?.let {
                    if (isNavigated) {
                        // navigate to Sign Up fragment
                        this.findNavController().navigate(
                            MainFragmentDirections.actionMainFragmentToSignUpFragment()
                        )
                        mainViewModel.onNavigateToSignUpFragmentDone()
                    }
                }
            })
            if (savedInstanceState == null) {
                if (mainFragmentBinding.signInLayout != null) {
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val signInFragment = SignInFragment()
                    // Add the fragment to its container using a transaction
                    fragmentManager.beginTransaction()
                        .add(R.id.sign_in_layout, signInFragment)
                        .commit()
                }
            }
            return mainFragmentBinding.root
        } else {
            // user is signed in
            return inflater.inflate(R.layout.home_fragment, container, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // check for authentication
        if (!FireAuthBase(requireContext()).checkIfCurrentUserIsSignedIn()) {
            // user not logged in
            inflater.inflate(R.menu.main_menu, menu)
        } else {
            // user not logged in
            inflater.inflate(R.menu.home_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // terms of service
            R.id.terms_service -> {
                val termsOfServiceLink = getString(R.string.terms_service_link)
                MainUtil().openWebPage(requireContext(), termsOfServiceLink)
            }
            // privacy policy
            R.id.privacy_policy -> {
                val privacyPolicyLink = getString(R.string.privacy_policy_link)
                MainUtil().openWebPage(requireContext(), privacyPolicyLink)
            }
            // share app
            R.id.share_app -> {
                var aboutAppMessage = getString(R.string.about_app_share_1)
                aboutAppMessage += "\n" + getString(R.string.about_app_share2)
                aboutAppMessage += "\n" + getString(R.string.app_store_link)
                MainUtil().shareTextData(
                    requireContext(),
                    getString(R.string.share_app_via_text),
                    aboutAppMessage
                )
            }
            // settings
            R.id.settings -> {
                // navigate to Settings fragment
                this.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSettingsFragment()
                )
            }
            // sign out
            R.id.signOut -> {
                FireAuthBase(requireContext()).signUserOut()
                requireActivity().finish()
                startActivity(requireActivity().intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}