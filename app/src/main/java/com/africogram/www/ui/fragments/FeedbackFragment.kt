package com.africogram.www.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.africogram.www.databinding.FeedbackFragmentBinding
import com.africogram.www.viewmodels.FeedbackViewModel

class FeedbackFragment : Fragment() {
    private lateinit var feedbackViewModel: FeedbackViewModel
    private lateinit var feedbackFragmentBinding: FeedbackFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        feedbackFragmentBinding = FeedbackFragmentBinding.inflate(layoutInflater, container, false)
        return feedbackFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedbackViewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]
        feedbackFragmentBinding.viewModel = feedbackViewModel
        // bind lifecycle owner
        feedbackFragmentBinding.lifecycleOwner = this
    }
}