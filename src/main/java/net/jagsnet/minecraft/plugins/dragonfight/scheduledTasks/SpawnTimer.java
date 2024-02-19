package net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

import static org.bukkit.Bukkit.getServer;

public class SpawnTimer {
    public static Boolean countdown = true;
    public static Boolean noDragon = true;
    public static long cTime = 7200;
    public static long pastTime = 7200;
    public static World world = Bukkit.getWorld("world_the_end");

    public static void time(long time){
        cTime = time;
        pastTime = System.currentTimeMillis();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (world.getEntitiesByClass(EnderDragon.class).size() == 0 && noDragon == false && world.getEnderDragonBattle().getRespawnPhase() == DragonBattle.RespawnPhase.NONE) {
                    noDragon = true;
                } else if (world.getEntitiesByClass(EnderDragon.class).size() != 0 || world.getEnderDragonBattle().getRespawnPhase() != DragonBattle.RespawnPhase.NONE) {
                    noDragon = false;
                }
                if (countdown && noDragon) {
                    Collection<EnderDragon> dragons = world.getEntitiesByClass(EnderDragon.class);
                    if (dragons.size() != 0) {
                        World world = Bukkit.getWorld("world_the_end");
                        DragonBattle battle = world.getEnderDragonBattle();
                        if (battle.getEnderDragon().getHealth() > 1) {
                            noDragon = false;
                        }
                    }
                }
                if (cTime <= 0 && countdown && noDragon) {
                    Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Timer ran out, spawning dragon");
                    noDragon = false;
                    Utils.loadMain("Main");
                    cTime = Utils.getMain().getInt("respawnTime");
                    Utils.spawn();
                }
                if (countdown && noDragon) {
                    cTime = cTime - (System.currentTimeMillis() - pastTime);

                    int hours = (int) Math.floor(cTime / 1000 / 60 / 60);
                    int minutes = (int) Math.floor((cTime - (hours * 1000 * 60 * 60)) / 1000 / 60);
                    int seconds = (int) Math.floor((cTime - (minutes * 1000 * 60) - (hours * 1000 * 60 * 60)) / 1000);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getWorld().getName().equals("world_the_end")) {
                            String w = ChatColor.WHITE + "" + ChatColor.BOLD + "";
                            String lp = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "";
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(w + "The " + lp + "Dragon" + w + " Will Spawn In "
                                    + ((hours>0) ? hours + ((hours>1) ? " Hours, " : " Hour, " ) : "" )
                                    + ((minutes>0) ? minutes + ((minutes>1) ? " Minutes And " : " Minute And " ) : "" )
                                    + seconds + " Seconds!"));
                        }
                    }
                }
                pastTime = System.currentTimeMillis();
            }
        }, 0L, 20L);
    }

    public static void setCountdownStatus(Boolean tf) {
        countdown = tf;
    }

    public static void setCountdownTime(long time) {
        cTime = time;
    }

    public static void restartTimer() {
        Utils.loadMain("Main");
        cTime = Utils.getMain().getInt("respawnTime");
        noDragon = true;
    }
}
