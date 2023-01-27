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
import com.example.hideandseek.MainApplication
import com.example.hideandseek.R
import com.example.hideandseek.data.datasource.local.LocationData
import com.example.hideandseek.data.datasource.local.TrapData
import com.example.hideandseek.databinding.FragmentMainBinding
import com.example.hideandseek.ui.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val viewModel: MainFragmentViewModel by viewModels()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Viewの取得
        // 時間表示の場所
        val tvRelativeTime: TextView = binding.tvRelativeTime
        val tvLimitTime: TextView = binding.tvLimitTime
        // Map
        val ivMap: ImageView = binding.ivMap
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

        // User normal
        // TODO: Statusを受け取って表示が切り替わるようにする
        val user1Normal: ImageView = binding.user1Normal
        val user2Normal: ImageView = binding.user2Normal
        val user3Normal: ImageView = binding.user3Normal
        // User demon
        val user4Demon: ImageView = binding.user4Demon
        // User captured
        val user1Captured: ImageView = binding.user1Captured

        // BeTrappedFragmentから戻ってきた時
        setFragmentResultListener("BeTrappedFragmentSkillTime") { _, bundle ->
            val result = bundle.getString("skillTime")
            Log.d("skillTimeResultFragment", result.toString())
            if (result != null) {
                viewModel.setSkillTImeString(result)
            }
        }

        setFragmentResultListener("BeTrappedFragmentIsOverSkillTime") { _, bundle ->
            val result = bundle.getBoolean("isOverSkillTime")
            Log.d("isOverSkillTimeResultFragment", result.toString())
            viewModel.setIsOverSkillTime(result)
        }

        // 2重LiveData解消のために変数定義
        var allLocation: List<LocationData> = listOf()
        var allTraps: List<TrapData> = listOf()
        var limitTime = ""
        var skillTime = ""

        viewModel.allLocationsLive.observe(viewLifecycleOwner) {
            allLocation = it
        }

        viewModel.allTrapsLive.observe(viewLifecycleOwner) {
            allTraps = it
        }

        viewModel.limitTime.observe(viewLifecycleOwner) {
            tvLimitTime.text = it
            limitTime = it
        }

        viewModel.skillTime.observe(viewLifecycleOwner) {
            skillTime = it
        }

        // 自分の情報の表示
        viewModel.userLive.observe(viewLifecycleOwner) { userLive ->
            Log.d("UserLive", userLive.toString())
            if (userLive.isNotEmpty()) {
                viewModel.setLimitTime(userLive[0].relativeTime)
                tvRelativeTime.text = userLive[userLive.size - 1].relativeTime
                // 制限時間になったかどうかの判定
                viewModel.compareTime(userLive[userLive.size - 1].relativeTime, limitTime)
                setFragmentResult("MainFragmentLimitTime", bundleOf("limitTime" to limitTime))

                // 自分の位置情報のurl
                val iconUrlHide = "https://onl.bz/dcMZVEa"
                var url = "https://maps.googleapis.com/maps/api/staticmap" +
                    "?center=${userLive[userLive.size - 1].latitude},${userLive[userLive.size - 1].longitude}" +
                    "&size=310x640&scale=1" +
                    "&zoom=18" +
                    "&key=AIzaSyA-cfLegBoleKaT2TbU5R4K1uRkzBR6vUQ" +
                    "&markers=icon:" + iconUrlHide + "|${userLive[userLive.size - 1].latitude},${userLive[userLive.size - 1].longitude}"

                // 他人の位置を追加
                Log.d("ALL_Location", allLocation.toString())
                if (allLocation.isNotEmpty()) {
                    // ユーザーの位置情報
                    for (i in allLocation.indices) {
                        if (allLocation[i].objId == 1) {
                            viewModel.postTrapRoom(1)
                        } else {
                            url += "&markers=icon:" + iconUrlHide + "|${allLocation[i].latitude},${allLocation[i].longitude}"
                        }
                    }
                }

                // trapの位置情報
                if (allTraps.isNotEmpty()) {
                    for (i in allTraps.indices) {
                        if (allTraps[i].objId == 0) {
                            url += "&markers=icon:https://onl.bz/FetpS7Y|${allTraps[i].latitude},${allTraps[i].longitude}"
                        }
                        if (viewModel.checkCaughtTrap(userLive[userLive.size - 1], allTraps[i])) {
                            // TrapにかかったらFragmentを移動
                            setFragmentResult("MainFragmentTrapTime", bundleOf("trapTime" to userLive[userLive.size - 1].relativeTime))

                            findNavController().navigate(R.id.navigation_be_trapped)
                        }
                    }
                }

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
                    setFragmentResult("MainFragmentSkillTime", bundleOf("skillTime" to skillTime))
                }

                // URLから画像を取得
                // 相対時間10秒おきに行う
                if (userLive[userLive.size - 1].relativeTime.substring(7, 8) == "0") {
                    Log.d("fetchMAP", "Mapが更新されました")
                    coroutineScope.launch {
                        val originalBitmap = viewModel.fetchMap(url)
                        viewModel.setMap(originalBitmap)
                    }
                }
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
                setFragmentResult("MainFragmentSkillTime", bundleOf("skillTime" to viewModel.getNowUser().relativeTime))
            }
            viewModel.postTrapRoom(0)
            viewModel.postTrapSpacetime()
            viewModel.setSkillTime()
            viewModel.setIsOverSkillTime(false)
        }

        viewModel.isOverSkillTime.observe(viewLifecycleOwner) {
            setFragmentResult("MainFragmentIsOverSkillTime", bundleOf("isOverSkillTime" to it))
            changeBtSkillVisible(it)
        }

        // Mapに画像をセット
        viewModel.map.observe(viewLifecycleOwner) {
            ivMap.setImageBitmap(it)
        }

        return root
    }
}
