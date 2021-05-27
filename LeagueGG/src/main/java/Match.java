/*
Author: Justin Kühl, Karsten Kaschte, Leon Helbig
Datum: 20.05.2021
Version: 1.0
 */

public class Match {

    private long gameId;
    private String role;
    private int champion;
    private String lane;
    private boolean win;
    private int kills;
    private int deaths;
    private int assists;

    public Match(long gameId, String role, int champion, String lane, int kills, int deaths, int assists, boolean win){
        this.gameId = gameId;
        this.role = role;
        this.champion = champion;
        this.lane = lane;
        this.win = win;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
    }

    public int getAssists() {
        return assists;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public boolean isWin() {
        return win;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getChampion() {
        return champion;
    }

    public void setChampion(int champion) {
        this.champion = champion;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }
}
