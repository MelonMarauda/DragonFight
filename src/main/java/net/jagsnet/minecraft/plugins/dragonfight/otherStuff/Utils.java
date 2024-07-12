package net.jagsnet.minecraft.plugins.dragonfight.otherStuff;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.mlib.utils.Messaging;
import org.bukkit.*;
import org.bukkit.boss.DragonBattle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.bukkit.Bukkit.getServer;

public class Utils {
    // ------------- Send Formatted Message To Player ------------------
    public static void sendMessage(Player player, String msg){
        Messaging.sendMessage(player, msg, ChatColor.LIGHT_PURPLE, "DRAGONS");
    }

    // ------------- Config functions =D ------------------
    public static void setup(String configName){
        File file;
        FileConfiguration customFile;
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("DragonFight").getDataFolder(), configName + ".yml");
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }
    private static File file2;
    private static FileConfiguration customFile2;
    public static FileConfiguration get(){
        return customFile2;
    }
    public static void load(String name){
        file2 = new File(Bukkit.getServer().getPluginManager().getPlugin("DragonFight").getDataFolder(), name + ".yml");
        customFile2 = YamlConfiguration.loadConfiguration(file2);
    }
    public static void save(){
        try{
            customFile2.save(file2);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static void setupMain(String configName){
        File file;
        FileConfiguration customFile;
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("DragonFight").getDataFolder(), configName + ".yml");
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }
    private static File file2Main;
    private static FileConfiguration customFile2Main;
    public static FileConfiguration getMain(){
        return customFile2Main;
    }
    public static void loadMain(String name){
        file2Main = new File(Bukkit.getServer().getPluginManager().getPlugin("DragonFight").getDataFolder(), name + ".yml");
        customFile2Main = YamlConfiguration.loadConfiguration(file2Main);
    }
    public static void saveMain(){
        try{
            customFile2Main.save(file2Main);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    // ------------- Spawn Dragon ------------------
    public static void spawn() {
        Bukkit.getLogger().info(" [" + Utils.class.getName() + "] Spawning end crystals to initiate respawn");
        World world = Bukkit.getWorld("world_the_end");

        if (!world.getName().equals("world_the_end")) return;

        DragonBattle battle = world.getEnderDragonBattle();
        Location portal = battle.getEndPortalLocation();
        world.getBlockAt((int) portal.getX(), (int) portal.getY() + 4, (int) portal.getZ()).setType(Material.DRAGON_EGG);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity crystal = world.spawnEntity(new Location(world, portal.getX() + 3.5, portal.getY() + 1, portal.getZ() + 0.5), EntityType.ENDER_CRYSTAL);
                crystal.setInvulnerable(true);
            }
        }, 20L);
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity crystal = world.spawnEntity(new Location(world, portal.getX() + 0.5, portal.getY() + 1, portal.getZ() - 2.5), EntityType.ENDER_CRYSTAL);
                crystal.setInvulnerable(true);
            }
        }, 40L);
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity crystal = world.spawnEntity(new Location(world, portal.getX() - 2.5, portal.getY() + 1, portal.getZ() + 0.5), EntityType.ENDER_CRYSTAL);
                crystal.setInvulnerable(true);
            }
        }, 60L);
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity crystal = world.spawnEntity(new Location(world, portal.getX() + 0.5, portal.getY() + 1, portal.getZ() + 3.5), EntityType.ENDER_CRYSTAL);
                crystal.setInvulnerable(true);
            }
        }, 80L);
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                battle.initiateRespawn();

                Bukkit.getLogger().info(" [" + Utils.class.getName() + "] Initiated respawn and letting everyone know");
            }
        }, 100L);
        scheduler.scheduleSyncDelayedTask(DragonFight.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (Utils.getMain().getString("global").equalsIgnoreCase("true")) {
                        p.sendTitle("Its Here!", "The Ender Dragon Is Respawning!", 40, 80, 40);
                    } else if (p.getWorld().getName().equals("world_the_end")) {
                        p.sendTitle("Its Here!", "The Ender Dragon Is Respawning!", 40, 80, 40);
                    }
                }
            }
        }, 120L);
    }

    // ------------- Kill Dragon ------------------
    public static void kill() {
        World world = Bukkit.getWorld("world_the_end");

        if (!world.getName().equals("world_the_end")) return;

        Collection<EnderDragon> dragons =  world.getEntitiesByClass(EnderDragon.class);
        for (Entity entity : dragons) {
            EnderDragon dragon = (EnderDragon) entity;
            dragon.setHealth(0);
        }
    }
}
