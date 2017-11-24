package me.Alexisblack.MCKD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public class Main extends JavaPlugin implements Listener {
    private Scoreboard statsBoard;
    private Objective stats;


    @Override
    public void onEnable() {
        //register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);

        //initialize the score board
        statsBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        //Scoreboard configuration
        stats = statsBoard.registerNewObjective("KDA", "dummy");
        stats.setDisplayName("KDA");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);

        //kills, Deaths, Assists team deceleration and initialization
        Team kills = statsBoard.registerNewTeam("Kills");
        kills.addEntry(ChatColor.GREEN + "Kills: ");
        kills.setPrefix("0");

        Team deaths = statsBoard.registerNewTeam("Deaths");
        deaths.addEntry(ChatColor.RED + "Deaths: ");
        deaths.setPrefix("0");

        Team assists = statsBoard.registerNewTeam("Assists");
        assists.addEntry(ChatColor.YELLOW + "Assists: ");
        assists.setPrefix("0");

        //set scoreboard positions
        stats.getScore(ChatColor.GREEN + "Kills: ").setScore(0);
        stats.getScore(ChatColor.RED + "Deaths: ").setScore(0);
        stats.getScore(ChatColor.YELLOW + "Assists: ").setScore(0);
    }

    @Override
    public void onDisable() {
        //runs then the server stops and disables all plugins
    }


    //display any important information to the player
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Alpha Testing Message
        Bukkit.broadcastMessage("Welcome " + event.getPlayer().getName() + " to KitPVP! We are trying a new K/D/A system. Please report any " +
                "bugs you encounter to server admin.");

        //add score board to player
        Player player = event.getPlayer();
        player.setScoreboard(statsBoard);

        try {
            //load players kda from hash map
            KDA playerKDA = CombatListener.combatLogs.get(player.getUniqueId());
            String playerKills = Integer.toString(playerKDA.getKills());
            String playerDeaths = Integer.toString(playerKDA.getDeaths());
            String playerAssists = Integer.toString(playerKDA.getAssists());

            //update scoreboard with hash map values
            Scoreboard board = player.getScoreboard();
            board.getTeam("Kills").setPrefix(playerKills);
            board.getTeam("Deaths").setPrefix(playerDeaths);
            board.getTeam("Assists").setPrefix(playerAssists);
        } catch (Exception e) { //key not found exception, create new entry for hash
            //create new KDA object and add it to the hash map with key player uuid
            KDA newKDA = new KDA();
            UUID newPlayer = event.getPlayer().getUniqueId();
            CombatListener.combatLogs.put(newPlayer, newKDA);
        }


    }
}
