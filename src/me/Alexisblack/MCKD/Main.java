package me.Alexisblack.MCKD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Main extends JavaPlugin {
    public static HashMap<String, KDA> combatLogs;

    private Scoreboard statsBoard;
    private Objective stats;
    private Score kills;
    private Score deaths;
    private Score assists;

    @Override
    public void onEnable() {
        //initialize the score boardK
        statsBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        //Scoreboard configuration
        stats = statsBoard.registerNewObjective("KDA", "dummy");
        stats.setDisplayName("KDA");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);

        //set scoreboard positions
        kills = stats.getScore(ChatColor.GREEN + "Kills: ");
        deaths = stats.getScore(ChatColor.RED + "Deaths: ");
        assists =  stats.getScore(ChatColor.YELLOW + "Assists: ");


        //load hash map
        try {
            combatLogs = loadHashMap();
        } catch (IOException e) {
            combatLogs = new HashMap<String, KDA>();
        }

        //register events
        getServer().getPluginManager().registerEvents(new CombatListener(statsBoard), this);

    }

    @Override
    public void onDisable() {
        //runs then the server stops and disables all plugins
        try {
            saveHashMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap loadHashMap() throws IOException {
        HashMap<String, KDA> loadMap = new HashMap<String, KDA>();
        Properties properties = new Properties();
        properties.load(new FileInputStream("combatLogs.properties"));
        for (String key: properties.stringPropertyNames()) {
            loadMap.put(key, (KDA) properties.get(key));
        }
        return loadMap;
    }

    private void saveHashMap() throws IOException {
        HashMap<String, KDA> saveMap = combatLogs;
        Properties properties = new Properties();
        for (Map.Entry<String, KDA> entry : saveMap.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }
        properties.store(new FileOutputStream("combatLogs.properties"), null);
    }

}
