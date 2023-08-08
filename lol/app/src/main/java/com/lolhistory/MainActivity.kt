package com.lolhistory

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lolhistory.databinding.ActivityMainBinding
import com.lolhistory.datamodel.SummonerRankInfo
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var puuid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etInputSummoner.setOnKeyListener{view, i , keyEvent ->

            when(i){
                KeyEvent.KEYCODE_ENTER -> binding.btnInputSummoner.callOnClick()
            }
        return@setOnKeyListener false
        }

        binding.btnInputSummoner.setOnClickListener{
            binding.pbLoding.visibility = View.VISIBLE
            viewModel.getSummonerIdInfo(binding.etInputSummoner.text.toString().trim())
            binding.etInputSummoner.setText("")
        }

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.summoerIdInfoLiveData.observe(this){
            if (it == null) {
                Toast.makeText(
                    applicationContext,
                    R.string.not_exist_summoner,
                    Toast.LENGTH_SHORT
                ).show()
                binding.pbLoding.visibility = View.GONE
            } else {
                puuid = it.puuid
            }
        }

        viewModel.summonerRankInfoLiveData.observe(this){
            if(it != null){
                // 랭크 정보 가져오기 성공
                setRankInfo(it)
            } else {
                binding.pbLoding.visibility = View.GONE
            }
        }
    }

    private fun setRankInfo(summonerRankInfo: SummonerRankInfo){
        binding.tvSummonerName.text = summonerRankInfo.summonerName
        val tierRank = summonerRankInfo.tier + " " + summonerRankInfo.rank
        binding.tvTier.text = tierRank

        if (summonerRankInfo.tier == "UNRANKED"){
            binding.tvRankType.text = ""
            binding.tvLp.text = ""
            binding.tvTotalWinRate.text = ""
            binding.tvTotalWinLose.text = ""

        } else {
            binding.tvRankType.text = summonerRankInfo.queueType
            val point = summonerRankInfo.leaguePoints.toString() + "LP"
            binding.tvLp.text = point
            val rate = summonerRankInfo.wins.toDouble() / (summonerRankInfo.wins + summonerRankInfo.losses).toDouble() * 100
            binding.tvTotalWinRate.text = String.format(Locale.getDefault(), format = "%.2f%%", rate)
            val winsAndLosses = summonerRankInfo.wins.toString() + "승 "+ summonerRankInfo.losses.toString() + "패"
            binding.tvTotalWinLose.text = winsAndLosses
        }
        setTierEmblem(summonerRankInfo.tier)

        binding.layoutInfo.visibility = View.VISIBLE

        binding.pbLoding.visibility = View.GONE
    }

    private fun setTierEmblem(tier: String){
        when (tier) {

            "UNRANKED" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_unranked)
            "IRON" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_iron)
            "BRONZE" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_bronze)
            "SILVER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_silver)
            "GOLD" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_gold)
            "PLATINUM" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_platinum)
            "EMERALD" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_emerald)
            "DIAMOND" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_diamond)
            "MASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_master)
            "GRANDMASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_grandmaster)
            "CHALLENGER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_challenger)
        }
    }
}