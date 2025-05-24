package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.officescape.item.Item;
import com.officescape.item.Flash;
import com.officescape.item.Stapler;
import com.officescape.item.ThrowableItem;
import com.officescape.unit.NPC;
import com.officescape.unit.NPCFactory;
import com.officescape.unit.Player;


public class MainScreen implements Screen {
    private Player player;
    private Array<NPC> npcs;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Array<Rectangle> collisionRects = new Array<>();
    private final Array<Rectangle> furnitureRects = new Array<>();
    private Viewport viewport;
    private boolean showDebug = false;
    private Array<Item> items;
    private ShapeRenderer itemShapeRenderer;
    private GameProgress gameProgress;
    private InventoryPanel inventoryPanel;

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        player = new Player(GameConstants.PLAYER_FILE_PATH);

        NPCFactory npcFactory = NPCFactory.getInstance();
        npcFactory.setPlayer(player);
        npcFactory.createBabka(GameConstants.BABKA_START.x(), GameConstants.BABKA_START.y(), GameConstants.BABKA_START.direction(), GameConstants.BABKA_WAYPOINTS);
        npcFactory.createCat(GameConstants.CAT_START.x(), GameConstants.CAT_START.y(), GameConstants.CAT_START.direction(), GameConstants.CAT_WAYPOINTS);
        npcFactory.createBoss(GameConstants.BOSS_START.x(), GameConstants.BOSS_START.y(), GameConstants.BOSS_START.direction(), GameConstants.BOSS_WAYPOINTS);
        npcFactory.createColleague(GameConstants.COLLEAGUE1_START.x(), GameConstants.COLLEAGUE1_START.y(), GameConstants.COLLEAGUE1_START.direction(), GameConstants.COLLEGUE1_WAYPOINTS);
        npcFactory.createColleague(GameConstants.COLLEAGUE2_START.x(), GameConstants.COLLEAGUE2_START.y(), GameConstants.COLLEAGUE2_START.direction(), GameConstants.COLLEGUE2_WAYPOINTS);
        npcFactory.createColleague(GameConstants.COLLEAGUE3_START.x(), GameConstants.COLLEAGUE3_START.y(), GameConstants.COLLEAGUE3_START.direction(), GameConstants.COLLEGUE3_WAYPOINTS);
        npcFactory.createColleague(GameConstants.COLLEAGUE4_START.x(), GameConstants.COLLEAGUE4_START.y(), GameConstants.COLLEAGUE4_START.direction(), GameConstants.COLLEGUE4_WAYPOINTS);
        npcFactory.createColleague(GameConstants.COLLEAGUE5_START.x(), GameConstants.COLLEAGUE5_START.y(), GameConstants.COLLEAGUE5_START.direction(), GameConstants.COLLEGUE5_WAYPOINTS);
        npcFactory.createItshnik(GameConstants.ITSHNIK_START.x(), GameConstants.ITSHNIK_START.y(), GameConstants.ITSHNIK_START.direction(), GameConstants.ITSHNIK_WAYPOINTS);
        npcs = npcFactory.getAllNPCs();

        items = new Array<>();
        items.add(new Flash(GameConstants.FLASH_FROM_SERVER.x(), GameConstants.FLASH_FROM_SERVER.y()));
        items.add(new Stapler(GameConstants.STEPLER.x(), GameConstants.STEPLER.y()));
        itemShapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        tiledMap = new TmxMapLoader().load(GameConstants.MAP_FILE_PATH);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        gameProgress = new GameProgress(camera);
        inventoryPanel = new InventoryPanel(camera, items);

        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        float mapPixelWidth = mapWidth * tileWidth;
        float mapPixelHeight = mapHeight * tileHeight;

        viewport = new StretchViewport(mapPixelWidth, mapPixelHeight, camera);
        viewport.apply();

