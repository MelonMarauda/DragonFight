package net.jagsnet.minecraft.plugins.dragonfight.specialAttacks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Bukkit.getServer;

public class TeleportPlayers {
    public static void tele(DragonBattle battle, int count, int delay) {
        EnderDragon dragon = battle.getEnderDragon();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.getLocation().distance(dragon.getLocation()) < 200) && (p.getWorld() == dragon.getWorld())) {
                foreach(battle, count, delay, p);
            }
        }
    }

    static void foreach(DragonBattle battle, int count, int delay, Player p) {
        int d = 0;
        while (count > 0) {
            d = d + delay;
            spawn(d, battle, p);
            count--;
        }
    }

    static void spawn(int delay, DragonBattle battle, Player p) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location center = battle.getEndPortalLocation();
                center.setX(center.getX() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setZ(center.getZ() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setY(254);
                while (center.getY() > 1) {
                    if (center.getBlock().getType() != Material.AIR) {
                        center.add(0, 1, 0);
                        p.teleport(center);
                        break;
                    }
                    center.add(0, -1, 0);
                }
            }
        }, delay);
    }
}
