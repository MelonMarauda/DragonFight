package net.jagsnet.minecraft.plugins.dragonfight.commands;

import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Rewards;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks.SpawnTimer;
import net.jagsnet.minecraft.plugins.mlib.utils.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Player implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
            if (args.length < 1) {
                Utils.sendMessage(p, "--- Dragon plugin commands ---");
                Utils.sendMessage(p, "/dragon claim");
                Utils.sendMessage(p, "/dragon status");
                Utils.sendMessage(p, "/dragon help");
                return true;
            }

            if (args[0].equalsIgnoreCase("claim")) {
                Utils.load("Dragons");
                HashMap<UUID, String> pRewards = Rewards.playerRewards();
                if (pRewards == null) {
                    Utils.sendMessage(p, "You have no rewards to claim.");
                    return true;
                }
                if (pRewards.containsKey(p.getUniqueId())) {
                    String reward = pRewards.get(p.getUniqueId());
                    List<String> rewards = Utils.get().getStringList(reward);
                    int rewardCount = rewards.size();
                    boolean top = false;
                    if (reward.contains("1st") || reward.contains("2nd") || reward.contains("3rd")) {
                        rewardCount = rewardCount + Utils.get().getStringList(reward.split("\\.")[0] + ".participation").size();
                        top = true;
                    }
                    if (Inventories.space(p, rewardCount)) {
                        for (String s : rewards) {
                            if (s.contains("<player>")) {
                                s = s.replace("<player>", p.getName());
                            }
                            if (s.contains("<dragon>")) {
                                s = s.replace("<dragon>", reward.split("\\.")[0]);
                            }
                            int chance = Integer.parseInt(s.split(" ")[0]);
                            if (chance >= ThreadLocalRandom.current().nextInt(0, 100)) {
                                s = s.replace(s.split(" ")[0], "");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                            }
                        }
                        if (top) {
                            rewards = Utils.get().getStringList(reward.split("\\.")[0] + ".participation");
                            for (String s : rewards) {
                                if (s.contains("<player>")) {
                                    s = s.replace("<player>", p.getName());
                                }
                                if (s.contains("<dragon>")) {
                                    s = s.replace("<dragon>", reward.split("\\.")[0]);
                                }
                                int chance = Integer.parseInt(s.split(" ")[0]);
                                if (chance >= ThreadLocalRandom.current().nextInt(0, 100)) {
                                    s = s.replace(s.split(" ")[0], "");
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                                }
                            }
                        }
                        pRewards.remove(p.getUniqueId());
                        Rewards.setPlayerRewards(pRewards);
                    } else {
                        Utils.sendMessage(p, "Clear some inventory space and then use /dragon claim to claim your rewards!");
                    }
                } else {
                    Utils.sendMessage(p, "You have no rewards to claim.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("status")) {
                if (SpawnTimer.noDragon) {
                    Long cTime = SpawnTimer.cTime;
                    int hours = (int) Math.floor(cTime / 1000 / 60 / 60);
                    int minutes = (int) Math.floor((cTime - (hours * 1000 * 60 * 60)) / 1000 / 60);
                    int seconds = (int) Math.floor((cTime - (minutes * 1000 * 60) - (hours * 1000 * 60 * 60)) / 1000);
                    Utils.sendMessage(p, "The Dragon Will Respawn In "
                            + ((hours>0) ? hours + ((hours>1) ? " Hours, " : " Hour, " ) : "" )
                            + ((minutes>0) ? minutes + ((minutes>1) ? " Minutes And " : " Minute And " ) : "" )
                            + seconds + " Seconds!");
                    return true;
                }
                World world = Bukkit.getWorld("world_the_end");
                DragonBattle battle = world.getEnderDragonBattle();
                if (battle.getEnderDragon() != null) {
                    EnderDragon dragon = battle.getEnderDragon();
                    Utils.sendMessage(p, "The dragon " + dragon.getCustomName() + ChatColor.WHITE + " is alive! It has " + battle.getEnderDragon().getHealth() + " health left!");
                    return true;
                }
                if (battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE) {
                    Utils.sendMessage(p, "The dragon is respawning!");
                    return true;
                }
                Utils.sendMessage(p, "Somethings wrong? Contact your nearest admin =D");
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                Utils.sendMessage(p, "--- Dragon plugin commands ---");
                Utils.sendMessage(p, "/dragon claim");
                Utils.sendMessage(p, "/dragon status");
                Utils.sendMessage(p, "/dragon help");
                return true;
            }

            Utils.sendMessage(p, "Invalid dragons command.");
        }
        return true;
    }
}
