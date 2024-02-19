package net.jagsnet.minecraft.plugins.dragonfight.otherStuff;

import java.util.HashMap;
import java.util.UUID;

public class Rewards {
    public static HashMap<UUID, String> pr = null;

    public static HashMap<UUID, String> playerRewards() {
        return pr;
    }

    public static void setPlayerRewards(HashMap<UUID, String> hash) {
        pr = hash;
    }
}
