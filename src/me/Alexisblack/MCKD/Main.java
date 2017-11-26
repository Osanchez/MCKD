package me.Alexisblack.MCKD;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Main extends JavaPlugin {
    public static HashMap<String, KDA> combatLogs;

    @Override
    public void onEnable() {
        //load hash map
        try {
            combatLogs = loadHashMap();
        } catch (IOException e) {
            combatLogs = new HashMap<String, KDA>();
        }

        //register events
        getServer().getPluginManager().registerEvents(new CombatListener(), this);

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
