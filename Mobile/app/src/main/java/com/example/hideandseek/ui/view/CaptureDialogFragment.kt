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
import com.example.hideandseek.R
import com.example.hideandseek.databinding.FragmentCaptureDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaptureDialogFragment : DialogFragment() {
    private var _binding: FragmentCaptureDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCaptureDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 捕まったか確認するダイアログ
        val btCaptureYes: ImageView = binding.btCaptureYes
        val btCaptureNo: ImageView = binding.btCaptureNo

        var flag = false

        btCaptureYes.setOnClickListener {
            flag = true
            dialog?.dismiss()
        }

        btCaptureNo.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.setOnDismissListener {
            if (flag) {
                val notifyCaptureDialogFragment = NotifyCaptureDialogFragment()
                val supportFragmentManager = childFragmentManager
                notifyCaptureDialogFragment.show(supportFragmentManager, "notifyCapture")
            }
        }

        return root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.fragment_capture_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setDimAmount(0f)
        return dialog
    }
}
