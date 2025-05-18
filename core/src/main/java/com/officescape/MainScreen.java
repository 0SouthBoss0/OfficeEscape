// MainScreen.java
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    private Array<Rectangle> collisionRects = new Array<>();
    private Viewport viewport;
    private boolean showDebug = false;

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        player = new Player(GameConstants.PLAYER_FILE_PATH);

        NPCFactory npcFactory = NPCFactory.getInstance();
        npcFactory.createNPC(GameConstants.NPC_FILE_PATH, 50, 50);
        npcFactory.createNPC(GameConstants.NPC_FILE_PATH, 150, 50);
        npcFactory.createNPC(GameConstants.NPC_FILE_PATH, 50, 150);

        npcs = npcFactory.getAllNPCs();


        camera = new OrthographicCamera();
        tiledMap = new TmxMapLoader().load(GameConstants.MAP_FILE_PATH);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        float mapPixelWidth = mapWidth * tileWidth;
        float mapPixelHeight = mapHeight * tileHeight;

        // Используем StretchViewport для растягивания на весь экран
        viewport = new StretchViewport(mapPixelWidth, mapPixelHeight, camera);
        viewport.apply();

        loadCollisions();
    }

    private void loadCollisions() {
        collisionRects.clear();
        MapObjects objects = tiledMap.getLayers().get(GameConstants.COLLISION_LAYER_NAME).getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(rect);
            }
        }
        TiledGraph.init(GameConstants.TILED_GRAPH_WIDTH, GameConstants.TILED_GRAPH_HEIGHT,
            GameConstants.TILED_SIZE, collisionRects);
        player.updateGraphWithNewWalls();
        for (NPC npc : npcs) {
            npc.updateGraphWithNewWalls();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            showDebug = !showDebug;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            player.goToCoords(clickPos.x, clickPos.y);
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
        batch.end();

        if (showDebug) {
            renderDebug();
        }
    }

    private void renderDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Рисуем узлы графа
        shapeRenderer.begin(ShapeType.Filled);
        TiledGraph graph = TiledGraph.getInstance();
        for (Vector2 node : graph.getNodes()) {
            if (graph.isWall(node, player.getCollisionWidth(), player.getCollisionHeight())) {
                shapeRenderer.setColor(1, 0, 0, 0.3f); // Красный для стен
            } else {
                shapeRenderer.setColor(0, 1, 0, 0.3f); // Зеленый для проходимых узлов
            }
            shapeRenderer.circle(node.x, node.y, 2);
        }
        shapeRenderer.end();

        // Рисуем путь
        if (player.getCurrentPath() != null && player.getCurrentPath().getCount() > 0) {
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 1); // Белый для пути

            Vector2 prev = player.getCurrentPath().get(0);
            for (int i = 1; i < player.getCurrentPath().getCount(); i++) {
                Vector2 current = player.getCurrentPath().get(i);
                shapeRenderer.line(prev, current);
                prev = current;
            }
            shapeRenderer.end();
        }
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
    }

    // Остальные методы интерфейса Screen оставлены без изменений
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
