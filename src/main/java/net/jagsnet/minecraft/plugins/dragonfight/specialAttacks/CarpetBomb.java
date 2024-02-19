package net.jagsnet.minecraft.plugins.dragonfight.specialAttacks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Bukkit.getServer;

public class CarpetBomb {
    public static void bomb(DragonBattle battle, int count, int delay, int cluster, String entity) {
        int d = 0;
        while (count > 0) {
            d = d + delay;
            spawn(cluster, d, battle, entity);
            count--;
        }
    }

    static void spawn(int cluster, int delay, DragonBattle battle, String entity) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location center = battle.getEnderDragon().getLocation();
                int stop = 0;
                while (stop < cluster) {
                    center.add(0, 1, 0);
                    Entity spawnedEntity = center.getWorld().spawnEntity(center, EntityType.valueOf(entity));

                    BukkitScheduler scheduler = getServer().getScheduler();
                    scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (spawnedEntity instanceof Monster) {
                                if (!spawnedEntity.isDead()){
                                    Monster m = (Monster) spawnedEntity;
                                    m.setHealth(0);
                                }
                            }
                        }
                    }, 6000);
                    stop++;
                }
            }
        }, delay);
    }
}
