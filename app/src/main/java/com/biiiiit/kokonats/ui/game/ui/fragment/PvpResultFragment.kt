package com.biiiiit.kokonats.ui.game.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.biiiiit.kokonats.R
import com.biiiiit.kokonats.data.bean.Game
import com.biiiiit.kokonats.data.bean.PvpResultState
import com.biiiiit.kokonats.databinding.FragmentPvpResultBinding
import com.biiiiit.kokonats.ui.game.ui.adapter.PvpResultAdapter
import com.biiiiit.kokonats.ui.game.viewmodel.GameTnmtDetailViewModel
import com.biiiiit.kokonats.ui.game.viewmodel.PvpResultViewModel
import com.biiiiit.kokonats.utils.shareOnTwitter
import com.biiiiit.lib_base.base.BaseFragment
import com.biiiiit.lib_base.data.COMMON_DATA
import com.biiiiit.lib_base.data.COMMON_ID
import com.biiiiit.lib_base.data.LoginUser
import com.biiiiit.lib_base.utils.SP_LOGIN_USER
import com.biiiiit.lib_base.utils.getAny

/**
 * @Author yo_hack
 * @Date 2022.01.13
 * @Description pvp detail activity
 **/
class PvpResultFragment : BaseFragment<FragmentPvpResultBinding, PvpResultViewModel>() {


    private var score: String = ""

    /**
     * tnmt --> tnmtId
     * pvp --> matchPlayId
     */
    private var id = 0L

    private var adapter = PvpResultAdapter()

    private var game: Game? = null
    private var result: PvpResultState? = null
    private var user = getAny<LoginUser>(SP_LOGIN_USER)

    private val gameTnmtVM: GameTnmtDetailViewModel by lazy {
        getAppViewModel(GameTnmtDetailViewModel::class.java)
    }

    companion object {
        fun newInstance(id: Long, score: String) = PvpResultFragment().apply {
            arguments = Bundle().apply {
                putLong(COMMON_ID, id)
                putString(COMMON_DATA, score)
            }
        }
    }

    override fun beforeOnCreate0() {
        arguments?.let {
            score = it.getString(COMMON_DATA, "")
            id = it.getLong(COMMON_ID, 0)
        }
    }


    override fun initView1() {
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.postDelayed({ binding.refreshLayout.finishRefresh() }, 10000)
            actionAlways()
        }
        binding.rcvScore.layoutManager = LinearLayoutManager(context)
        binding.rcvScore.adapter = adapter

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.ivShare.setOnClickListener(View.OnClickListener {
            shareResult()
        })
    }

    override fun actionAlways() {
        vm.queryPvpHistory(id)
        gameTnmtVM.queryGameDetail(id)
    }

    override fun initViewModel2() {
        vm.data.observe(viewLifecycleOwner) {
            binding.refreshLayout.finishRefresh()
            it?.let {
                val gameFinish = it.players?.all { it.state == 1 }
                if (gameFinish == true) { // 结束
                    showRcv(true)
                    adapter.myStatus = it.result ?: ""
                    adapter.setNewInstance(it.players?.toMutableList())
                } else { // 进行中
                    showRcv(false)
                }
                binding.tvScore.setText(
                    when (it.result) {
                        "W" -> R.string.game_win
                        "L" -> R.string.game_lost
                        "D" -> R.string.game_draw
                        else -> if (gameFinish == true) {
                            R.string.waiting
                        } else {
                            R.string.game_playing
                        }
                    }
                )
            }
        }
    }

    private fun showRcv(flag: Boolean) {
        binding.tvNotFinish.isVisible = !flag
        binding.rcvScore.isVisible = flag
    }

    private fun shareResult() {
        if (result != null && game != null) {
            context?.let {
                val gameTitle = game!!.name
                var opponentName = ""
                result!!.players!!.forEach { player ->
                    if (player.username != user!!.userName) opponentName = player.username
                }
                val content = when (result!!.result) {
                    "W" -> it.getString(R.string.share_result_pvp_won, gameTitle, opponentName)
                    "L" -> it.getString(R.string.share_result_pvp_lost, gameTitle, opponentName)
                    "D" -> it.getString(R.string.share_result_pvp_drawn, gameTitle, opponentName)
                    else -> null
                }
                if (content != null) {
                    shareOnTwitter(
                        it,
                        content,
                        "https://game.kokonats.club",
                        "kokonats_jp",
                        ""
                    )
                }
            }
        }
    }

    override fun getVMClazz(): Class<PvpResultViewModel> =
        PvpResultViewModel::class.java

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPvpResultBinding.inflate(layoutInflater, container, false)
}