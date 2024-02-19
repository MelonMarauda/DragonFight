package net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Rewards;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class DeleteRewards {
    public static void yeet(UUID pid){
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Rewards.playerRewards().containsKey(pid)) {
                    HashMap<UUID, String> pr = Rewards.playerRewards();
                    pr.remove(pid);
                    Rewards.setPlayerRewards(pr);
                }
            }
        }, 36000L);
    }
}
