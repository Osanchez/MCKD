package me.Alexisblack.MCKD;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.EventHandler;

public class CombatListener implements Listener {
    private Scoreboard statsBoard;
    private Objective stats;

    public void checkHashMapKDA() {
        //iterate hash map to view values being added
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

    public void checkHashMapAssists() {
        //iterate hash map to view values being added
        Iterator it = Main.assistLogs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("Killed UUID: " + pair.getKey());
            ArrayList<Player> assists = (ArrayList<Player>) pair.getValue();
            int x = 1;
            for(Player assist: assists) {
                System.out.println("Assist Player " + x + ": " + assist.getName());
                x++;
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void creditPlayerAssists(String killer, String defender) {
        if(Main.assistLogs.containsKey(defender)) {
            System.out.println("The assist log contains the killed player.");
            ArrayList<Player> playerAssists = Main.assistLogs.get(defender);
            for (Player assistPlayer : playerAssists) {
                if (!assistPlayer.getUniqueId().toString().equals(killer)) {
                    String assistPlayerID = assistPlayer.getUniqueId().toString();
                    if (Main.combatLogs.containsKey(assistPlayerID)) {
                        KDA playerKDA = Main.combatLogs.get(assistPlayerID);
                        playerKDA.incrementAssists();
                        Main.combatLogs.replace(assistPlayerID, playerKDA);
                    } else {
                        KDA newPlayerKDA = new KDA();
                        newPlayerKDA.incrementAssists();
                        Main.combatLogs.put(assistPlayerID, newPlayerKDA);
                    }
                } else {
                    System.out.println("made sure the killer didn't receive assist credit as well as kill credit.");
                }
            }
        } else {
            System.out.println("Killer UUID: " + killer);
            System.out.println("Defender UUID: " + defender);
            System.out.println("------ The Hash Map ------\n");
            checkHashMapAssists();
            System.out.println("The assist log doesn't contain the killed player.");
        }

    }



    //display any important information to the player
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //initialize the score board
        statsBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        //Scoreboard configuration
        stats = statsBoard.registerNewObjective("KDA", "dummy");
        stats.setDisplayName("KDA");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);

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
        }
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
                    //credit all players with assists
                    creditPlayerAssists(attackerID, defenderID);
                    Main.assistLogs.remove(defenderID);

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
                    //credit all players with assists
                    creditPlayerAssists(attackerID, defenderID);
                    Main.assistLogs.remove(defenderID);

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

                    //add defender to hash map for the first time
                    KDA newDefenderKDA = new KDA();
                    newDefenderKDA.incrementKills();
                    Main.combatLogs.put(defenderID, newDefenderKDA);

                    //update scoreboardd
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

    // TODO: update assists, then remove that player from the hash map until he gets damaged again

    @EventHandler
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            String damagedPlayerID = damagedPlayer.getUniqueId().toString();
            if(event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();
                String attackerID = attacker.getUniqueId().toString();
                if(Main.assistLogs.containsKey(damagedPlayerID)) {
                    ArrayList<Player> allAssists = Main.assistLogs.get(damagedPlayerID);
                    boolean alreadyAssisted = false;
                    for(int x = 0; x < allAssists.size(); x++) {
                        String playerAssister = allAssists.get(x).getUniqueId().toString();
                        if(playerAssister.equals(attackerID)) {
                            alreadyAssisted = true;
                        }
                    }
                    if(alreadyAssisted == false) {
                        allAssists.add(attacker);
                        Main.assistLogs.replace(damagedPlayerID, allAssists);
                        checkHashMapAssists();
                    }
                } else {
                    ArrayList<Player> newAssistPlayers = new ArrayList<>();
                    newAssistPlayers.add(attacker);
                    Main.assistLogs.put(damagedPlayerID, newAssistPlayers);
                    System.out.println("New Attacker Added to the assist log!");
                }
            }
        }
    }
}
