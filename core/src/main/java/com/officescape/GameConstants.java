package com.officescape;

public class GameConstants {
    // Player
    public static final float PLAYER_DEFAULT_SPEED = 200f;
    public static final float PLAYER_DEFAULT_SCALE = 1f;
    public static final float PLAYER_COLLISION_RATIO = 0.4f;
    public static final int PLAYER_START_X = 40;
    public static final int PLAYER_START_Y = 650;
    public static final float MAX_PLAYER_WIDTH = 28.7f;
    public static final float MAX_PLAYER_HEIGHT = 28.7f;
    public static final float STEP_DURATION = 0.2f;


    // Pathfinding
    public static final float PATH_REACH_THRESHOLD = 1f;
    public static final float NODE_SEARCH_RADIUS = 8f;
    public static final float IS_WALL = 0.3f;

    // Map
    public static final String MAP_FILE_PATH = "map.tmx";
    public static final String COLLISION_LAYER_NAME = "walls";

    // Graph
    public static final int TILED_GRAPH_WIDTH = 80;
    public static final int TILED_GRAPH_HEIGHT = 45;
    public static final float TILED_SIZE = 16f;

    // textures
    public static final String PLAYER_FILE_PATH = "player_skin/player_idle.png";
    public static final String BABKA_FILE_PATH = "player_skin/npc_idle.png";
    public static final String BOSS_FILE_PATH = "player_skin/npc_idle.png";
    public static final String CAT_FILE_PATH = "player_skin/npc_idle.png";
    public static final String COLLEAGUE_FILE_PATH = "player_skin/npc_idle.png";
    public static final String ITSHNIK_FILE_PATH = "player_skin/npc_idle.png";


}

