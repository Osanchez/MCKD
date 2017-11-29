package me.Alexisblack.MCKD;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
    public static HashMap<String, KDA> combatLogs; //Player UUID, KDA Object
    public static HashMap<String, ArrayList<Player>> assistLogs; //Damaged Player UUID, PlayerAssists Object

    @Override
    public void onEnable() {
        //load hash map
        try {
            combatLogs = loadHashMap();
        } catch (IOException e) {
            combatLogs = new HashMap<String, KDA>();
        }

        //initialize assist log
        assistLogs = new HashMap<String, ArrayList<Player>>();

        //register events
        getServer().getPluginManager().registerEvents(new CombatListener(), this);

    }

    @Override
    public void onDisable() {

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
