package me.Alexisblack.MCKD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.HashMap;
import java.util.UUID;


public class CombatListener implements Listener {

    public static HashMap<UUID, KDA> combatLogs = new HashMap<>();

    //adds a new player to the hash map
    public void addPlayerLog(UUID player, KDA newKDA) {
        combatLogs.put(player, newKDA);
    }

    //Handles kills and deaths of players
    @EventHandler
        public void onPlayerKill(PlayerDeathEvent event) {
        if(event.getEntity() instanceof Player) {
            final Player defender = (Player) event.getEntity();
            UUID defenderID = defender.getUniqueId();
            if(event.getEntity().getKiller() instanceof Player) {
                final Player attacker = (Player) event.getEntity().getKiller();
                UUID attackerID = attacker.getUniqueId();
                if(combatLogs.containsKey(attackerID)) {
                    //increment killers kills
                    KDA attackerKDA = combatLogs.get(attackerID);
                    attackerKDA.incrementKills();
                    combatLogs.replace(attackerID, attackerKDA);
                } else {
                    //add killer to hash map for the first time
                    KDA newKiller = new KDA();
                    newKiller.incrementKills();
                    combatLogs.put(attackerID, newKiller);
                }
                if(combatLogs.containsKey(defenderID)) {
                    //increment defenders deaths
                    KDA defenderKDA = combatLogs.get(defenderID);
                    defenderKDA.incrementDeaths();
                    combatLogs.replace(defenderID, defenderKDA);
                } else {
                    //add defender to hash map for the first time
                    KDA newDefender = new KDA();
                    newDefender.incrementKills();
                    combatLogs.put(defenderID, newDefender);
                }

            }
        }
    }

    //TODO: Listener for Assists
}
