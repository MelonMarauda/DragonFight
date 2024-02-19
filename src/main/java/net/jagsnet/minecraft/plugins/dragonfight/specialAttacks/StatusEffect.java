package net.jagsnet.minecraft.plugins.dragonfight.specialAttacks;

import org.bukkit.Bukkit;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StatusEffect {
    public static void statusEffect(DragonBattle battle, int time, int strength, String effectName) {
        EnderDragon dragon = battle.getEnderDragon();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.getLocation().distance(dragon.getLocation()) < 200) && (p.getWorld() == dragon.getWorld())) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectName), time, strength));
            }
        }
    }
}
