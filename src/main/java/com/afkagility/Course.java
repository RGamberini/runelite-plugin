package com.afkagility;

public class Course {
    public String name;
    public Obstacle[] obstacles;
    public int region;

    public Course(String name, Obstacle[] obstacles, int region) {
        this.name = name;
        this.obstacles = obstacles;
        this.region = region;
    }
}
