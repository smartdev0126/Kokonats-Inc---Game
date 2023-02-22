package com.biiiiit.kokonats.ui.game.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.biiiiit.kokonats.R
import com.biiiiit.kokonats.data.bean.Game
import com.biiiiit.kokonats.data.bean.Tournament
import com.biiiiit.kokonats.data.bean.TournamentPlay
import com.biiiiit.kokonats.databinding.FragmentTnmtResultBinding
import com.biiiiit.kokonats.ui.game.ui.adapter.TnmtResultAdapter
import com.biiiiit.kokonats.ui.game.viewmodel.GameTnmtDetailViewModel
import com.biiiiit.kokonats.ui.game.viewmodel.TnmtResultViewModel
import com.biiiiit.kokonats.ui.user.vm.UserEnergyViewModel
import com.biiiiit.kokonats.utils.isEnglishLocale
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
 * @Description tnmt result
 **/
class TnmtResultFragment : BaseFragment<FragmentTnmtResultBinding, TnmtResultViewModel>() {


    private var score: String = ""

    /**
     * tnmt --> tnmtId
     * pvp --> matchPlayId
     */
    private var id = 0L

    private val adapter = TnmtResultAdapter()

    private var user = getAny<LoginUser>(SP_LOGIN_USER)

    private var tournament: Tournament? = null
    private var game: Game? = null

    private val gameTnmtVM: GameTnmtDetailViewModel by lazy {
        getAppViewModel(GameTnmtDetailViewModel::class.java)
    }


    companion object {
        fun newInstance(id: Long, score: String) = TnmtResultFragment().apply {
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
        binding.tvScore.text = "+${score}"
        binding.rcvScore.layoutManager = LinearLayoutManager(context)
        binding.rcvScore.adapter = adapter

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun initViewModel2() {
        vm.tnmtPlayList.observe(this) {
            adapter.setNewInstance(it)
            binding.ivShare.setOnClickListener(View.OnClickListener {_ -> shareResult(it) })
        }

        gameTnmtVM.tournament.observe(this) {
            tournament = it
            gameTnmtVM.queryGameDetail(it.gameId)
        }

        gameTnmtVM.gameDetail.observe(this) {
            game = it
        }
    }

    private fun shareResult(players: MutableList<TournamentPlay>) {
        var ranking = -1
        for (player in players) {
            if (user?.userName == player.userName) {
                ranking = players.indexOf(player)
                break
            }
        }
        if (ranking > -1) {
            if (game != null && tournament != null) {
                context?.let {
                    ranking++
                    var rankingStr = ranking.toString()
                    if (isEnglishLocale(requireContext())) {
                        rankingStr = when (ranking) {
                            1 -> "1st"
                            2 -> "2nd"
                            3 -> "3rd"
                            else -> ranking.toString() + "th"
                        }
                    }
                    shareOnTwitter(
                        it,
                        it.getString(R.string.share_result_tournament, game!!.name, tournament!!.tournamentName, rankingStr),
                        "https://game.kokonats.club",
                        "kokonats_jp",
                        ""
                    )
                }
            }
        } else {
            showToast("Cannot find your ranking. username: " + user?.userName)
        }
    }

    override fun actionAlways() {
        vm.queryTnmtPlayHistory(id)
        gameTnmtVM.queryTnmtDetail(id)
    }

    override fun getVMClazz(): Class<TnmtResultViewModel> = TnmtResultViewModel::class.java

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTnmtResultBinding.inflate(inflater, container, false)
}