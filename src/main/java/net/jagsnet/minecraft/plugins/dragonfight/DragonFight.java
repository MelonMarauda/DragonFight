package net.jagsnet.minecraft.plugins.dragonfight;

import net.jagsnet.minecraft.plugins.dragonfight.commands.Admin;
import net.jagsnet.minecraft.plugins.dragonfight.commands.Player;
import net.jagsnet.minecraft.plugins.dragonfight.listeners.*;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks.SpawnTimer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

import static org.bukkit.Bukkit.getServer;

public final class DragonFight extends JavaPlugin {
    private static DragonFight instance;

    public Long ct = System.currentTimeMillis();
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        this.getCommand("adragon").setExecutor(new Admin());
        this.getCommand("dragon").setExecutor(new Player());

        getServer().getPluginManager().registerEvents(new DragonDeath(), this);
        getServer().getPluginManager().registerEvents(new DragonDamage(), this);
        getServer().getPluginManager().registerEvents(new DragonSpawn(), this);
        getServer().getPluginManager().registerEvents(new DragonPhaseChange(), this);
        getServer().getPluginManager().registerEvents(new DragonAgro(), this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Utils.kill();
            }
        }, 100L);

        scheduler.scheduleSyncRepeatingTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    World world = Bukkit.getWorld("world_the_end");
                    DragonBattle battle = world.getEnderDragonBattle();
                    EnderDragon ed = battle.getEnderDragon();
                    if (ed.getPhase() == EnderDragon.Phase.CIRCLING) {
                        if ((System.currentTimeMillis() - ct) > 60000) {
                            ed.setPhase(EnderDragon.Phase.FLY_TO_PORTAL);
                            Bukkit.getPlayer("MelonMarauda").sendMessage("Forcing dragon to land");
                        }
                    } else {
                        ct = System.currentTimeMillis();
                    }
                } catch (Exception e) {}
            }
        }, 100L, 100L);

        Utils.setupMain("Main");
        Utils.loadMain("Main");
        if (Utils.getMain().get("respawnTime") == null) {
            System.out.println("Dragonfight requires further configuration!!!");
            System.out.println("Dragonfight requires further configuration!!!");
            System.out.println("Dragonfight requires further configuration!!!");
            System.out.println("Dragonfight requires further configuration!!!");
            System.out.println("Dragonfight requires further configuration!!!");
        } else {
            SpawnTimer.time(Utils.getMain().getLong("respawnTime"));
        }
        Utils.load("Dragons");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DragonFight getInstance() {return instance;}
}
