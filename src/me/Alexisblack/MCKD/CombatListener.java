package me.Alexisblack.MCKD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;


public class CombatListener implements Listener {

    public static HashMap<UUID, KDA> combatLogs = new HashMap<>();

    //display any important information to the player
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Alpha Testing Message
        Bukkit.broadcastMessage("Welcome " + event.getPlayer().getName() + " to KitPVP! We are trying a new K/D/A system. Please report any " +
                "bugs you encounter to server admin.");

        //add score board to player
        Player player = event.getPlayer();
        player.setScoreboard(Main.statsBoard);

        try {
            //load players kda from hash map
            KDA playerKDA = combatLogs.get(player.getUniqueId());
            String playerKills = Integer.toString(playerKDA.getKills());
            String playerDeaths = Integer.toString(playerKDA.getDeaths());
            String playerAssists = Integer.toString(playerKDA.getAssists());

            //update scoreboard with hash map values
            Scoreboard board = player.getScoreboard();
            board.getTeam("Kills").setSuffix(playerKills);
            board.getTeam("Deaths").setSuffix(playerDeaths);
            board.getTeam("Assists").setSuffix(playerAssists);
        } catch (Exception e) { //key not found exception, create new entry for hash
            //create new KDA object and add it to the hash map with key player uuid
            KDA newKDA = new KDA();
            UUID newPlayer = event.getPlayer().getUniqueId();
            combatLogs.put(newPlayer, newKDA);

            //add 0's to scoreboard as place values since the player was not in the hash map
            Scoreboard board = player.getScoreboard();
            board.getTeam("Kills").setSuffix(Integer.toString(0));
            board.getTeam("Deaths").setSuffix(Integer.toString(0));
            board.getTeam("Assists").setSuffix(Integer.toString(0));

            //TODO: add default values of 0 to the scoreboard so that its not blank before a player kills player
        }
    }

    //Handles kills and deaths of players
    @EventHandler
        public void onPlayerKill(PlayerDeathEvent event) {
            if (event.getEntity() instanceof Player) {
                final Player defender = event.getEntity();
                UUID defenderID = defender.getUniqueId();
                if (event.getEntity().getKiller() instanceof Player) {
                    final Player attacker = event.getEntity().getKiller();
                    UUID attackerID = attacker.getUniqueId();
                    //attacker
                    if (combatLogs.containsKey(attackerID)) {
                        //increment kills in hash map
                        KDA attackerKDA = combatLogs.get(attackerID);
                        attackerKDA.incrementKills();
                        combatLogs.replace(attackerID, attackerKDA);
                        //update scoreboard
                        Scoreboard attackerBoard = attacker.getScoreboard();
                        attackerBoard.getTeam("Kills").setSuffix(Integer.toString(attackerKDA.getKills()));

                    } else {
                        //add killer to hash map for the first time
                        KDA newKiller = new KDA();
                        newKiller.incrementKills();
                        combatLogs.put(attackerID, newKiller);
                        //update scoreboard
                        Scoreboard attackerBoard = attacker.getScoreboard();
                        attackerBoard.getTeam("Kills").setSuffix(Integer.toString(newKiller.getKills()));
                    }
                    //defender
                    if (combatLogs.containsKey(defenderID)) {
                        //increment deaths in hashmap
                        KDA defenderKDA = combatLogs.get(defenderID);
                        defenderKDA.incrementDeaths();
                        combatLogs.replace(defenderID, defenderKDA);
                        //update scoreboard
                        Scoreboard defenderBoard = defender.getScoreboard();
                        defenderBoard.getTeam("Deaths").setSuffix(Integer.toString(defenderKDA.getDeaths()));
                    } else {
                        //add defender to hash map for the first time
                        KDA newDefender = new KDA();
                        newDefender.incrementKills();
                        combatLogs.put(defenderID, newDefender);
                        //update scoreboard
                        Scoreboard defenderBoard = defender.getScoreboard();
                        defenderBoard.getTeam("Deaths").setSuffix(Integer.toString(newDefender.getDeaths()));
                    }

                }
            }
    }

    //TODO: Listener for Assists
}
