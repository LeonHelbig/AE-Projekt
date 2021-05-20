/*********************************************************
Author: Leon Helbig, Karsten Kaschte, Justin Kühl
Date: 20.05.2021
Version: 1.0
**********************************************************/
const cors = require('cors');
const express = require('express')
const path = require('path')
const { exec } = require('child_process')
const webapp = express()
webapp.use(cors());
webapp.use(express.json());
webapp.use(express.static('public'));
webapp.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'))

})
webapp.post('/run', (req, res) => {
    console.log(req.body);
var escapedString = req.body.summonername; 

  exec(`java -jar LeagueGGReformed.jar ${escapedString}`, function (error, stdout, stderr) { //führt die angegebene jar Datei aus, mit dem Parameter, welcher er sich aus dem Eingabefeld der Startseite holt
    if (error) res.send(error)
    res.send(stdout)
    console.error(stderr)
  })
})
const server = webapp.listen(3000, () => { 
  console.log('Now listening on 3000') //gibt beim Starten des Servers über die Windows Powershell eine Nachricht
})