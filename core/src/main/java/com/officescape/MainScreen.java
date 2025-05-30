package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.officescape.item.*;
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
    private final Array<Rectangle> forbiddenZones = new Array<>();
    private final Rectangle cameraZone = new Rectangle(97, 102, 62, 30);
    private final Rectangle exitZone = new Rectangle(1160, 700, 70, 50);
    private Viewport viewport;
    private boolean showDebug = false;
    private Array<Item> items;
    private ShapeRenderer itemShapeRenderer;
    private GameProgress gameProgress;
    private InventoryPanel inventoryPanel;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private OfficeEscape game;

    private float coffeeSpeedBoostTimer = 0;
    private boolean isCoffeeBoostActive = false;
    private float originalPlayerSpeed;

    public MainScreen(OfficeEscape game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        player = new Player(GameConstants.PLAYER_FILE_PATH);

        forbiddenZones.add(new Rectangle(97, 60, 62, 80));
        forbiddenZones.add(new Rectangle(800, 636, 100, 50));

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
        npcFactory.createColleague(GameConstants.COLLEAGUE6_START.x(), GameConstants.COLLEAGUE6_START.y(), GameConstants.COLLEAGUE6_START.direction(), GameConstants.COLLEGUE6_WAYPOINTS);
        npcFactory.createItshnik(GameConstants.ITSHNIK_START.x(), GameConstants.ITSHNIK_START.y(), GameConstants.ITSHNIK_START.direction(), GameConstants.ITSHNIK_WAYPOINTS);
        npcs = npcFactory.getAllNPCs();

        items = new Array<>();
        items.add(new Flash(GameConstants.FLASH_FROM_SERVER.x(), GameConstants.FLASH_FROM_SERVER.y(), GameConstants.FLASH_FROM_SERVER.direction()));
        items.add(new KeyCard(GameConstants.KEY_CARD.x(), GameConstants.KEY_CARD.y(), GameConstants.KEY_CARD.direction()));
        items.add(new Stapler(GameConstants.STEPLER_1.x(), GameConstants.STEPLER_1.y(), GameConstants.STEPLER_1.direction()));
        items.add(new Stapler(GameConstants.STEPLER_2.x(), GameConstants.STEPLER_2.y(), GameConstants.STEPLER_2.direction()));
        items.add(new Printer(GameConstants.PRINTER_1.x(), GameConstants.PRINTER_1.y(), GameConstants.PRINTER_1.direction()));
        items.add(new Printer(GameConstants.PRINTER_2.x(), GameConstants.PRINTER_2.y(), GameConstants.PRINTER_2.direction()));
        items.add(new Wardrobe(GameConstants.WARDROBE_1.x(), GameConstants.WARDROBE_1.y(), GameConstants.WARDROBE_1.direction()));
        items.add(new Wardrobe(GameConstants.WARDROBE_2.x(), GameConstants.WARDROBE_2.y(), GameConstants.WARDROBE_2.direction()));
        items.add(new Wardrobe(GameConstants.WARDROBE_3.x(), GameConstants.WARDROBE_3.y(), GameConstants.WARDROBE_3.direction()));
        items.add(new Wardrobe(GameConstants.WARDROBE_4.x(), GameConstants.WARDROBE_4.y(), GameConstants.WARDROBE_4.direction()));
        items.add(new Trash(GameConstants.TRASH_1.x(), GameConstants.TRASH_1.y(), GameConstants.TRASH_1.direction()));
        items.add(new Trash(GameConstants.TRASH_2.x(), GameConstants.TRASH_2.y(), GameConstants.TRASH_2.direction()));
        items.add(new Trash(GameConstants.TRASH_3.x(), GameConstants.TRASH_3.y(), GameConstants.TRASH_3.direction()));
        items.add(new Coffee(GameConstants.COFFEE_1.x(), GameConstants.COFFEE_1.y(), GameConstants.COFFEE_1.direction()));
        items.add(new Coffee(GameConstants.COFFEE_2.x(), GameConstants.COFFEE_2.y(), GameConstants.COFFEE_2.direction()));
        items.add(new Fish(GameConstants.FISH.x(), GameConstants.FISH.y(), GameConstants.FISH.direction()));
        items.add(new Camera(GameConstants.CAMERA.x(), GameConstants.CAMERA.y(), GameConstants.CAMERA.direction()));
        items.add(new CoffeeMachine(GameConstants.COFFEE_MACHINE.x(), GameConstants.COFFEE_MACHINE.y(), GameConstants.COFFEE_MACHINE.direction()));
        items.add(new TurnStile(GameConstants.TURNSTILE.x(), GameConstants.TURNSTILE.y(), GameConstants.TURNSTILE.direction()));
        items.add(new ButtonServer(GameConstants.BUTTON_SERVER.x(), GameConstants.BUTTON_SERVER.y(), GameConstants.BUTTON_SERVER.direction()));
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
        for (int i = 0; i < items.size; i++) {
            Item item = items.get(i);
            if (item instanceof Wardrobe || item instanceof Printer || item instanceof Trash || item instanceof TurnStile) {
                float[] vertices = item.sprite.getVertices();
                float minX = Math.min(Math.min(vertices[0], vertices[5]), Math.min(vertices[10], vertices[15]));
                float maxX = Math.max(Math.max(vertices[0], vertices[5]), Math.max(vertices[10], vertices[15]));
                float minY = Math.min(Math.min(vertices[1], vertices[6]), Math.min(vertices[11], vertices[16]));
                float maxY = Math.max(Math.max(vertices[1], vertices[6]), Math.max(vertices[11], vertices[16]));
                furnitureRects.add(new Rectangle(minX, minY, maxX - minX, maxY - minY));
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
        if (gameOver || gameWon) {
            renderGameEndScreen();
            return;
        }

        if (gameProgress.areAllQuestsCompleted()) {
            gameWon = true;
            return;
        }

        if (player.getCollisionBox().overlaps(cameraZone) && !gameProgress.getQuestStatus(1)) {
            gameOver = true;
            return;
        }

        if (isCoffeeBoostActive) {
            coffeeSpeedBoostTimer -= delta;
            if (coffeeSpeedBoostTimer <= 0) {
                player.speed = originalPlayerSpeed;
                isCoffeeBoostActive = false;
            }
        }

        // Проверка на проигрыш
        for (NPC npc : npcs) {
            if (npc.currentState == NPC.NPCState.PANIC && npc.isPlayerNearby(collisionRects)) {
                gameOver = true;
                return;
            }
        }

        for (Rectangle zone : forbiddenZones) {
            if (player.getCollisionBox().overlaps(zone) && isPlayerSeenByAnyNPC()) {
                gameOver = true;
                return;
            }
        }

        if (gameProgress.getQuestStatus(4)) {
            gameProgress.updateQuest(5, player.getCollisionBox().overlaps(exitZone));
        }

        Array<Item> itemsToRemove = new Array<>();
        for (Item item : items) {
            if (item instanceof Stapler stapler) {
                stapler.updateFlight(delta);
                if (stapler.isReadyToDispose()) {
                    itemsToRemove.add(stapler);
                }
            }
        }

        for (Item item : itemsToRemove) {
            item.dispose();
            items.removeValue(item, true);
        }


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
                if (item.canBeTaken(player)) {
                    ((TakeableItem) item).onTake(player, gameProgress);
                }
                if (item.canBeBroken(player)) {
                    ((BreakableItem) item).onBreak(player);
                    if (isPlayerSeenByAnyNPC()) {
                        gameOver = true;
                        return;
                    }
                    NPC.reportBrokenItem((BreakableItem) item);
                    break;
                }
                if (item.canBeUsed(player, inventoryPanel.getInventoryItems())) {
                    ((UsableItem) item).onUse(gameProgress);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            for (Item item : items) {
                if (item instanceof HideableItem hideable) {
                    float distance = Vector2.dst(player.getX(), player.getY(), item.getX(), item.getY());

                    if (distance < GameConstants.HIDE_DISTANCE) {
                        if (hideable.isPlayerHidden(player)) {
                            hideable.onUnhide(player);
                        } else {
                            hideable.onHide(player);

                        }
                        if (isPlayerSeenByAnyNPC()) {
                            gameOver = true;
                            return;
                        }
                        break;
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            for (Item item : items) {
                if (item instanceof Coffee) {
                    if (!isCoffeeBoostActive) {
                        items.removeValue(item, true);
                        originalPlayerSpeed = player.speed;
                        player.speed *= 2;
                        isCoffeeBoostActive = true;
                        coffeeSpeedBoostTimer = 15f;
                    }
                    break;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            for (Item item : items) {
                if (item.canBeThrown(player) && item.isTaken) {
                    ((ThrowableItem) item).onThrow(player);
                    item.isTaken = false;
                    break;
                }
            }
        }

        player.update(delta, collisionRects);
        for (int i = 0; i < npcs.size; i++) {
            npcs.get(i).update(delta, collisionRects);
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
            item.draw(batch);
        }
        batch.end();

        // render highlight of items
        itemShapeRenderer.setProjectionMatrix(camera.combined);
        itemShapeRenderer.begin(ShapeType.Line);
        for (Item item : items) {
            if (item.canBeTaken(player)) {
                item.drawHighlight(itemShapeRenderer, new Color(1, 1, 1, 1));
            }
            if (item.canBeHidden(player)) {
                item.drawHighlight(itemShapeRenderer, new Color(0f, 1f, 1f, 1));
            }
            if (item.canBeBroken(player)) {
                item.drawHighlight(itemShapeRenderer, new Color(1f, 0f, 0f, 1));
            }
            if (item.canBeUsed(player, inventoryPanel.getInventoryItems())) {
                item.drawHighlight(itemShapeRenderer, new Color(1f, 1f, 0f, 1));
            }
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

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 1, 0.3f); // Фиолетовый с прозрачностью
        for (Rectangle zone : forbiddenZones) {
            shapeRenderer.rect(zone.x, zone.y, zone.width, zone.height);
        }
        shapeRenderer.setColor(0, 0, 1, 0.3f);
        shapeRenderer.rect(cameraZone.x, cameraZone.y, cameraZone.width, cameraZone.height);

        shapeRenderer.setColor(0, 1, 1, 0.3f);
        shapeRenderer.rect(exitZone.x, exitZone.y, exitZone.width, exitZone.height);


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
        npcs.clear();

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

    private boolean isPlayerSeenByAnyNPC() {
        for (NPC npc : npcs) {
            if (npc.isPlayerNearby(collisionRects)) {
                return true;
            }
        }
        return false;
    }

    private void renderGameEndScreen() {
        // Приостанавливаем движение всех объектов
        player.setCanMove(false);

        // Параметры рамки
        float frameWidth = 800;
        float frameHeight = 500;
        float frameX = camera.position.x - frameWidth / 2;
        float frameY = camera.position.y - frameHeight / 2;
        float padding = 20;

        // Рендерим полупрозрачную рамку
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        // Внешняя рамка (темная)
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(frameX - 5, frameY - 5, frameWidth + 10, frameHeight + 10);

        // Внутренняя рамка (цвет зависит от победы/поражения)
        if (gameWon) {
            shapeRenderer.setColor(161 / 255f, 159 / 255f, 124 / 255f, 0.9f);
        } else {
            shapeRenderer.setColor(79 / 255f, 82 / 255f, 119 / 255f, 0.9f);
        }
        shapeRenderer.rect(frameX, frameY, frameWidth, frameHeight);
        shapeRenderer.end();

        // Текст сообщения
        batch.begin();

        String message = gameWon ? "ПОБЕДА! Все квесты выполнены!" : "Вас обнаружили!";
        String restartMessage = "Нажмите R для перезапуска";
        String winLoseText = gameWon ? "YOU WIN" : "GAME OVER";


        BitmapFont font = gameProgress.getFont();
        BitmapFont bigFont = new BitmapFont(font.getData(), font.getRegion(), font.usesIntegerPositions()); // Использует стандартный шрифт LibGDX
        bigFont.getData().setScale(3.0f);

        GlyphLayout bigLayout = new GlyphLayout(bigFont, winLoseText);

        float bigTextX = camera.position.x - bigLayout.width / 2;
        float bigTextY = camera.position.y + 50; // Выше центра

        bigFont.setColor(gameWon ?
            new Color(85 / 255f, 10 / 255f, 7 / 255f, 0.9f) :
            new Color(23 / 255f, 14 / 255f, 25 / 255f, 0.9f));
        bigFont.draw(batch, winLoseText, bigTextX, bigTextY);


        font.getData().setScale(1.0f);
        GlyphLayout mainLayout = new GlyphLayout(font, message);

        float mainTextX = camera.position.x - mainLayout.width / 2;
        float mainTextY = bigTextY - 100;

        font.draw(batch, message, mainTextX, mainTextY);

        GlyphLayout restartLayout = new GlyphLayout(font, restartMessage);

        float restartTextX = camera.position.x - restartLayout.width / 2;
        float restartTextY = mainTextY - 50;

        font.draw(batch, restartMessage, restartTextX, restartTextY);

        batch.end();

        // Возможность перезапустить игру
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameOver = false;
            gameWon = false;

            // Сбрасываем синглтон TiledGraph
            TiledGraph.reset();

            // Перезагрузка экрана
            dispose();
            game.setScreen(new MainScreen(game));
        }
    }
}
