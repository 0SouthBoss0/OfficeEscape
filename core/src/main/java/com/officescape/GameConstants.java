package com.officescape;

import com.officescape.unit.Character;

public class GameConstants {
    // Player
    public static final float PLAYER_DEFAULT_SPEED = 200f;
    public static final float PLAYER_DEFAULT_SCALE = 1f;
    public static final float PLAYER_COLLISION_RATIO = 0.4f;
    public static final int PLAYER_START_X = 50;
    public static final int PLAYER_START_Y = 660;
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
    public static final String BABKA_FILE_PATH = "player_skin/babka_idle.png";
    public static final String BOSS_FILE_PATH = "player_skin/boss_idle.png";
    public static final String CAT_FILE_PATH = "player_skin/cat_idle.png";
    public static final String COLLEAGUE_FILE_PATH = "player_skin/clerk_idle.png";
    public static final String ITSHNIK_FILE_PATH = "player_skin/it_idle.png";


    // NPC
    public static final float NPC_DETECTION_RANGE = 200f;
    public static final float NPC_FOV_ANGLE = 90f;
    public static final float NPC_MIN_DELAY = 5f;
    public static final float NPC_MAX_DELAY = 15f;

    public static final Position BABKA_START = new Position(1100, 600, Character.Direction.RIGHT);
    public static final Position BOSS_START = new Position(72, 67, Character.Direction.LEFT);
    public static final Position CAT_START = new Position(730, 390, Character.Direction.UP);
    public static final Position COLLEAGUE1_START = new Position(73, 550, Character.Direction.UP);
    public static final Position COLLEAGUE2_START = new Position(279, 605, Character.Direction.LEFT);
    public static final Position COLLEAGUE3_START = new Position(680, 630, Character.Direction.LEFT);
    public static final Position COLLEAGUE4_START = new Position(1240, 416, Character.Direction.LEFT);
    public static final Position COLLEAGUE5_START = new Position(590, 65, Character.Direction.LEFT);
    public static final Position ITSHNIK_START = new Position(1113, 65, Character.Direction.LEFT);

    public static final Position COFFEE_MACHINE = new Position(747, 360, Character.Direction.RIGHT);
    public static final Position COFFEE_CORNER = new Position(747, 425, Character.Direction.RIGHT);
    public static final Position COFFEE_EAT1 = new Position(598, 405, Character.Direction.DOWN);
    public static final Position COFFEE_EAT2 = new Position(681, 406, Character.Direction.DOWN);
    public static final Position SMALL_MEET_CENTER = new Position(856, 365, Character.Direction.UP);
    public static final Position SMALL_MEET_LEFT = new Position(818, 400, Character.Direction.RIGHT);
    public static final Position SMALL_MEET_RIGHT = new Position(904, 400, Character.Direction.LEFT);
    public static final Position TOILET = new Position(483, 677, Character.Direction.LEFT);
    public static final Position SERVER_TOP = new Position(792, 664, Character.Direction.LEFT);
    public static final Position SERVER_BOTTOM = new Position(826, 598, Character.Direction.DOWN);


    public record Position(int x, int y, Character.Direction direction) {
        // record автоматически создает конструктор, геттеры, equals, hashCode и toString
    }
}

