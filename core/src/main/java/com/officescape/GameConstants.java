package com.officescape;

public class GameConstants {
    // Player
    public static final float PLAYER_DEFAULT_SPEED = 300f;
    public static final float PLAYER_DEFAULT_SCALE = 0.1f;
    public static final float PLAYER_COLLISION_RATIO = 0.8f;
    public static final int PLAYER_START_X = 130;
    public static final int PLAYER_START_Y = 190;

    // Pathfinding
    public static final float PATH_REACH_THRESHOLD = 3f;
    public static final float NODE_SEARCH_RADIUS = 5f;
    public static final float IS_WALL = 0.8f;

    // Map
    public static final String MAP_FILE_PATH = "map.tmx";
    public static final String COLLISION_LAYER_NAME = "walls";

    // Player
    public static final String PLAYER_FILE_PATH = "player.png";

    // Graph
    public static final int TILED_GRAPH_WIDTH = 120;
    public static final int TILED_GRAPH_HEIGHT = 120;
    public static final float TILED_SIZE = 16f;

    public static final float STEP_THRESHOLD = 1f;
}

