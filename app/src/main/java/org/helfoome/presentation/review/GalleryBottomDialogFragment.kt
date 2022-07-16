package org.helfoome.presentation.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.DialogGalleryImageSelectionBinding

@AndroidEntryPoint
class GalleryBottomDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogGalleryImageSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogGalleryImageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.tvKakao.setOnClickListener {
            // TODO
        }
        binding.tvNaver.setOnClickListener {
            // TODO
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}
