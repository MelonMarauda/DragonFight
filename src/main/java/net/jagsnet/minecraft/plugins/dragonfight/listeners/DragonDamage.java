package net.jagsnet.minecraft.plugins.dragonfight.listeners;

import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

public class DragonDamage implements Listener {
    public static HashMap<UUID, Double> pd;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!event.getEntity().getWorld().getName().equals("world_the_end")) return;
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)) return;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Player p = null;
            if (e.getDamager() instanceof Player) {
                p = (Player) e.getDamager();
            }
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    p = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Trident) {
                Trident trident = (Trident) e.getDamager();
                if (trident.getShooter() instanceof Player) {
                    p = (Player) trident.getShooter();
                }
            }
            if (p != null) {
                UUID pid = p.getUniqueId();
                if (pd == null) {
                    pd = new HashMap<UUID, Double>() {{
                        put(pid, e.getFinalDamage());
                    }};
                } else if (pd.containsKey(p.getUniqueId())) {
                    pd.replace(pid, pd.get(pid) + e.getFinalDamage());
                } else {
                    pd.put(pid, e.getFinalDamage());
                }
                String w = ChatColor.WHITE + "" + ChatColor.BOLD + "";
                String lp = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "";
                int dmg = (int) Math.round(pd.get(pid));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(w + "You have dealt roughly " + dmg + " damage to the " + lp + "Dragon" + w + " so far!"));
            }
        }
    }

    public static void resetDamage() {
        pd = null;
    }

    public static HashMap<UUID, Double> damageDealt(){
        return pd;
    }
}
