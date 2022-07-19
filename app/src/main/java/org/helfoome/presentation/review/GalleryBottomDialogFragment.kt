package org.helfoome.presentation.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.DialogGalleryImageSelectionBinding
import org.helfoome.presentation.type.ReviewImageType

@AndroidEntryPoint
class GalleryBottomDialogFragment(private val onDialogClickListener: (ReviewImageType) -> (Unit)) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogGalleryImageSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogGalleryImageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.tvPhotoShoot.setOnClickListener {
            onDialogClickListener(ReviewImageType.PHOTO_SHOOT)
            dismiss()
        }
        binding.tvGallery.setOnClickListener {
            onDialogClickListener(ReviewImageType.GALLERY)
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}
