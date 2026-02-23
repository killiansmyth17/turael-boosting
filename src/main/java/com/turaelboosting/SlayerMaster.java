package com.turaelboosting;

import net.runelite.api.coords.WorldPoint;

// TODO: Add co-ordinates to the enum for each master
public enum SlayerMaster {
    Krystilia(new WorldPoint(3109, 3515, 0)),
    Mazchna(new WorldPoint(3512, 3510, 0)),
    Vannaka(new WorldPoint(3145, 9914, 0)),
    Chaeldar(new WorldPoint(2445, 4432, 0)),
    Konar(new WorldPoint(1309, 3785, 0)),
    Nieve(new WorldPoint(2432, 3423, 0)),
    Duradel(new WorldPoint(2869, 2982, 1));

    private final WorldPoint location;

    SlayerMaster(WorldPoint location) {
        this.location = location;
    }

    public WorldPoint location() {
        return location;
    }
}
