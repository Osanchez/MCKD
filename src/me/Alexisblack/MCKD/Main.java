package me.Alexisblack.MCKD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    public static Scoreboard statsBoard;
    public static Objective stats;

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

        Team deaths = statsBoard.registerNewTeam("Deaths");
        deaths.addEntry(ChatColor.RED + "Deaths: ");

        Team assists = statsBoard.registerNewTeam("Assists");
        assists.addEntry(ChatColor.YELLOW + "Assists: ");

        //set scoreboard positions
        stats.getScore(ChatColor.GREEN + "Kills: ").setScore(0);
        stats.getScore(ChatColor.RED + "Deaths: ").setScore(0);
        stats.getScore(ChatColor.YELLOW + "Assists: ").setScore(0);
    }

    @Override
    public void onDisable() {
        //runs then the server stops and disables all plugins
    }
}
