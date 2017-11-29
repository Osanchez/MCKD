package me.Alexisblack.MCKD;

public class KDA {
    private int kills;
    private int deaths;
    private int assists;
    public double KDA;

    //TODO: MR Calculations ~ Maybe

    KDA() {
        kills = 0;
        deaths = 0;
        assists = 0;
        KDA = 0.00;
    }

    public void recalculateKDA() {
        KDA = (double) kills / deaths;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills += 1;
        recalculateKDA();
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths += 1;
        recalculateKDA();
    }


    public int getAssists() {
        return assists;
    }

    public void incrementAssists() {
        this.assists += 1;
    }
}
