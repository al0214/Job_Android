package com.lolhistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lolhistory.datamodel.SummoerIdInfo
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.repository.RiotRepository
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class MainActivityViewModel: ViewModel() {

    private val _summonerIdInfoLiveData = MutableLiveData<SummoerIdInfo>()
    val summoerIdInfoLiveData: LiveData<SummoerIdInfo> get () = _summonerIdInfoLiveData

    private val _summonerRankInfoLiveData = MutableLiveData<SummonerRankInfo>()
    val summonerRankInfoLiveData: LiveData<SummonerRankInfo> get() = _summonerRankInfoLiveData
    fun getSummonerIdInfo(summonerName: String) {
        RiotRepository.getSummonerIdInfo(summonerName)
            .subscribe(object : SingleObserver<SummoerIdInfo>{
                override fun onSubscribe(d: Disposable) {
                    // 구독 시 실행

                }

                override fun onSuccess(t: SummoerIdInfo) {
                    // ID 정보 호출 성공
                    _summonerIdInfoLiveData.value = t
                    // 랭크 정보 검색
                    getSummonerRankInfo(t.id)
                    // 매치 리스트 검색
                }

                override fun onError(e: Throwable) {
                    // 호출 실패
                    _summonerIdInfoLiveData.value = null
                    Log.e("TESTLOG", "[getSummonerIdInfo] exception : $e")
                }
            })

    }

    fun getSummonerRankInfo(id: String){
        RiotRepository
            .getSummonerRankInfo(id)
            .subscribe(object : SingleObserver<List<SummonerRankInfo>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: List<SummonerRankInfo>) {
                    setSummonerRankInfo(t)
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    private fun setSummonerRankInfo(summonerRankInfoList: List<SummonerRankInfo>){
        var soloRankInfo: SummonerRankInfo? = null
        var flexRankInfo: SummonerRankInfo? = null
        var soloRankTierPoint = 0
        var flexRankTierPoint = 0
        val summonerRankInfo: SummonerRankInfo

        if (summonerRankInfoList.isEmpty()){
            summonerRankInfo = SummonerRankInfo(
                _summonerIdInfoLiveData.value!!.name,
                "",
                "UNRANKED",
                "",
                0,
                0,
                0
                )
            } else if (summonerRankInfoList.size == 1 && summonerRankInfoList[0].queueType == "CHERY") {
            summonerRankInfo = SummonerRankInfo(
                _summonerIdInfoLiveData.value!!.name,
                "CHERY",
                "UNRANKED",
                "",
                0,
                0,
                0
            )
        } else{
            for (info in summonerRankInfoList){
                if (info.queueType == "RANKED_SOLO_5x5"){
                    // 솔랭
                    soloRankInfo = info
                    soloRankTierPoint = calcTier(info.tier, info.rank, info.leaguePoints)
                } else if (info.queueType == "RANKED_FLEX_SR"){
                    // 자랭
                    flexRankInfo = info
                    flexRankTierPoint = calcTier(info.tier, info.rank, info.leaguePoints)
                }
            }
            summonerRankInfo = if (soloRankTierPoint < flexRankTierPoint){
                flexRankInfo!!
            } else {
                soloRankInfo!!
            }
        }
        _summonerRankInfoLiveData.value = summonerRankInfo
    }


    private fun calcTier(tier: String, rank: String, lp: Int): Int{
        var tierPoint = 0
        when (tier) {
            "IRON" -> tierPoint = 0
            "BRONZE" -> tierPoint = 1000
            "SILVER" -> tierPoint = 2000
            "GOLD" -> tierPoint = 3000
            "PLATINUM" -> tierPoint = 4000
            "EMERALD" -> tierPoint = 5000
            "DIAMOND" -> tierPoint = 6000
            "MASTER" -> tierPoint = 7000
            "GRANDMASTER" -> tierPoint = 8000
            "CHALLENGER" -> tierPoint = 9000
        }
        when (rank){
            "IV" -> tierPoint += 100
            "III" -> tierPoint += 300
            "II" -> tierPoint += 500
            "I" -> tierPoint += 700
        }
        tierPoint += lp
        return tierPoint
    }
}