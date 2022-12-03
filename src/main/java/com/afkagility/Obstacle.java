package com.afkagility;

import net.runelite.api.coords.WorldPoint;

public class Obstacle {
    public String name;
    public int id;
    public WorldPoint location;

    public Obstacle(String name, int id, WorldPoint location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }
}
