package com.lolhistory.repository

import com.google.gson.annotations.SerializedName
import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.SummoerIdInfo
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.retrofit.APIClinent
import com.lolhistory.retrofit.RiotAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// Riot 게임 데이터를 가져오는 리포지토리 객체입니다.
object RiotRepository {
    // RiotAPI와 RiotV5API 클라이언트를 생성합니다.
    private val riotAPI = APIClinent.getRiotClient().create(RiotAPI::class.java)
    private val riotV5API = APIClinent.getRiotV5Client().create(RiotAPI::class.java)

    // 소환사 이름을 이용하여 소환사 정보를 가져오는 함수입니다.
    fun getSummonerIdInfo(summonerName: String): Single<SummoerIdInfo> = riotAPI
        .getSummonerIdInfo(summonerName)  // RiotAPI를 통해 소환사 정보 요청을 보냅니다.
        .subscribeOn(Schedulers.io())       // 백그라운드 스레드에서 요청을 처리합니다.
        .observeOn(AndroidSchedulers.mainThread())  // UI 스레드에서 결과를 처리합니다.

    // 소환사 ID를 이용하여 소환사의 랭크 정보를 가져오는 함수입니다.
    fun getSummonerRankInfo(summonerId: String): Single<List<SummonerRankInfo>> = riotAPI
        .getSummonerRankInfo(summonerId)  // RiotAPI를 통해 랭크 정보 요청을 보냅니다.
        .subscribeOn(Schedulers.io())       // 백그라운드 스레드에서 요청을 처리합니다.
        .observeOn(AndroidSchedulers.mainThread())  // UI 스레드에서 결과를 처리합니다.

    fun getMatchHistoryList(
        puuid:String,
        start:Int,
        count:Int
    ): Single<List<String>> = riotV5API
        .getMatchHistoryList(puuid, start, count)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getMatchHistory(matchId: String): Single<MatchHistory> = riotV5API
        .getMatchHistory(matchId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

// 이 파일은 Riot 게임 데이터를 가져오기 위한 리포지토리 클래스를 정의하고 있습니다.
// 소환사 정보와 랭크 정보를 가져오는 함수를 제공합니다.
// 이 함수들은 백그라운드에서 데이터를 요청하고, 가져온 결과를 UI 스레드에서 처리하여
// 원할한 앱 사용자 경험을 제공합니다.
