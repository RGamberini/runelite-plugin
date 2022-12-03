package com.afkagility;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


import javax.inject.Inject;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "AFK Agility"
)
public class AFKAgilityPlugin extends Plugin {
    private AFKAgilitySession afkAgilitySession = new AFKAgilitySession();
    public TileObject obstacle = null;

    @Inject
    private Client client;

    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AFKAgilityOverlay overlay;

    private final int[][] directions = {
            new int[]{0, 1},
            new int[]{0, -1},
            new int[]{1, 0},
            new int[]{-1, 0},

    };

    private TileObject containsMarkOfGrace(Client client, WorldPoint wp) {
        LocalPoint lp = LocalPoint.fromWorld(client, wp);
        Tile tile = client.getScene().getTiles()[wp.getPlane()][lp.getSceneX()][lp.getSceneY()];
        if (tile == null) return null;
        if (tile.getGroundItems().stream().anyMatch(item -> item.getId() == 11849)) tile.getGroundObject();
        return null;
    }
    private TileObject flood(Client client, WorldPoint wp, Map<WorldPoint, Boolean> visited) {
        visited.put(wp, true);
        if (containsMarkOfGrace(client, wp) != null) return containsMarkOfGrace(client, wp);
        for (int[] direction: directions) {
            WorldPoint nextLoc = new WorldPoint(
                    wp.getX() + direction[0],
                    wp.getY()  + direction[1],
                    wp.getPlane()
            );

            if (!visited.containsKey(nextLoc) &&
                    new WorldArea(wp, 1,1)
                        .canTravelInDirection(client, direction[0], direction[1])) {
                if (flood(client, nextLoc, visited) != null) {
                    System.out.println("FOUND MARK OF GRACE");
                    return flood(client, nextLoc, visited);
                }
            }
        }
        return null;
    }
    private TileObject getMarkOfGrace(Client client) {
        WorldPoint start = client.getLocalPlayer().getWorldLocation();
        return flood(client, start, new HashMap<>());
    }

    @Override
    protected void startUp() throws IllegalAccessException {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        obstacle = null;
        overlayManager.remove(overlay);
        afkAgilitySession = new AFKAgilitySession();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen()) return;
        obstacle = afkAgilitySession.getObject(client);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        switch (scriptPreFired.getScriptId()) {
            case 5173:
                afkAgilitySession.startCourse(client);
                notifier.notify("Course started!");
                break;
            case 5174:
                afkAgilitySession.endCourse();
                notifier.notify("Course ended!");
                break;
            case 5175:
                afkAgilitySession.nextObstacle();
                notifier.notify("Click on next obstacle");
                break;
        }
    }
}
