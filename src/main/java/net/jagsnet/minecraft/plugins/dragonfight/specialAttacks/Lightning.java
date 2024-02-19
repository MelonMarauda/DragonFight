package net.jagsnet.minecraft.plugins.dragonfight.specialAttacks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Bukkit.getServer;

public class Lightning {
    public static void boom(DragonBattle battle, int count, int delay){
        int d = 0;
        while (count > 0) {
            d = d + delay;
            strikes(d, battle);
            count--;
        }
    }

    static void strikes(int delay, DragonBattle battle) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location center = battle.getEndPortalLocation();
                center.setX(center.getX() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setZ(center.getZ() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setY(254);
                int stop = 0;
                while (center.getY() > 1 && stop < 4) {
                    if (center.getBlock().getType() != Material.AIR) {
                        center.getWorld().strikeLightning(center);
                        stop++;
                    }
                    center.add(0, -1, 0);
                }
            }
        }, delay);
    }
}
