package com.afkagility;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;
import java.util.List;

public class AFKAgilityOverlay extends Overlay {
    private final AFKAgilityPlugin plugin;

    @Inject
    private AFKAgilityOverlay(AFKAgilityPlugin plugin)
    {
        System.out.println("AFK AGILITY OVERLAY CREATED");
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.obstacle == null) return null;
        if (plugin.obstacle.getClickbox() == null) return null;
        OverlayUtil.renderPolygon(graphics, plugin.obstacle.getClickbox().getBounds(), Color.MAGENTA);
        return null;
    }
}
