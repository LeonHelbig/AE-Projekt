/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SummonerCreator {

    // Methode zum Erhalt der Infomationen eines summoners, welche mit dem Namen des summoners erhalten werden
    public Summoner getSummonerByName(String name) {

        //Namen können auch Leerzeichen enthalten. Da der Name aber in die  Abfrage-URL eingebaut werden muss, müssen alle Leerzeichen durch "%20" ersetzt werden.
        while(name.contains(" ")){
            name = name.replace(" ", "%20");
        }

        Requester requester = new Requester();
        String summonerUrl = String.format("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/%s", name);
        JSONObject summonerInfos =  new JSONObject(Objects.requireNonNull(requester.getResponse(summonerUrl)));

        //JSON-Attribute, die die Abfrage zurück gibt und für weitere Abfragen weiterverwendet werden.
        String summonerName = summonerInfos.getString("name");
        String summonerId = summonerInfos.getString("id");
        String accountId = summonerInfos.getString("accountId");
        int profileIconId = summonerInfos.getInt("profileIconId");
        String puuid = summonerInfos.getString("puuid");
        long summonerLevel = summonerInfos.getLong("summonerLevel");

        // "Tier" und "Rank" beziehen sich auf die Ranglistenplatzierung und sind per default als "unranked" definiert, falls der Spieler noch keine Ranglistenspiele gespielt hat
        String tier = "unranked";
        String rank = "unranked";

        String rankedUrl = String.format("https://euw1.api.riotgames.com/lol/league/v4/entries/by-summoner/%s", summonerId);
        JSONArray rankedInfos = new JSONArray(Objects.requireNonNull(requester.getResponse(rankedUrl)));

        for (int i = 0; i < rankedInfos.length(); i++) {
            JSONObject jsonobject = rankedInfos.getJSONObject(i);
            tier = jsonobject.getString("tier");
            rank = jsonobject.getString("rank");
        }

        //Die letzten 10 Spiele (Verloren oder Gewonnen? + extra Werte zum Spiel selbst) eines summoners werden hier ausgelesen und in einer Liste gespeichert
        List<Match> matchHistory = getMatchHistoryOfSummoner(accountId);

        return (new Summoner(summonerName, summonerId, accountId, profileIconId, puuid, summonerLevel, rank, tier, matchHistory));
    }

    //Generiert den Spielverlauf eines gesuchten summoners
    public List<Match> getMatchHistoryOfSummoner(String accountId){

        String matchHistoryUrl = String.format("https://euw1.api.riotgames.com/lol/match/v4/matchlists/by-account/%s", accountId);
        String gameUrl;
        int kills = 0;
        int deaths = 0;
        int assists = 0;
        boolean win = false;
        long gameId;
        String role;
        int champion;
        String lane;
        Requester requester = new Requester();

        JSONObject jsonObjectHistory = new JSONObject(Objects.requireNonNull(requester.getResponse(matchHistoryUrl)));
        JSONArray jsonArrayhistory = jsonObjectHistory.getJSONArray("matches");

        List<Match> matchHistory = new ArrayList<>();

        for(int i = 0; i < 10; i++){

            gameId = jsonArrayhistory.getJSONObject(i).getLong("gameId");
            role = jsonArrayhistory.getJSONObject(i).getString("role");
            champion = jsonArrayhistory.getJSONObject(i).getInt("champion");
            lane = jsonArrayhistory.getJSONObject(i).getString("lane");

            gameUrl = String.format("https://euw1.api.riotgames.com/lol/match/v4/matches/%s", gameId);
            JSONObject detailedMatchInfos = new JSONObject(Objects.requireNonNull(requester.getResponse(gameUrl)));
            JSONArray participants = detailedMatchInfos.getJSONArray("participants");

            //Über den gespielten Charakter (Champion) ermitteln wir, welcher der 10 Spieler eines Matches der gesuchte ist und lesen seine Stats aus
            for(int j = 0; j < participants.length(); j++){

                if(champion == participants.getJSONObject(j).getInt("championId")){
                    JSONObject stats = participants.getJSONObject(j).getJSONObject("stats");
                    kills = stats.getInt("kills");
                    deaths = stats.getInt("deaths");
                    assists = stats.getInt("assists");
                    win = stats.getBoolean("win");
                    break;
                }
            }
            matchHistory.add((new Match(gameId, role, champion, lane, kills, deaths, assists, win)));
        }
        return matchHistory;
    }
}
