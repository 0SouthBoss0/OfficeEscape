package com.officescape;

import com.badlogic.gdx.utils.Array;
import com.officescape.unit.Character;

import java.util.Arrays;

public class GameConstants {
    // Player
    public static final float PLAYER_DEFAULT_SPEED = 200f;
    public static final float PLAYER_DEFAULT_SCALE = 1f;
    public static final float PLAYER_COLLISION_RATIO = 0.4f;
    public static final int PLAYER_START_X = 50;
    public static final int PLAYER_START_Y = 660;
    public static final float MAX_PLAYER_WIDTH = 20.7f;
    public static final float MAX_PLAYER_HEIGHT = 20.7f;
    public static final float STEP_DURATION = 0.2f;
    public static final int ITEM_THROW_DISTANCE = 100;

    // Pathfinding
    public static final float PATH_REACH_THRESHOLD = 1f;
    public static final float NODE_SEARCH_RADIUS = 8f;
    public static final float IS_WALL = 0.3f;
    public static final float IS_FURNUTURE = 0.3f;

    // Map
    public static final String MAP_FILE_PATH = "map.tmx";
    public static final String COLLISION_LAYER_NAME = "walls";
    public static final String FURNITURE_LAYER_NAME = "furniture";

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

    //reactions
    public static final String REACTION_ATTENTION_FILE_PATH = "player_skin/reaction_attention.png";
    public static final String REACTION_SLEEP_FILE_PATH = "player_skin/reaction_sleep.png";
    public static final String REACTION_STARS_FILE_PATH = "player_skin/reaction_stars.png";

    // NPC params
    public static final float NPC_DETECTION_RANGE = 200f;
    public static final float NPC_FOV_ANGLE = 90f;
    public static final float NPC_MIN_DELAY = 5f;
    public static final float NPC_MAX_DELAY = 15f;

    // NPC start positions
    public static final Position BABKA_START = new Position(1100, 600, Character.Direction.RIGHT);
    public static final Position BOSS_START = new Position(72, 67, Character.Direction.LEFT);
    public static final Position CAT_START = new Position(730, 390, Character.Direction.UP);
    public static final Position COLLEAGUE1_START = new Position(73, 550, Character.Direction.UP);
    public static final Position COLLEAGUE2_START = new Position(279, 605, Character.Direction.LEFT);
    public static final Position COLLEAGUE3_START = new Position(680, 630, Character.Direction.LEFT);
    public static final Position COLLEAGUE4_START = new Position(1240, 416, Character.Direction.LEFT);
    public static final Position COLLEAGUE5_START = new Position(590, 65, Character.Direction.LEFT);
    public static final Position ITSHNIK_START = new Position(1113, 65, Character.Direction.LEFT);

    // NPC places positions
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

    // NPC waypoints
    public static final Position[] BABKA_WAYPOINTS = {BABKA_START};
    public static final Position[] BOSS_WAYPOINTS = {BOSS_START, COFFEE_CORNER, TOILET, SMALL_MEET_CENTER};
    public static final Position[] CAT_WAYPOINTS = {CAT_START, SERVER_BOTTOM, SMALL_MEET_LEFT};
    public static final Position[] COLLEGUE1_WAYPOINTS = {COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT};
    public static final Position[] COLLEGUE2_WAYPOINTS = {COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT};
    public static final Position[] COLLEGUE3_WAYPOINTS = {COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT};
    public static final Position[] COLLEGUE4_WAYPOINTS = {COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT};
    public static final Position[] COLLEGUE5_WAYPOINTS = {COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT};
    public static final Position[] ITSHNIK_WAYPOINTS = {ITSHNIK_START, SERVER_TOP, COFFEE_MACHINE};

    // Items params
    public static final float ITEM_PICKUP_RANGE = 100f;
    public static final float ITEM_HIGHLIGHT_RANGE = 100f;

