package net.jagsnet.minecraft.plugins.dragonfight.listeners;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Rewards;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks.DeleteRewards;
import net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks.SpawnTimer;
import net.jagsnet.minecraft.plugins.mlib.utils.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DragonDeath implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) {
            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Loading config");
            Utils.load("Dragons");
            HashMap<UUID, Double> pd = DragonDamage.damageDealt();
            Map.Entry<UUID, Double> one = null;
            Map.Entry<UUID, Double> two = null;
            Map.Entry<UUID, Double> thr = null;
            if (pd != null) {
                Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Calculating damage leaderboard");
                for (Map.Entry<UUID, Double> entry : pd.entrySet()) {
                    if (one == null || one.getValue() < entry.getValue()) {
                        one = entry;
                    }
                }
                if (pd.size() > 1) {
                    for (Map.Entry<UUID, Double> entry : pd.entrySet()) {
                        if ((two == null || two.getValue() < entry.getValue()) && !entry.equals(one)) {
                            two = entry;
                        }
                    }
                }
                if (pd.size() > 2) {
                    for (Map.Entry<UUID, Double> entry : pd.entrySet()) {
                        if ((thr == null || thr.getValue() < entry.getValue()) && !entry.equals(one) && !entry.equals(two)) {
                            thr = entry;
                        }
                    }
                }
                Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Sending damage leaderboard to players");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().getName().equals("world_the_end")) {
                        if (one != null) {
                            Utils.sendMessage(p, "Top contributors and their damage!");
                            if (Bukkit.getPlayer(one.getKey()) != null) {
                                Utils.sendMessage(p, "1st - " + Bukkit.getPlayer(one.getKey()).getName() + " - " + Math.round(one.getValue()));
                            } else {
                                Utils.sendMessage(p, "1st - " + Bukkit.getOfflinePlayer(one.getKey()).getName() + " - " + Math.round(one.getValue()));
                            }
                        }
                        if (two != null) {
                            if (Bukkit.getPlayer(two.getKey()) != null) {
                                Utils.sendMessage(p, "2nd - " + Bukkit.getPlayer(two.getKey()).getName() + " - " + Math.round(two.getValue()));
                            } else {
                                Utils.sendMessage(p, "2nd - " + Bukkit.getOfflinePlayer(two.getKey()).getName() + " - " + Math.round(two.getValue()));
                            }
                        }
                        if (thr != null) {
                            if (Bukkit.getPlayer(thr.getKey()) != null) {
                                Utils.sendMessage(p, "3rd - " + Bukkit.getPlayer(thr.getKey()).getName() + " - " + Math.round(thr.getValue()));
                            } else {
                                Utils.sendMessage(p, "3rd - " + Bukkit.getOfflinePlayer(thr.getKey()).getName() + " - " + Math.round(thr.getValue()));
                            }
                        }
                    }
                }

                Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Sending rewards");

                NamespacedKey key = new NamespacedKey(DragonFight.getInstance(), "dragonName");
                if (e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String dragon = e.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    int pRewardsLength = Utils.get().getStringList(dragon + ".participation").size();
                    if (one != null) {
                        Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Giving rewards to " + one.getKey().toString());
                        giveRewards(one, dragon, ".1st", pRewardsLength);
                    }
                    if (two != null) {
                        Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Giving rewards to " + two.getKey().toString());
                        giveRewards(two, dragon, ".2nd", pRewardsLength);
                    }
                    if (thr != null) {
                        Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Giving rewards to " + thr.getKey().toString());
                        giveRewards(thr, dragon, ".3rd", pRewardsLength);
                    }
                    Double health = Utils.get().getDouble(dragon + ".health");
                    Double threshold = Utils.get().getDouble(dragon + ".participationThreshold");
                    Double ht = health * threshold;
                    for (Map.Entry<UUID, Double> pid : pd.entrySet()) {
                        if (pd.get(pid.getKey()) >= ht && !(pid == one || pid == two || pid == thr)) {
                            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Giving rewards to " + pid.getKey().toString());
                            giveRewards(pid, dragon, ".participation", pRewardsLength);
                        }
                    }
                }
            }
            DragonDamage.resetDamage();
            SpawnTimer.restartTimer();
        }
    }

    void giveRewards(Map.Entry<UUID, Double> pid, String dragon, String reward, int pRewardsLength) {
        if (Bukkit.getPlayer(pid.getKey()) != null) {
            List<String> rewards = Utils.get().getStringList(dragon + reward);
            if (Inventories.space(Bukkit.getPlayer(pid.getKey()), rewards.size() + pRewardsLength)) {
                Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Commands path " + reward);
                for (String s : rewards) {
                    if (s.contains("<player>")) {
                        s = s.replace("<player>", Bukkit.getPlayer(pid.getKey()).getName());
                    }
                    if (s.contains("<dragon>")) {
                        s = s.replace("<dragon>", dragon);
                    }
                    int chance = Integer.parseInt(s.split(" ")[0]);
                    int random = ThreadLocalRandom.current().nextInt(0, 100);
                    Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Command chance " + chance + " | Random gen number " + random);
                    if (chance >= random) {
                        s = s.replace(s.split(" ")[0], "");
                        Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Executing command: " + s);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                    }
                }
                if (reward.contains("1st") || reward.contains("2nd") || reward.contains("3rd")) {
                    Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Commands path .participation");
                    rewards = Utils.get().getStringList(dragon + ".participation");
                    for (String s : rewards) {
                        if (s.contains("<player>")) {
                            s = s.replace("<player>", Bukkit.getPlayer(pid.getKey()).getName());
                        }
                        if (s.contains("<dragon>")) {
                            s = s.replace("<dragon>", dragon);
                        }
                        int chance = Integer.parseInt(s.split(" ")[0]);
                        int random = ThreadLocalRandom.current().nextInt(0, 100);
                        Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Command chance " + chance + " | Random gen number " + random);
                        if (chance >= random) {
                            s = s.replace(s.split(" ")[0], "");
                            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Executing command: " + s);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                        }
                    }
                }
            } else {
                Utils.sendMessage(Bukkit.getPlayer(pid.getKey()), "Clear some inventory space and then use /dragon claim to claim your rewards!");
                Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Inventory full, sending rewards to storage");
                setRewards(pid, dragon, reward);
                DeleteRewards.yeet(pid.getKey());
            }
        } else {
            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Player offline, sending rewards to storage");
            setRewards(pid, dragon, reward);
            DeleteRewards.yeet(pid.getKey());
        }
    }

    void setRewards(Map.Entry<UUID, Double> pid, String dragon, String reward) {
        if (Rewards.playerRewards() == null) {
            Map.Entry<UUID, Double> finalOne = pid;
            Rewards.setPlayerRewards(new HashMap<UUID, String>(){{put(finalOne.getKey(), dragon + reward);}});
        } else {
            HashMap<UUID, String> temp = Rewards.playerRewards();
            temp.put(pid.getKey(), dragon + reward);
            Rewards.setPlayerRewards(temp);
        }
    }
}
