package net.jagsnet.minecraft.plugins.dragonfight.listeners;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.dragonfight.specialAttacks.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class DragonPhaseChange implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDragonPhaseChange(EnderDragonChangePhaseEvent e) {
        if (e.getEntity().getWorld().getName().equalsIgnoreCase("world_the_end")) {
//            Bukkit.getPlayer("MelonMarauda").sendMessage(e.getCurrentPhase().toString() + " -> " + e.getNewPhase().toString());
            NamespacedKey key = new NamespacedKey(DragonFight.getInstance(), "dragonName");
            if (e.getEntity().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                DragonBattle battle = e.getEntity().getWorld().getEnderDragonBattle();
                if (!playerNear(battle)) {
                    return;
                }
                String dragon = e.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                String phase = e.getNewPhase().toString();
                String test = dragon + ".special." + phase;
                Utils.load("Dragons");
                if (!Utils.get().getString(test + ".name").equalsIgnoreCase("false")) {
                    int chance = Utils.get().getInt(dragon + ".special." + phase + ".chance");
                    if (chance >= ThreadLocalRandom.current().nextInt(0, 100)) {
                        switch (Utils.get().getString(test + ".name").toLowerCase()) {
                            case "lightning":
                                Lightning.boom(battle, Utils.get().getInt(test + ".count"), Utils.get().getInt(test + ".delay"));
                                break;
                            case "spawn":
                                SpawnMobs.spawnMobs(battle, Utils.get().getInt(test + ".count"), Utils.get().getInt(test + ".delay"), Utils.get().getInt(test + ".cluster"), Utils.get().getString(test + ".entity"));
                                break;
                            case "bomb":
                                CarpetBomb.bomb(battle, Utils.get().getInt(test + ".count"), Utils.get().getInt(test + ".delay"), Utils.get().getInt(test + ".cluster"), Utils.get().getString(test + ".entity"));
                                break;
                            case "fireballrain":
                                FireballRain.rain(battle, Utils.get().getInt(test + ".count"), Utils.get().getInt(test + ".delay"));
                                break;
                            case "statuseffect":
                                StatusEffect.statusEffect(battle, Utils.get().getInt(test + ".time"), Utils.get().getInt(test + ".strength"), Utils.get().getString(test + ".effect"));
                                break;
                            case "respawncrystals":
                                RespawnCrystals.respawn(battle);
                                break;
                            case "teleportplayers":
                                TeleportPlayers.tele(battle, Utils.get().getInt(test + ".count"), Utils.get().getInt(test + ".delay"));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    boolean playerNear(DragonBattle battle){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getName().equals("world_the_end")) {
                EnderDragon dragon = battle.getEnderDragon();
                if ((p.getLocation().distance(dragon.getLocation()) < 200) && (p.getWorld() == dragon.getWorld())) {
                    return true;
                }
            }
        }
        return false;
    }
}
