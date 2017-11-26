package me.Alexisblack.MCKD;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.util.Map;



public class CombatListener implements Listener {
    private Scoreboard statsBoard;


    public CombatListener(Scoreboard statsBoardTemplate) {
        statsBoard = statsBoardTemplate;
    }

    public void checkHashMap() {
        //check hashmaps inscase
        Iterator it = Main.combatLogs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("UUID: " + pair.getKey());
            KDA playerKDA = (KDA) pair.getValue();
            System.out.println("Kills: " + playerKDA.getKills());
            System.out.println("Deaths: " + playerKDA.getDeaths());
            System.out.println("Assists: " + playerKDA.getAssists() + "\n");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    //display any important information to the player
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        try {
            //add score board to player
            Player joinedPlayer = event.getPlayer();
            joinedPlayer.setScoreboard(statsBoard);
            Scoreboard playerBoard = event.getPlayer().getScoreboard();
            Objective playerObjective = playerBoard.getObjective("KDA");

            //get KDA object from hash map
            KDA playerKDA = Main.combatLogs.get(joinedPlayer.getUniqueId().toString());

            //load players kill, deaths, assists from kda object
            int playerKills = playerKDA.getKills();
            int playerDeaths = playerKDA.getDeaths();
            int playerAssists = playerKDA.getAssists();

            //update scoreboard with hash map values
            playerObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(playerKills);
            playerObjective.getScore(ChatColor.RED + "Deaths: ").setScore(playerDeaths);
            playerObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(playerAssists);
            System.out.println("Went into try statement");
        } catch (NullPointerException e) {
            //add score board to player
            Player joinedPlayer = (Player) event.getPlayer();
            joinedPlayer.setScoreboard(statsBoard);
            Scoreboard playerBoard = event.getPlayer().getScoreboard();
            Objective playerObjective = playerBoard.getObjective("KDA");

            //create new KDA object and add it to the hash map with key player uuid
            KDA newKDA = new KDA();
            String newPlayer = joinedPlayer.getUniqueId().toString();
            Main.combatLogs.put(newPlayer, newKDA);

            //load players kda from new KDA object
            int playerKills = newKDA.getKills();
            int playerDeaths = newKDA.getDeaths();
            int playerAssists = newKDA.getAssists();

            //add 0's to scoreboard as place values since the player was not in the hash map
            playerObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(playerKills);
            playerObjective.getScore(ChatColor.RED + "Deaths: ").setScore(playerDeaths);
            playerObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(playerAssists);
            System.out.println("Went into catch statement");
        }
        //TODO: add default values of 0 to the scoreboard so that its not blank before a player kills player
        //print the hashmap to view if the information was saved
    }

    //Handles kills and deaths of players
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {

        Player defender = event.getEntity();

        if (defender instanceof Player) {
            System.out.println("defender is a player.");
            if (defender.getKiller() instanceof Player) {
                System.out.println("attacker is a player");
                Player attacker = defender.getKiller();
                String defenderID = defender.getUniqueId().toString();
                String attackerID = attacker.getUniqueId().toString();
                //attacker
                if (Main.combatLogs.containsKey(attackerID)) {
                    System.out.println("hash map contains attacker.");

                    //increment kills in hash map
                    KDA attackerKDA = Main.combatLogs.get(attackerID);
                    attackerKDA.incrementKills();
                    Main.combatLogs.replace(attackerID, attackerKDA);

                    //update scoreboard
                    Scoreboard attackerBoard = attacker.getScoreboard();
                    Objective attackerObjective = attackerBoard.getObjective("KDA");

                    attackerObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(attackerKDA.getKills());
                    attackerObjective.getScore(ChatColor.RED + "Deaths: ").setScore(attackerKDA.getDeaths());
                    attackerObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(attackerKDA.getAssists());
                } else {
                    System.out.println("hash map does not contain attacker.");

                    //add killer to hash map for the first time
                    KDA newKillerKDA = new KDA();
                    newKillerKDA.incrementKills();
                    Main.combatLogs.put(attackerID, newKillerKDA);

                    //update scoreboard
                    Scoreboard attackerBoard = attacker.getScoreboard();
                    Objective attackerObjective = attackerBoard.getObjective("KDA");

                    attackerObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(newKillerKDA.getKills());
                    attackerObjective.getScore(ChatColor.RED + "Deaths: ").setScore(newKillerKDA.getDeaths());
                    attackerObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(newKillerKDA.getAssists());
                }
                //defender
                if (Main.combatLogs.containsKey(defenderID)) {
                    System.out.println("hash map contains defender.");
                    //increment deaths in hashmap
                    KDA defenderKDA = Main.combatLogs.get(defenderID);
                    defenderKDA.incrementDeaths();
                    Main.combatLogs.replace(defenderID, defenderKDA);

                    //update scoreboard
                    Scoreboard defenderBoard = defender.getScoreboard();
                    Objective defenderObjective = defenderBoard.getObjective("KDA");

                    defenderObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(defenderKDA.getKills());
                    defenderObjective.getScore(ChatColor.RED + "Deaths: ").setScore(defenderKDA.getDeaths());
                    defenderObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(defenderKDA.getAssists());

                } else {
                    System.out.println("hash map does not contain defender.");

                    //add defender to hash map for the first time
                    KDA newDefenderKDA = new KDA();
                    newDefenderKDA.incrementKills();
                    Main.combatLogs.put(defenderID, newDefenderKDA);

                    //update scoreboard
                    Scoreboard defenderBoard = defender.getScoreboard();
                    Objective defenderObjective = defenderBoard.getObjective("KDA");

                    defenderObjective.getScore(ChatColor.GREEN + "Kills: ").setScore(newDefenderKDA.getKills());
                    defenderObjective.getScore(ChatColor.RED + "Deaths: ").setScore(newDefenderKDA.getDeaths());
                    defenderObjective.getScore(ChatColor.YELLOW + "Assists: ").setScore(newDefenderKDA.getAssists());
                }
            } else {
                System.out.println(event.getEntity().getName() +  " died to non Player Entity.");
            }
        }
    }

    //TODO: Listener for Assists
}
