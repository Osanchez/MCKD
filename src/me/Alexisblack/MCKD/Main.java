package me.Alexisblack.MCKD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class Main extends JavaPlugin implements Listener{
    private Scoreboard statsBoard;
    private Objective stats;
    //private HashMap<OfflinePlayer, Score> scores = new HashMap<>();


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

        //kills, Deaths, Assists Score initialization and positions
        Score kills = stats.getScore(ChatColor.GREEN + "Kills: " + 0);
        kills.setScore(12);
        Score deaths = stats.getScore(ChatColor.RED + "Deaths: " + 0);
        deaths.setScore(11);
        Score assists = stats.getScore(ChatColor.YELLOW + "Assists: " + 0);
        assists.setScore(10);
    }

    @Override
    public void onDisable() {
        //runs then the server stops and disables all plugins
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Alpha Testing Message
        Bukkit.broadcastMessage("Welcome " + event.getPlayer().getName() + " to KitPVP! We are trying a new K/D/A system. Please report any " +
                "bugs you encounter to server admin.");

        //add score board to player
        Player p = event.getPlayer();
        p.setScoreboard(statsBoard);
    }
}
