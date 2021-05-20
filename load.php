<!-------------------------------------------------------
Author: Leon Helbig, Karsten Kaschte, Justin Kühl
Datum: 20.05.2021
Version: 1.0
--------------------------------------------------------->
<!DOCTYPE html>
<html lang="de">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>LoL-Stats</title>
    </head>
    <body>
        <div class="content">
            <div class="loading-ring"><div></div><div></div><div></div><div></div></div> <!-- Ladesymbol -->
        </div>
        <style>
            body{
                background-color: #5384E8;
            }

            .content{
                position: relative;
                margin-left: calc(50% - 68px); /* calc berechnet den Wert, welcher in der Klammer steht */
                top: 200px;
            }

            .loading-ring {
                display: inline-block;
                position: relative;
                width: 80px;
                height: 80px;
            }

            .loading-ring div {
                box-sizing: border-box;
                display: block;
                position: absolute;
                width: 128px;
                height: 128px;
                margin: 8px;
                border: 8px solid #fff;
                border-radius: 50%;
                animation: loading-ring 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
                border-color: #fff transparent transparent transparent;
            }

            .loading-ring div:nth-child(1) {
                animation-delay: -0.45s;
            }

            .loading-ring div:nth-child(2) {
                animation-delay: -0.3s;
            }

            .loading-ring div:nth-child(3) {
                animation-delay: -0.15s;
            }

            @keyframes loading-ring {
                0% {
                    transform: rotate(0deg);
                }
                100% {
                    transform: rotate(360deg);
                }
            }
        </style>
        <script>
            setTimeout(function(){ // diese Funktion erzeugt einen 10s langen delay und leitet dann, mit Parameter in der URL, auf die nächste Seite weiter
                window.location.href = 'playerpage.php?summonername=<?php echo $_GET['summonername'];?>';
            }, 10000);
        </script>
    </body>
</html>
