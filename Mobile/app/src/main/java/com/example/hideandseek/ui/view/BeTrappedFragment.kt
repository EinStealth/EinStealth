package com.example.hideandseek.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hideandseek.R
import com.example.hideandseek.databinding.FragmentBeTrappedBinding
import com.example.hideandseek.ui.viewmodel.BeTrappedFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BeTrappedFragment : Fragment() {
    private var _binding: FragmentBeTrappedBinding? = null
    private val viewModel: BeTrappedFragmentViewModel by viewModels()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBeTrappedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Viewの取得
        // 時間表示の場所
        val tvRelativeTime: TextView = binding.tvRelativeTime
        val tvLimitTime: TextView = binding.tvLimitTime

        // 捕まったボタン
        val btCaptureOn: ImageView = binding.btCaptureOn

        // スキルボタン
        val btSkillOn: ImageView = binding.btSkillOn
        val btSkillOff: ImageView = binding.btSkillOff
        val progressSkill: ProgressBar = binding.progressSkill

        fun changeBtSkillVisible(isOn: Boolean) {
            if (isOn) {
                btSkillOn.visibility = View.VISIBLE
                btSkillOff.visibility = View.INVISIBLE
                progressSkill.visibility = View.INVISIBLE
            } else {
                btSkillOn.visibility = View.INVISIBLE
                btSkillOff.visibility = View.VISIBLE
                progressSkill.visibility = View.VISIBLE

                progressSkill.max = 60
            }
        }

        // 2重LiveData解消のために変数定義
        var limitTime = ""
        var skillTime = ""
        var trapTime = ""

        // Trapが解除されるまでのプログレスバー
        val progressTrap: ProgressBar = binding.progressTrap
        progressTrap.max = 60

        setFragmentResultListener("MainFragmentLimitTime") { _, bundle ->
            val result = bundle.getString("limitTime")
            Log.d("limitTimeResultListener", result.toString())
            tvLimitTime.text = result
            if (result != null) {
                limitTime = result
            }
        }

        setFragmentResultListener("MainFragmentTrapTime") { _, bundle ->
            val result = bundle.getString("trapTime")
            Log.d("trapTimeResultListener", result.toString())
            if (result != null) {
                trapTime = result
            }
        }

        setFragmentResultListener("MainFragmentSkillTime") { _, bundle ->
            val result = bundle.getString("skillTime")
            Log.d("skillTimeResultListener", result.toString())
            if (result != null) {
                viewModel.setSkillTimeInit(result)
            }
        }

        setFragmentResultListener("MainFragmentIsOverSkillTime") { _, bundle ->
            val result = bundle.getBoolean("isOverSkillTime")
            Log.d("isOverSKillTimeResultListener", result.toString())
            viewModel.setIsOverSkillTime(result)
        }

        viewModel.skillTime.observe(viewLifecycleOwner) {
            skillTime = it
        }

        // 自分の情報の表示
        viewModel.userLive.observe(viewLifecycleOwner) { userLive ->
            Log.d("UserLive", userLive.toString())
            if (userLive.isNotEmpty()) {
                tvRelativeTime.text = userLive[userLive.size - 1].relativeTime
                // 制限時間になったかどうかの判定
                viewModel.compareTime(userLive[userLive.size - 1].relativeTime, limitTime)

                // trapにかかっている時間を計測
                viewModel.compareTrapTime(userLive[userLive.size - 1].relativeTime, trapTime)
                val howProgressTrap = viewModel.howProgressTrapTime(userLive[userLive.size - 1].relativeTime, trapTime)
                progressTrap.progress = howProgressTrap

                // Skill Buttonの Progress Bar
                // スキルボタンを複数回押したとき、relativeが一旦最初の(skillTime+1)秒になって、本来のrelativeまで1秒ずつ足される
                // observeを二重にしてるせいで変な挙動していると思われる（放置するとメモリやばそう）
                // この辺ちゃんと仕様わかってないので、リファクタリング時に修正する
                if (skillTime != "") {
                    viewModel.compareSkillTime(
                        userLive[userLive.size - 1].relativeTime,
                        skillTime,
                    )
                    progressSkill.progress = viewModel.howProgressSkillTime(
                        userLive[userLive.size - 1].relativeTime,
                        skillTime,
                    )
                    setFragmentResult("BeTrappedFragmentSkillTime", bundleOf("skillTime" to skillTime))
                }
            }
        }

        // 捕まったボタンが押された時の処理
        btCaptureOn.setOnClickListener {
            val captureDialogFragment = CaptureDialogFragment()
            val supportFragmentManager = childFragmentManager
            captureDialogFragment.show(supportFragmentManager, "capture")
        }

        // skillボタンが押された時の処理
        btSkillOn.setOnClickListener {
            // Userの最新情報から位置をとってきて、それを罠の位置とする
            coroutineScope.launch {
                setFragmentResult("BeTrappedFragmentSkillTime", bundleOf("skillTime" to viewModel.getNowUser().relativeTime))
            }
            viewModel.postTrapRoom(0)
            viewModel.postTrapSpacetime()
            viewModel.setSkillTime()
            viewModel.setIsOverSkillTime(false)
        }

        viewModel.isOverSkillTime.observe(viewLifecycleOwner) {
            setFragmentResult("BeTrappedFragmentIsOverSkillTime", bundleOf("isOverSkillTime" to it))
            changeBtSkillVisible(it)
        }

        viewModel.isOverTrapTime.observe(viewLifecycleOwner) {
            // TrapTimeが終われば、もとのFragmentに戻る
            if (it) {
                findNavController().navigate(R.id.navigation_main)
            }
        }

        viewModel.isOverLimitTime.observe(viewLifecycleOwner) {
            if (it) {
                // クリアダイアログを表示
                val successEscapeDialogFragment = SuccessEscapeDialogFragment()
                val supportFragmentManager = childFragmentManager
                successEscapeDialogFragment.show(supportFragmentManager, "clear")
            }
        }

        return root
    }
}
