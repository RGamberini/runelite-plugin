package com.afkagility;

import com.google.gson.Gson;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class AFKAgilitySession {
    private static final Course[] courses;

    static {
        InputStream instr = AFKAgilitySession.class.getResourceAsStream("courses.json");
        assert instr != null;
        InputStreamReader strrd = new InputStreamReader(instr);
        courses = new Gson().fromJson(strrd, Course[].class);
    }

    private Course course = null;
    private int obstacleIndex;

    private Course getCourse(int region) {
        return Arrays.stream(courses)
                .filter(course -> course.region == region)
                .findFirst()
                .orElse(null);
    }

    public void startCourse(Client client) {
        int region = client.getLocalPlayer().getWorldLocation().getRegionID();
        course = getCourse(region);
    }


    private TileObject getObstacle(Tile tile, Obstacle obstacle) {
        return Stream.concat(
                        Arrays.stream((TileObject[]) tile.getGameObjects()),
                        Arrays.stream(new TileObject[]{
                                tile.getWallObject(),
                                tile.getGroundObject(),
                                tile.getDecorativeObject()}
                        ))
                .filter(Objects::nonNull)
                .filter(object -> object.getId() == obstacle.id)
                .findFirst()
                .orElse(null);
    }

    private WorldPoint findTile(Client client, Obstacle obstacle) {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        int z = client.getPlane();

        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];
                if (tile == null) continue;
                if (getObstacle(tile, obstacle) != null) {
                    System.out.printf("FOUND AT SCENE LOCATION (Z: %d, X: %d, Y: %d)%n", z, x, y);
                    return tile.getWorldLocation();
                }
            }
        }
        return null;
    }

    public TileObject getObject(Client client) {
        if (course == null && client.getPlane() == 0) {
            int region = client.getLocalPlayer().getWorldLocation().getRegionID();
            course = getCourse(region);
        }
        if (course == null) return null;

        Obstacle obstacle = course.obstacles[obstacleIndex];
        if (obstacle.location == null)
            obstacle.location = findTile(client, obstacle);

        if (obstacle.location == null) {
            throw new IllegalStateException(
                    String.format("Unable to find object %d (%s) in current scene.%n", obstacle.id, obstacle.name));
        }
        LocalPoint point = LocalPoint.fromWorld(client, obstacle.location);

        Tile tile = client.getScene().getTiles()[obstacle.location.getPlane()][point.getSceneX()][point.getSceneY()];
        return getObstacle(tile, obstacle);
    }

    public boolean isStarted() {
//        return course != null;
        return true;
    }

    public void nextObstacle() {
        obstacleIndex++;
    }

    public void endCourse() {
        obstacleIndex = 0;
    }
}
