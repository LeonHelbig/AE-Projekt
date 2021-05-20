<!-------------------------------------------------------
Author: Leon Helbig, Karsten Kaschte, Justin Kühl
Date: 20.05.2021
Version: 1.0
--------------------------------------------------------->
<!DOCTYPE html>
<html lang="de">
  <head>
    <link rel="stylesheet" href="style.css">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LoL Playerpage</title>
  </head>
  <body>
    <div class="topnav">
        <a href="lolstats.php">Home</a>
        <a class="active" href="">Stats</a>
        <a href="about.html">About</a>
    </div>
    <?php
        error_reporting(E_ERROR | E_PARSE); //schält alle Warnungen aus
        
        // Erstellt eine Verbindung zur Datenbank
        $link = mysqli_connect("localhost", "root", "", "leaguegg");

        // Überprüft die Verbindung zur Datenbank
        if($link === false){
            die("ERROR: Could not connect. " . mysqli_connect_error());
        }
        //Wählt alle Einträge aus, an welchen Stellen der Eintrag "summonername" dem zuvor eingegebenen Namen gleicht und speichert sie in Variablen
        $sql = " SELECT Id, summonername, summoner_id, profileicon_id, summonerLevel, ranking, tier  FROM summoner WHERE summonername LIKE '%{$_GET['summonername']}%'"; 
        if($result = mysqli_query($link, $sql)){
            if(mysqli_num_rows($result) > 0){
                while($row = mysqli_fetch_array($result)){
                    $id = $row['Id'];
                    $summonerName = $row['summonername'];
                    $summonerId = $row['summoner_id'];
                    $profileIconId = $row['profileicon_id'];
                    $summonerLevel = $row['summonerLevel'];
                    $ranking = $row['ranking'];
                    $tier = $row['tier'];
                }
                mysqli_free_result($result);
            } else{
                echo "No records matching your query were found.";
            }
        } else{
            echo "ERROR: Could not able to execute $sql. " . mysqli_error($link);
        }
    ?>
    <div class="content">
        <div class="playerinformation">
            <div class="spacer"> <!-- Platzhalter für die erste Spalte des Flexgrids -->
            </div>
            <div class="mainstat">
                <img class="playericon" src="http://ddragon.leagueoflegends.com/cdn/11.8.1/img/profileicon/<?php echo $profileIconId ?>.png" href="#">
                <img class="rankedborder" <?php if($ranking == "unranked"){ echo "style='visibility:hidden;'"; }; ?> src="https://opgg-static.akamaized.net/images/borders2/<?php echo strtolower($tier) ?>.png" href="#">
                <div class="playerlevel">
                    <?php
                        echo "Level: " . $summonerLevel; 
                    ?>
                </div>
            </div>
            <div>
                <div class="summonername">
                    Summonername: 
                </div>
                <div class="summonername name">
                    <b><?php
                        echo $summonerName; 
                    ?></b>
                </div>
                <div class="summonerrank">
                    <?php 
                        if($ranking == "unranked"){
                            echo "Unranked";    
                        }else{
                            echo "Rank: " . ucwords(strtolower($tier)) . '&nbsp;'; echo $ranking;
                        }
                    ?>
                        
                </div>
            </div>
            <div class="spacer"> <!-- Platzhalter für eine Spalte des Flexgrids -->
            </div>
            <div class="spacer">
            </div>
            <div class="spacer">
            </div>
            <div class="spacer">
            </div>
            <div class="spacer">
            </div>
            <div class="spacer"> 
            </div>
            <div class="spacer">
            </div>
        <?php
            //wählt alle Einträge aus an welchen Stellen die "summoner_id" der ID gleicht, welche in die Variable $id gespeichert worden ist
            $sqlMatch = "SELECT kills, deaths, assists, win FROM matches WHERE summoner_id LIKE '%{$id}%'";
            if($matchResult = mysqli_query($link, $sqlMatch)){
                if(mysqli_num_rows($matchResult) > 0){
                    echo "<table class='matchhistory'>";
                        echo "<tr class='tr'>";
                            echo "<th class='th'>Kills</th>";
                            echo "<th class='th'>Deaths</th>";
                            echo "<th class='th'>Assists</th>";
                            echo "<th class='th'>Win</th>";
                        echo "</tr>";
                        while($row = mysqli_fetch_array($matchResult)){
                            echo "<tr class='tr'>";
                                echo "<td class='td'>" . $row['kills'] . "</td>";
                                echo "<td class='td'>" . $row['deaths'] . "</td>";
                                echo "<td class='td'>" . $row['assists'] . "</td>";
                                echo "<td class='td'>" . $row['win'] . "</td>";
                            echo "</tr>";
                        }
                    echo "</table>";
                    mysqli_free_result($matchResult);
                } else{
                    echo "No records matching your query were found.";
                }
            } else{
                echo "ERROR: Could not able to execute $sqlMatch. " . mysqli_error($link);
            }
        ?>
        </div>
    </div>
  </body>
</html>