package com.example.hideandseek.ui.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.hideandseek.R
import com.example.hideandseek.databinding.FragmentSuccessEscapeDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessEscapeDialogFragment : DialogFragment() {
    private var _binding: FragmentSuccessEscapeDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSuccessEscapeDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 捕まったか確認するダイアログ
        val btClose: ImageView = binding.btClose

        btClose.setOnClickListener {
            findNavController().navigate(R.id.navigation_result)
            dialog?.dismiss()
        }

        return root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.fragment_success_escape_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setDimAmount(0f)
        return dialog
    }
}
