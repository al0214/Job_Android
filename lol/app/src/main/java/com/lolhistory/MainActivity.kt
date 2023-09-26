package com.lolhistory

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lolhistory.databinding.ActivityMainBinding
import com.lolhistory.datamodel.SummonerRankInfo
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var puuid = ""

    private var isVisibleLayoutInfo = false

    private val inputMethodManager by lazy {
        this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

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
            inputMethodManager.hideSoftInputFromWindow(binding.etInputSummoner.windowToken, 0)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.setHasFixedSize(true)
        binding.layoutSwipe.setOnRefreshListener {
            viewModel.getSummonerIdInfo(binding.tvSummonerName.text.toString())
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
                Log.d("TESTLOG", "summonerRankInfoLiveData observe")
                setRankInfo(it)
                binding.layoutInput.visibility = View.GONE
            } else {
                binding.pbLoding.visibility = View.GONE
            }
        }

        viewModel.matchHistoryListLiveData.observe(this){
            if (it.isEmpty()){
                // 대전 기록 없음
            }else{
                val historyAdapter = HistoryAdapter(ArrayList(it), puuid)
                binding.rvHistory.adapter = historyAdapter
                binding.layoutSwipe.isRefreshing = false
            }
            binding.pbLoding.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if(isVisibleLayoutInfo){
            // 정보 창 출력 중
            binding.layoutInfo.visibility = View.GONE
            binding.layoutInput.visibility = View.VISIBLE
            isVisibleLayoutInfo = !isVisibleLayoutInfo
        } else {
            // 검색 창 출력 중
            finish()
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
        isVisibleLayoutInfo = true
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