    // Items textures
    public static final String STAPLER_FILE_PATH = "items/stapler.png";
    public static final String KEY_CARD_FILE_PATH = "items/key_card.png";
    public static final String FLASH_FILE_PATH = "items/flash.png";
    public static final String CAMERA_GRAY_FILE_PATH = "items/camera_gray.png";
    public static final String CAMERA_RED_FILE_PATH = "items/camera_red.png";
    public static final String COFFEE_FILE_PATH = "items/coffee.png";
    public static final String TURNSTILE_FILE_PATH = "items/turnstile.png";
    public static final String FISH_FILE_PATH = "items/fish.png";
    public static final String WARDROBE_FILE_PATH = "items/wardrobe.png";
    public static final String COFFEE_MACHINE_FILE_PATH = "items/coffee_machine.png";
    public static final String CRASHED_COFFEE_MACHINE_FILE_PATH = "items/crashed_coffee_machine.png";
    public static final String PRINTER_FILE_PATH = "items/printer.png";
    public static final String CRASHED_PRINTER_FILE_PATH = "items/crashed_printer.png";
    public static final String TRASH_FILE_PATH = "items/trash.png";
    public static final String FIRE_TRASH_FILE_PATH = "items/fire_trash.png";

    // Items scales
    public static final float STAPLER_SCALE = 0.5f;
    public static final float KEY_CARD_SCALE = 0.5f;
    public static final float FLASH_SCALE = 0.05f;
    public static final float CAMERA_GRAY_SCALE = 0.05f;
    public static final float CAMERA_RED_SCALE = 0.05f;
    public static final float COFFEE_SCALE = 0.05f;
    public static final float TURNSTILE_SCALE = 0.05f;
    public static final float FISH_FILE_SCALE = 0.05f;
    public static final float WARDROBE_SCALE = 0.7f;
    public static final float COFFEE_MACHINE_SCALE = 0.05f;
    public static final float CRASHED_COFFEE_SCALE = 0.05f;
    public static final float PRINTER_FILE_SCALE = 0.05f;
    public static final float CRASHED_PRINTER_SCALE = 0.05f;
    public static final float TRASH_FILE_SCALE = 0.05f;
    public static final float FIRE_TRASH_SCALE = 0.05f;


    // Items positions
    public static final Position FLASH_FROM_SERVER = new Position(1080, 30, Character.Direction.UP);
    public static final Position STEPLER = new Position(300, 300, Character.Direction.UP);

    // Font params
    public static String FONT_FILE_PATH = "fonts/ArialRegular.ttf";
    public static String FONT_CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!?()•-";
    public static String FONT_NUMS = "0123456789";

    // Game progress panel
    public static String[] QUEST_LIST = new String[]{"Добыть диск от сервера", "На сервере отключить камеру",
        "Украсть ключ-карту у босса", "Прокрасться мимо бабки", "Сбежать!", "Не попасться..."};
    public static int FONT_SIZE_PROGRESS = 50;
    public static float PADDING_PROGRESS = 40f;
    public static float LINE_HEIGHT_PROGRESS = 50f;
    public static float TITLE_HEIGHT_PROGRESS = 50f;
    public static String QUEST_LABEL = "Прогресс игры:";

    // Inventory panel
    public static int FONT_SIZE_INVENTORY = 20;
    public static int NUMS_SIZE_INVENTORY = 30;
    public static float PADDING_INVENTORY = 20f;
    public static float ITEM_WIDTH_INVENTORY = 100f;
    public static float ITEM_HEIGHT_INVENTORY = 120f;
    public static float SPACING_INVENTORY = 10f;
    public static float PADDING_PANEL_DOWN_INVENTORY = 20f;
    public static float ICON_SIZE_INVENTORY = 64f;
    public static int PADDING_FOR_ICON_INVENTORY = 30;
    public static int PADDING_FOR_NAME_INVENTORY = 15;


    public record Position(int x, int y, Character.Direction direction) {
    }
}

