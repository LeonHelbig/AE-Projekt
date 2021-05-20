import java.util.List;

public class Summoner {
    public String name;
    public String summonerId;
    public String accountId;
    public int profileIconId;
    public String puuid;
    public long summonerLevel;
    public String rank;
    public String tier;
    public  List<Match> matchHistory;

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