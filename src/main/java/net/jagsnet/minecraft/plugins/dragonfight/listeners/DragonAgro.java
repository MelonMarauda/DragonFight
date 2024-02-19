package net.jagsnet.minecraft.plugins.dragonfight.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class DragonAgro implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAgro(EntityTargetEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world_the_end")) return;
        if (e.getTarget() == null) return;
        if (e.getTarget().getType().equals(EntityType.ENDER_DRAGON)) e.setTarget(null);
    }
}
