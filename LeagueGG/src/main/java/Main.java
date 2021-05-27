/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

public class Main {

    public static void main(String[] args) {

        String summonerName = "";

        //Name des Spielers wird zusammengesetzt, damit der Name der in der CMD eingegeben wird auch Leerzeichen enthalten darf
        for(String namePart : args) {
            summonerName = summonerName + " " + namePart;
        }

        SummonerCreator creator = new SummonerCreator();
        Summoner summoner = creator.getSummonerByName(summonerName);

        DatabaseImporter importer = new DatabaseImporter();
        importer.importToDatabase(summoner);
    }
}