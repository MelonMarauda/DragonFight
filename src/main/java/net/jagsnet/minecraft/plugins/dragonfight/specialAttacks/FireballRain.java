package net.jagsnet.minecraft.plugins.dragonfight.specialAttacks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Bukkit.getServer;

public class FireballRain {
    public static void rain(DragonBattle battle, int count, int delay){
        int d = 0;
        while (count > 0) {
            d = d + delay;
            drops(d, battle);
            count--;
        }
    }

    static void drops(int delay, DragonBattle battle) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location center = battle.getEndPortalLocation();
                center.setX(center.getX() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setZ(center.getZ() + ThreadLocalRandom.current().nextInt(-50, 50));
                center.setY(254);
                DragonFireball fireball = (DragonFireball) center.getWorld().spawnEntity(center, EntityType.DRAGON_FIREBALL);
                fireball.setDirection(new Vector(0, -90, 0));
                fireball.setVelocity(new Vector(0, -10, 0));
                fireball.setGravity(true);
            }
        }, delay);
    }
}
