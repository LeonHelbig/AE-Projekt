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
    <title>LoL-Stats</title>
  </head>
  <body>
    <div> 
      <div class="header">
        <img class="logo" src="logo.png">
        <h1 class="stats">stats</h1>
      </div>
      <div class="inputfield">
        <form onSubmit="search();" action="load.php" method="get"> <!-- Formular, welches bei Bestätigung die JavaScript Funktion search() ausführt -->
          <input type="text" placeholder="Enter your Summoner name" name="summonername" id="summonername" minlength="3" class="summonerinput" required /></br>
          <input type="submit" class="button" value="Submit" name="test"/>
        </form>
      </div>
    </div>
  <script>
      function search() //Diese Funktion, gibt den Wert aus dem Eingabe Feld "summonername" an den lokalen Webserver mit Port 3000 weiter
      {
        var inputVal = document.getElementById("summonername").value;
        console.log(inputVal);
        fetch("http://localhost:3000/run", {
          method:"POST",
          headers: {
          'Content-Type': 'application/json'
          },
          body:JSON.stringify({summonername: inputVal})});
      };
  </script>
  </body>
</html>