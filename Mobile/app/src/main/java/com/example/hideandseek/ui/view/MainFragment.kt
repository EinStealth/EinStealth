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
import com.example.hideandseek.data.repository.UserRepository
import com.example.hideandseek.databinding.FragmentMainBinding
import com.example.hideandseek.ui.viewmodel.MainFragmentViewModel
import kotlinx.coroutines.*

class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val viewModel: MainFragmentViewModel by viewModels()

    private var trapNumber = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        // Viewの取得
        // 時間表示の場所
        val ivTime:           ImageView = binding.ivTime
        val tvNow:            TextView  = binding.tvNow
        val tvLimit:          TextView  = binding.tvLimit
        val tvRelativeTime:   TextView  = binding.tvRelativeTime
        val tvLimitTime:      TextView  = binding.tvLimitTime
        // Map
        val ivMap:            ImageView = binding.ivMap
        // 捕まったボタン
        val btCaptureOn:      ImageView = binding.btCaptureOn
        val btCaptureOff:     ImageView = binding.btCaptureOff

        fun changeBtCaptureVisible(isOn: Boolean) {
            if (isOn) {
                btCaptureOn.visibility  = View.VISIBLE
                btCaptureOff.visibility = View.INVISIBLE
            } else {
                btCaptureOn.visibility  = View.INVISIBLE
                btCaptureOff.visibility = View.VISIBLE
            }
        }

        // スキルボタン
        val btSkillOn:        ImageView   = binding.btSkillOn
        val btSkillOff:       ImageView   = binding.btSkillOff
        val progressSkill:    ProgressBar = binding.progressSkill

        fun changeBtSkillVisible(isOn: Boolean) {
            if (isOn) {
                btSkillOn.visibility     = View.VISIBLE
                btSkillOff.visibility    = View.INVISIBLE
                progressSkill.visibility = View.INVISIBLE
            } else {
                btSkillOn.visibility     = View.INVISIBLE
                btSkillOff.visibility    = View.VISIBLE
                progressSkill.visibility = View.VISIBLE

                progressSkill.max = 60
            }
        }

        // 観戦中
        val ivWatching:       ImageView = binding.ivWatching

        // 捕まってステータスが捕まったになったら観戦モードになるkiwoj
        fun changeStatusCaptured() {
            ivWatching.visibility   = View.VISIBLE
            changeBtCaptureVisible(false)
            btSkillOff.visibility   = View.VISIBLE
            btSkillOn.visibility    = View.INVISIBLE
        }

        // User normal
        val user1Normal:      ImageView = binding.user1Normal
        val user2Normal:      ImageView = binding.user2Normal
        val user3Normal:      ImageView = binding.user3Normal
        // User demon
        val user4Demon:       ImageView = binding.user4Demon
        // User captured
        val user1Captured:    ImageView = binding.user1Captured

        // Result画面
        val resultBack:       ImageView = binding.resultBack
        val tvResult:         TextView  = binding.tvResult
        val tvWinner:         TextView  = binding.tvWinner
        val tvLoser:          TextView  = binding.tvLoser
        val winnerUser1:      ImageView = binding.winnerUser1
        val winnerUser2:      ImageView = binding.winnerUser2
        val loserUser3:       ImageView = binding.loserUser3
        val loserUser4:       ImageView = binding.loserUser4
        val btResultClose:    ImageView = binding.btResultClose

        fun changeResultDialogVisible(visibility: Int) {
            resultBack.visibility    = visibility
            tvResult.visibility      = visibility
            tvWinner.visibility      = visibility
            tvLoser.visibility       = visibility
            winnerUser1.visibility   = visibility
            winnerUser2.visibility   = visibility
            loserUser3.visibility    = visibility
            loserUser4.visibility    = visibility
            btResultClose.visibility = visibility
        }

        fun changeOtherResultDialog(visibility: Int) {
            ivTime.visibility         = visibility
            tvNow.visibility          = visibility
            tvLimit.visibility        = visibility
            tvRelativeTime.visibility = visibility
            tvLimitTime.visibility    = visibility
            user1Normal.visibility    = visibility
            user2Normal.visibility    = visibility
            user3Normal.visibility    = visibility
            user4Demon.visibility     = visibility
            user1Captured.visibility  = visibility
            btSkillOn.visibility      = visibility
            btSkillOff.visibility     = visibility
            progressSkill.visibility  = visibility

            btCaptureOn.visibility      = visibility
            btCaptureOff.visibility     = visibility
        }


        // データベースからデータを持ってくる
        context?.let {
            viewModel.setAllLocationsLive(it)
            viewModel.setUserLive(it)
            viewModel.setAllTrapsLive(it)
        }

        // BeTrappedFragmentから戻ってきた時
        setFragmentResultListener("BeTrappedFragmentSkillTime") {key, bundle ->
            val result = bundle.getString("skillTime")
            Log.d("skillTimeResultFragment", result.toString())
            if (result != null) {
                viewModel.setSkillTImeString(result)
            }
        }

        setFragmentResultListener("BeTrappedFragmentIsOverSkillTime") {key, bundle ->
            val result = bundle.getBoolean("isOverSkillTime")
            Log.d("isOverSkillTimeResultFragment", result.toString())
            viewModel.setIsOverSkillTime(result)
        }

        setFragmentResultListener("BeTrappedFragmentTrapNumber") {key, bundle ->
            val result = bundle.getInt("trapNumber")
            Log.d("trapNumber", result.toString())
            trapNumber = result
        }

        // 自分の情報の表示
        viewModel.userLive.observe(viewLifecycleOwner) { userLive ->
            Log.d("UserLive", userLive.toString())
            if (userLive.isNotEmpty()) {
                viewModel.setLimitTime(userLive[0].relativeTime)
                tvRelativeTime.text = userLive[userLive.size-1].relativeTime
                // 制限時間になったかどうかの判定
                viewModel.limitTime.observe(viewLifecycleOwner) { limitTime ->
                    viewModel.compareTime(userLive[userLive.size-1].relativeTime, limitTime)
                    setFragmentResult("MainFragmentLimitTime", bundleOf("limitTime" to limitTime))
                }

                // 自分の位置情報のurl
                val iconUrlHide = "https://onl.bz/dcMZVEa"
                var url = "https://maps.googleapis.com/maps/api/staticmap" +
                        "?center=${userLive[userLive.size-1].latitude},${userLive[userLive.size-1].longitude}" +
                        "&size=310x640&scale=1" +
                        "&zoom=18" +
                        "&key=AIzaSyA-cfLegBoleKaT2TbU5R4K1uRkzBR6vUQ" +
                        "&markers=icon:" + iconUrlHide + "|${userLive[userLive.size-1].latitude},${userLive[userLive.size-1].longitude}"

                // 他人の位置を追加
                viewModel.allLocationsLive.observe(viewLifecycleOwner) { allLocationLive ->
                    Log.d("ALL_Location", allLocationLive.toString())
                    if (allLocationLive.isNotEmpty()) {
                        // ユーザーの位置情報
                        for (i in allLocationLive.indices) {
                            if (allLocationLive[i].objId == 1) {
                                context?.let { context -> viewModel.postTrapRoom(context, 1) }
                            } else {
                                url += "&markers=icon:" + iconUrlHide + "|${allLocationLive[i].latitude},${allLocationLive[i].longitude}"
                            }
                        }
                    }
                }

                // trapの位置情報
                viewModel.allTrapsLive.observe(viewLifecycleOwner) { allTrap ->
                    if (allTrap.isNotEmpty()) {
                        for (i in allTrap.indices) {
                            if (allTrap[i].objId == 0) {
                                url += "&markers=icon:https://onl.bz/FetpS7Y|${allTrap[i].latitude},${allTrap[i].longitude}"
                            }
                            if (viewModel.checkCaughtTrap(userLive[userLive.size-1], allTrap[i])) {
                                // TrapにかかったらFragmentを移動
                                setFragmentResult("MainFragmentTrapNumber", bundleOf("trapNumber" to trapNumber))
                                setFragmentResult("MainFragmentTrapTime", bundleOf("trapTime" to userLive[userLive.size-1].relativeTime))

                                findNavController().navigate(R.id.navigation_be_trapped)
                            }
                        }
                    }
                }

                // Skill Buttonの Progress Bar
                // スキルボタンを複数回押したとき、relativeが一旦最初の(skillTime+1)秒になって、本来のrelativeまで1秒ずつ足される
                // observeを二重にしてるせいで変な挙動していると思われる（放置するとメモリやばそう）
                // この辺ちゃんと仕様わかってないので、リファクタリング時に修正する
                if (trapNumber > 0) {
                    viewModel.skillTime.observe(viewLifecycleOwner) { skillTime ->
                        viewModel.compareSkillTime(userLive[userLive.size-1].relativeTime,
                            skillTime
                        )
                        progressSkill.progress = viewModel.howProgressSkillTime(userLive[userLive.size-1].relativeTime,
                            skillTime
                        )
                        setFragmentResult("MainFragmentSkillTime", bundleOf("skillTime" to skillTime))
                    }
                }

                // URLから画像を取得
                // 相対時間10秒おきに行う
                if (userLive[userLive.size-1].relativeTime.substring(7, 8) == "0") {
                    Log.d("fetchMAP", "Mapが更新されました")
                    coroutineScope.launch {
                        val originalBitmap = viewModel.fetchMap(url)
                        viewModel.setMap(originalBitmap)
                    }
                }
            }
        }

        // Trap情報の監視
        viewModel.allTrapsLive.observe(viewLifecycleOwner) {
            Log.d("TRAP_LIVE", it.toString())
        }

        viewModel.limitTime.observe(viewLifecycleOwner) {
            tvLimitTime.text = it
        }

        viewModel.isOverLimitTime.observe(viewLifecycleOwner) {
            if (it) {
                // クリアダイアログを表示
                val successEscapeDialogFragment = SuccessEscapeDialogFragment()
                val supportFragmentManager = childFragmentManager
                successEscapeDialogFragment.show(supportFragmentManager, "clear")
            }
        }

        // Resultダイアログの閉じるを押した時の処理
        btResultClose.setOnClickListener {
            // Resultダイアログの非表示
            changeResultDialogVisible(View.INVISIBLE)

            // Result以外のものを表示
            changeOtherResultDialog(View.VISIBLE)
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
            context?.let {
                setFragmentResult("MainFragmentSkillTime", bundleOf("skillTime" to UserRepository(it).nowUser.relativeTime))
                viewModel.postTrapRoom(it, 0)
                viewModel.postTrapSpacetime(it)
                viewModel.setSkillTime(it)
            }
            viewModel.setIsOverSkillTime(false)
            trapNumber += 1
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
