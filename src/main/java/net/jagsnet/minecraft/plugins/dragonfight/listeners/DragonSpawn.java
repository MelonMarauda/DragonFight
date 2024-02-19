package net.jagsnet.minecraft.plugins.dragonfight.listeners;

import net.jagsnet.minecraft.plugins.dragonfight.DragonFight;
import net.jagsnet.minecraft.plugins.dragonfight.otherStuff.Utils;
import net.jagsnet.minecraft.plugins.mlib.utils.Strings;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DragonSpawn implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) {

            World world = Bukkit.getWorld("world_the_end");
            DragonBattle battle = world.getEnderDragonBattle();
            Location portal = battle.getEndPortalLocation();
            EnderDragon ed = (EnderDragon) e.getEntity();
            if (ed.getWorld() != world) return;
            if (ed.getLocation().getX() != portal.getX()) return;
            if (ed.getLocation().getZ() != portal.getZ()) return;
            if (ed.hasMetadata("NPC")) return;

            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Loading config");
            Utils.load("Dragons");
            String dragon;

            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Checking for specific dragon to spawn");
            if (specific) {
                dragon = dragonName;
            } else {
                Object[] dragons = Utils.get().getKeys(false).toArray();
                int randomNum = ThreadLocalRandom.current().nextInt(0, dragons.length);
                dragon = dragons[randomNum].toString();
            }
            setSpecific(false);

            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Setting attributes");

            ed.setCustomName(Strings.colourIt(Utils.get().getString(dragon + ".name")));
            ed.setCustomNameVisible(true);
            ed.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Utils.get().getDouble(dragon + ".health"));
            ed.setHealth(Utils.get().getDouble(dragon + ".health"));
            if (Utils.get().getString(dragon + ".glow").equalsIgnoreCase("true")) ed.setGlowing(true);
            ed.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(Utils.get().getDouble(dragon + ".armour"));

            NamespacedKey key = new NamespacedKey(DragonFight.getInstance(), "dragonName");
            ed.getPersistentDataContainer().set(key, PersistentDataType.STRING, dragon);

            battle.getBossBar().setColor(BarColor.valueOf(Utils.get().getString(dragon + ".barColour")));
            Bukkit.getLogger().info(" [" + this.getClass().getName() + "] Finished spawning");

            Collection<Entity> es = world.getNearbyEntities(portal, 10, 10, 10);

            for (Entity entity: es) {

                if (entity instanceof EnderCrystal) {
                    EnderCrystal ec = (EnderCrystal) entity;

                    ec.setInvulnerable(false);
                    ec.setPersistent(false);
                    ec.remove();
                }
            }
        }
    }

    private static boolean specific = false;
    public static void setSpecific(boolean b) {
        specific = b;
    }

    private static String dragonName;
    public static void setDragonName(String s) {
        dragonName = s;
    }
}
