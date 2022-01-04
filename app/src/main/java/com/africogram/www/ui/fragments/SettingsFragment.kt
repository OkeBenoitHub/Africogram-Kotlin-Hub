package com.africogram.www.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.africogram.www.databinding.SettingsFragmentBinding
import com.africogram.www.viewmodels.SettingsViewModel

class SettingsFragment : Fragment() {
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsFragmentBinding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsFragmentBinding = SettingsFragmentBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        settingsFragmentBinding.settingsViewModel = settingsViewModel
        // bind lifecycle
        settingsFragmentBinding.lifecycleOwner = this
        return settingsFragmentBinding.root
    }
}