package net.jagsnet.minecraft.plugins.dragonfight.commands;

import net.jagsnet.minecraft.plugins.dragonfight.listeners.DragonSpawn;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.dragonfight.scheduledTasks.SpawnTimer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Admin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (!p.hasPermission("dragons.admin")) {
                Utils.sendMessage(p, "You are missing dragons admin permissions. Speak to Melon for more info");
                return true;
            }

            if (args.length < 1) {
                Utils.sendMessage(p, "v1");
                Utils.sendMessage(p, "/adragon spawn");
                Utils.sendMessage(p, "/adragon kill");
                Utils.sendMessage(p, "/adragon setTimer <timeInSeconds>");
                Utils.sendMessage(p, "/adragon setCountdownStatus [true|false]");
                Utils.sendMessage(p, "/adragon getCountdownStatus");
                Utils.sendMessage(p, "/adragon restartCountdown");
                Utils.sendMessage(p, "/adragon phase");
                Utils.sendMessage(p, "/adragon reload");
                Utils.sendMessage(p, "/adragon nextDragon <dragonName>");
                return true;
            }

            if (args[0].equalsIgnoreCase("spawn")) {
                SpawnTimer.setCountdownTime(Utils.getMain().getLong("respawnTime"));
                Utils.spawn();
                return true;
            }

            if (args[0].equalsIgnoreCase("kill")) {
                Utils.kill();
                return true;
            }

            if (args[0].equalsIgnoreCase("setTimer")) {
                long time = 7200;
                try {
                    time = Long.parseLong(args[1]);
                } catch (Exception ignore) {
                    Utils.sendMessage(p, "You failed to type a number... You suck");
                    return true;
                }
                SpawnTimer.setCountdownTime(time);
                return true;
            }

            if (args[0].equalsIgnoreCase("setCountdownStatus")) {
                boolean status = true;
                if (!args[1].equalsIgnoreCase("true")) status = false;
                SpawnTimer.setCountdownStatus(status);
                return true;
            }

            if (args[0].equalsIgnoreCase("getCountdownStatus")) {
                Utils.sendMessage(p,"Countdown status: " + SpawnTimer.countdown.toString());
                Utils.sendMessage(p, "Countdown time left: " + SpawnTimer.cTime);
                return true;
            }

            if (args[0].equalsIgnoreCase("restartCountdown")) {
                SpawnTimer.restartTimer();
                return true;
            }

            if (args[0].equalsIgnoreCase("phase")) {
                World world = Bukkit.getWorld("world_the_end");
                DragonBattle battle = world.getEnderDragonBattle();
                Utils.sendMessage(p, String.valueOf(battle.getRespawnPhase()));
                return true;
            }


            if (args[0].equalsIgnoreCase("reload")) {
                Utils.loadMain("Main");
                Utils.load("Dragons");
                return true;
            }

            if (args[0].equalsIgnoreCase("nextdragon")) {
                if (Utils.get().get(args[1]) == null) {
                    Utils.sendMessage(p, "Dragon doesn't exist");
                    return true;
                }
                DragonSpawn.setDragonName(args[1]);
                DragonSpawn.setSpecific(true);
                return true;
            }

            Utils.sendMessage(p, "Invalid aDragon command.");
        }
        return true;
    }
}