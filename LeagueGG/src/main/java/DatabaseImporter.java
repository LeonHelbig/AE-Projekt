/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseImporter {

    public void importToDatabase(Summoner summoner){

        try {
            //Verbindung zur Datenbank herstellen
            String url = "jdbc:mysql://localhost:3306/leaguegg?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";

            //SQL Queries zum einfügen der Daten in die Tabellen
            String insertQuerySummoner = " insert into summoner (summonername, summoner_id, account_id, profileicon_id, puuid, summonerLevel, ranking, tier)" + " values (?, ?, ?, ?, ?, ?, ?, ?)";
            String insertQueryMatchHistory = " insert into matches (summoner_id, kills, deaths, assists, win)" + " values (?, ?, ?, ?, ?)";
            String summonerIdQuery = String.format(" select id from summoner where summonername like '%s' ", summoner.getSummonerName());
            int summonerId = 0;

            Connection databaseConnection = DriverManager.getConnection(url, "root", "");

            //Entfernt bereits gesuchte summoner und dessen Spielverlauf, um Redundanz zu verhintern
            clearExistingSummonerFromDatabase(databaseConnection, summoner.getSummonerName());

            //Einfügen der Daten in die Tabellen
            PreparedStatement summonerStatement = databaseConnection.prepareStatement(insertQuerySummoner);

            summonerStatement.setString(1, summoner.getSummonerName());
            summonerStatement.setString(2, summoner.getSummonerId());
            summonerStatement.setString(3, summoner.getAccountId());
            summonerStatement.setInt(4, summoner.getPofileIconId());
            summonerStatement.setString(5, summoner.getPuuid());
            summonerStatement.setLong(6, summoner.getSummonerLevel());
            summonerStatement.setString(7, summoner.getRank());
            summonerStatement.setString(8, summoner.getTier());

            summonerStatement.execute();

            PreparedStatement historyStatement = databaseConnection.prepareStatement(insertQueryMatchHistory);

            //Für jedes gefundene Spiel, das ein Spieler gespielt hat, soll dieses in eine extra Tabelle, mit der Id des Spielers geladen werden
            for (Match match : summoner.getMatchHistory()) {

                ResultSet set = summonerStatement.executeQuery(summonerIdQuery);

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
    public void clearExistingSummonerFromDatabase(Connection databaseConnection, String summonername){

        //Queries zum entfernen eines Spielereintrags
        String removeSummonerQuery = String.format("delete from summoner where summonername like '%s'", summonername);
        String summonerIdQuery = String.format(" select id from summoner where summonername like '%s'", summonername);
        int summonerId = 0;

        try{

            PreparedStatement removeSummoner = databaseConnection.prepareStatement(removeSummonerQuery);

            //Beschaffung der Id des angelegten Spielers
            ResultSet set = removeSummoner.executeQuery(summonerIdQuery);

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
