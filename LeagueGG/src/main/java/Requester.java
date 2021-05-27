/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Requester {

    //Methode zum senden der Anfragen an gegebene URLs
    public String getResponse(String url) {

        //Extra Property, was statisch gesetzt werden muss (weil der key nur manuell generiert und hier eingepflegt werden muss) um die API von Riot Games anfragen zu können.
        String apiKey = "RGAPI-d5b10e3a-79de-435e-a36e-986739c4fd8a";

        String inputLine = "";

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

            StringBuilder content = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();

            //Ausgabe der Antwort der Abfrage
            return content.toString();
        }catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }
}
