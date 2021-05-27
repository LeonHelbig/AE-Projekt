/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

import java.util.List;

public class Summoner {
    private String name;
    private String summonerId;
    private String accountId;
    private int profileIconId;
    private String puuid;
    private long summonerLevel;
    private String rank;
    private String tier;
    private  List<Match> matchHistory;

    public Summoner(String name, String summonerId, String accountId, int profileIconId, String puuid, long summonerLevel, String rank, String tier, List<Match> matchHistory) {
        this.name = name;
        this.summonerId = summonerId;
        this.accountId = accountId;
        this.profileIconId = profileIconId;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
        this.rank = rank;
        this.tier = tier;
        this.matchHistory = matchHistory;
    }

    public List<Match> getMatchHistory(){ return matchHistory;}

    public String getTier(){ return tier;}

    public String getRank() {
        return rank;
    }

    public String getSummonerName() {
        return name;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getPofileIconId() {
        return profileIconId;
    }

    public String getPuuid() {
        return puuid;
    }

    public long getSummonerLevel() {
        return summonerLevel;
    }
}