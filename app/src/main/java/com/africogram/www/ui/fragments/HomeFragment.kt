package com.africogram.www.ui.fragments

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.africogram.www.R
import com.africogram.www.utils.MainUtil
import com.africogram.www.utils.PhotoUtil
import com.africogram.www.viewmodels.HomeViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options

class HomeFragment : Fragment(), PhotoUtil.CropPhotoCallBack {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onCropPhotoFile(isProceed: Boolean, photoFileUri: Uri?) {
        if (isProceed && photoFileUri != null) {
            val cropImage = registerForActivityResult(CropImageContract()) { result ->
                if (result.isSuccessful) {
                    // use the returned uri
                    val uriContent = result.uriContent
                    val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
                } else {
                    // an error occurred
                    val exception = result.error
                }
            }
            cropImage.launch(
                options(uri = photoFileUri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                }
            )
        } else {
            MainUtil().showToastMessage(requireContext(),"Failed to crop photo")
        }
    }

}