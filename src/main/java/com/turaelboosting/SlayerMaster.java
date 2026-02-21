package com.turaelboosting;

import net.runelite.api.Point;

// TODO: Add co-ordinates to the enum for each master
public enum SlayerMaster {
    Krystilia(new Point(0, 0)),
    Mazchna(new Point(0, 0)),
    Vannaka(new Point(0, 0)),
    Chaeldar(new Point(0, 0)),
    Konar(new Point(0, 0)),
    Nieve(new Point(0, 0)),
    Duradel(new Point(0, 0));

    private final Point location;

    SlayerMaster(Point location) {
        this.location = location;
    }

    public Point location() {
        return location;
    }
}