        loadCollisions();
    }

    private void loadCollisions() {
        collisionRects.clear();
        furnitureRects.clear();

        MapObjects objects = tiledMap.getLayers().get(GameConstants.COLLISION_LAYER_NAME).getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(rect);
            }
        }
        MapObjects furnObjects = tiledMap.getLayers().get(GameConstants.FURNITURE_LAYER_NAME).getObjects();
        for (MapObject furnObject : furnObjects) {
            if (furnObject instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) furnObject).getRectangle();
                furnitureRects.add(rect);
            }
        }
        TiledGraph.init(GameConstants.TILED_GRAPH_WIDTH, GameConstants.TILED_GRAPH_HEIGHT,
            GameConstants.TILED_SIZE, collisionRects, furnitureRects);
        player.updateGraphWithNewWalls();
        for (NPC npc : npcs) {
            npc.updateGraphWithNewWalls();
        }
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        boolean showProgress = Gdx.input.isKeyPressed(Input.Keys.TAB);
        boolean showInventory = Gdx.input.isKeyPressed(Input.Keys.I);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            showDebug = !showDebug;
        }

        inventoryPanel.setVisible(showInventory);
        inventoryPanel.update(items);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            if (showDebug) {
                System.out.printf("Mouse click at: X=%.1f, Y=%.1f%n", clickPos.x, clickPos.y);
            }
            player.goToCoords(clickPos.x, clickPos.y);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            for (Item item : items) {
                if (item.canBeTaken()) {
                    item.take(player);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            for (Item item : items) {
                if (item.canBeThrown() && item.isTaken) {
                    ((ThrowableItem) item).onThrow(player);
                    item.isTaken = false;
                    break;
                }
            }
        }

        player.update(delta, collisionRects);
        for (NPC npc : npcs) {
            npc.update(delta, collisionRects);
        }

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        for (NPC npc : npcs) {
            npc.draw(batch);
        }
        for (Item item : items) {
            item.update(player);
            item.draw(batch);
        }
        batch.end();

        // render highlight of items
        itemShapeRenderer.setProjectionMatrix(camera.combined);
        itemShapeRenderer.begin(ShapeType.Line);
        for (Item item : items) {
            item.drawHighlight(itemShapeRenderer);
        }
        itemShapeRenderer.end();

        if (showProgress) {
            gameProgress.render(batch);
        }
        inventoryPanel.render(batch);

        if (showDebug) {
            renderDebug();
        }
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // draw nodes
        shapeRenderer.begin(ShapeType.Filled);
        TiledGraph graph = TiledGraph.getInstance();
        for (Vector2 node : graph.getNodes()) {
            if (graph.isWall(node, player.getCollisionWidth(), player.getCollisionHeight())) {
                shapeRenderer.setColor(1, 0, 0, 0.3f); // Красный для стен
            } else if (graph.isFurniture(node, player.getCollisionWidth(), player.getCollisionHeight())) {
                shapeRenderer.setColor(1, 0.5f, 0, 0.3f); // Оранжевый для мебели
            } else {
                shapeRenderer.setColor(0, 1, 0, 0.3f); // Зеленый для проходимых узлов
            }
            shapeRenderer.circle(node.x, node.y, 2);
        }
        shapeRenderer.end();

        // draw path
        if (player.getCurrentPath() != null && player.getCurrentPath().getCount() > 0) {
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 1);

            Vector2 prev = player.getCurrentPath().get(0);
            for (int i = 1; i < player.getCurrentPath().getCount(); i++) {
                Vector2 current = player.getCurrentPath().get(i);
                shapeRenderer.line(prev, current);
                prev = current;
            }
            shapeRenderer.end();
        }

        // draw NPC view sector
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 0, 0.3f);
        for (NPC npc : npcs) {
            Vector2 pos = new Vector2(npc.getX(), npc.getY());
            Vector2 facingDir = npc.getFacingDirection();

            float baseAngle = facingDir.angleDeg();

            float fovAngle = GameConstants.NPC_FOV_ANGLE;
            float range = GameConstants.NPC_DETECTION_RANGE;

            Vector2 leftDir = new Vector2(1, 0).setAngleDeg(baseAngle - fovAngle / 2).scl(range);
            Vector2 rightDir = new Vector2(1, 0).setAngleDeg(baseAngle + fovAngle / 2).scl(range);

            shapeRenderer.triangle(
                pos.x, pos.y,
                pos.x + leftDir.x, pos.y + leftDir.y,
                pos.x + rightDir.x, pos.y + rightDir.y
            );
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        player.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        itemShapeRenderer.dispose();
        for (Item item : items) {
            item.dispose();
        }
        gameProgress.dispose();
        inventoryPanel.dispose();

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
