/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.sql.*;


public class Main {

    public static void main(String[] args) {
        //Name des Spielers wird zusammengesetzt, damit der Name auch Leerzeichen enthalten darf
        String summonerName = "";
        for(String namePart : args){
            summonerName = summonerName + " " + namePart;
        }

        /* Scanner zum manuellen eingeben von Namen zum testen
       System.out.println("Gib einen Namen ein!");
        Scanner scanner = new Scanner(System.in);
        String summonerName = scanner.nextLine();
         */


        Summoner summoner = getSummonerByName(summonerName);

        // System.out.println(summoner.tier + " " + summoner.rank); Testing

        /*for(int i = 0; i < summoner.matchHistory.size(); i++) Testing
            System.out.println(summoner.matchHistory.get(i).isWin() + " " + summoner.matchHistory.get(i).getKills() +"/"+ summoner.matchHistory.get(i).getDeaths()+"/"+summoner.matchHistory.get(i).getAssists());
            */

        importToDatabase(summoner);
    }
    // Methode zum Erhalt der Infomationen eines summoners, welche mit dem Namen des summoners erhalten werden
    public static Summoner getSummonerByName(String name) {

        //Namen können auch Leerzeichen enthalten, wenn der Name per CMD/jar übergeben wird. Da der Name aber in die  Abfrage-URL eingebaut werden muss, müssen alle Leerzeichen durch "%20" ersetzt werden.
        while(name.contains(" ")){
            name = name.replace(" ", "%20");
        }

        String summonerUrl = String.format("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/%s", name);
        JSONObject summonerInfos =  new JSONObject(Objects.requireNonNull(getResponse(summonerUrl)));

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
        JSONArray rankedInfos = new JSONArray(Objects.requireNonNull(getResponse(rankedUrl)));

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
    public static List<Match> getMatchHistoryOfSummoner(String accountId){

        String matchHistoryUrl = String.format("https://euw1.api.riotgames.com/lol/match/v4/matchlists/by-account/%s", accountId);

        JSONObject jsonObjectHistory = new JSONObject(Objects.requireNonNull(getResponse(matchHistoryUrl)));
        JSONArray jsonArrayhistory = jsonObjectHistory.getJSONArray("matches");

        List<Match> matchHistory = new ArrayList<>();

        for(int i = 0; i < 10; i++){

            long gameId = jsonArrayhistory.getJSONObject(i).getLong("gameId");
            String role = jsonArrayhistory.getJSONObject(i).getString("role");
            int champion = jsonArrayhistory.getJSONObject(i).getInt("champion");
            String lane = jsonArrayhistory.getJSONObject(i).getString("lane");

            int kills = 0;
            int deaths = 0;
            int assists = 0;
            boolean win = false;

            JSONObject detailedMatchInfos = new JSONObject(Objects.requireNonNull(getResponse(String.format("https://euw1.api.riotgames.com/lol/match/v4/matches/%s", gameId))));
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

    //Methode zum senden der Anfragen an gegebene URLs
    public static String getResponse(String url) {

        //Extra Property, was statisch gesetzt werden muss (weil der key nur manuell generiert und hier eingepflegt werden muss) um die API von Riot Games anfragen zu können.
        String apiKey = "RGAPI-5c7669b8-c384-4b76-ac96-3b925b0b1d49";

        //Senden der Abfrage an den gegebenen Link und Hinzufügen des API-Keys
        try {
            URL buildUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) buildUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("X-Riot-Token", apiKey);
            connection.setConnectTimeout(5000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine = "";
            StringBuilder content = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();

            //Ausgabe der Antwort der Abfrage
            return content.toString();
        }catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }
    }
    public static void importToDatabase(Summoner summoner){

        try {
            //Verbindung zur Datenbank herstellen
            String url = "jdbc:mysql://localhost:3306/leaguegg?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";

            Connection databaseConnection = DriverManager.getConnection(url, "root", "");

            //Entfernt bereits gesuchte summoner und dessen Spielverlauf, um Redundanz zu verhintern
            clearExistingSummonerFromDatabase(databaseConnection, summoner.name);

            //SQL Queries zum einfügen der Daten in die Tabellen
            String insertQuerySummoner = " insert into summoner (summonername, summoner_id, account_id, profileicon_id, puuid, summonerLevel, ranking, tier)" + " values (?, ?, ?, ?, ?, ?, ?, ?)";
            String insertQueryMatchHistory = " insert into matches (summoner_id, kills, deaths, assists, win)" + " values (?, ?, ?, ?, ?)";

            //Einfügen der Daten in die Tabellen
            PreparedStatement summonerStatement = databaseConnection.prepareStatement(insertQuerySummoner);

            summonerStatement.setString(1, summoner.name);
            summonerStatement.setString(2, summoner.summonerId);
            summonerStatement.setString(3, summoner.accountId);
            summonerStatement.setInt(4, summoner.profileIconId);
            summonerStatement.setString(5, summoner.puuid);
            summonerStatement.setLong(6, summoner.summonerLevel);
            summonerStatement.setString(7, summoner.rank);
            summonerStatement.setString(8, summoner.tier);

            summonerStatement.execute();

            PreparedStatement historyStatement = databaseConnection.prepareStatement(insertQueryMatchHistory);

            //Für jedes gefundene Spiel, das ein Spieler gespielt hat, soll dieses in eine extra Tabelle, mit der Id des Spielers geladen werden
            for (Match match : summoner.matchHistory) {

                String summonerIdQuery = String.format(" select id from summoner where summonername like '%s' ", summoner.name);
                ResultSet set = summonerStatement.executeQuery(summonerIdQuery);
                int summonerId = 0;
                if(set.next()) {
                    summonerId = set.getInt("id");
                }

                historyStatement.setInt(1,summonerId);
                historyStatement.setInt(2, match.getKills());
                historyStatement.setInt(3, match.getDeaths());
                historyStatement.setInt(4, match.getAssists());
                historyStatement.setBoolean(5,match.isWin());

                historyStatement.execute();
            }

            //Verbindung zur Datenbank trennen
            databaseConnection.close();

        }catch(Exception ex) {
            //Ausgabe des Exception-Message
            System.out.println(ex.getMessage());
        }

    }
    public static void clearExistingSummonerFromDatabase(Connection databaseConnection, String summonername){

        try{
            //Erstellung der Queries zum entfernen eines Spielereintrags
            String removeSummonerQuery = String.format("delete from summoner where summonername like '%s'", summonername);
            PreparedStatement removeSummoner = databaseConnection.prepareStatement(removeSummonerQuery);

            //Beschaffung der Id des angelegten Spielers
            String summonerIdQuery = String.format(" select id from summoner where summonername like '%s'", summonername);
            ResultSet set = removeSummoner.executeQuery(summonerIdQuery);
            int summonerId = 0;
            if(set.next()) {
                summonerId = set.getInt("id");
            }

            //Statement zum Löschen der Matches eines Spielers, basierend auf seiner Id
            String removeMatchesByIdQuery = String.format(" delete from matches where summoner_id = %d", summonerId);
            PreparedStatement removeMatchesOfSummoner = databaseConnection.prepareStatement(removeMatchesByIdQuery);

            //Zuerst werden die Matches gelöscht und dann der Eintrag zum Spieler selbst
            removeMatchesOfSummoner.execute();
            removeSummoner.execute();

        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }

}