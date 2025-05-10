package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.officescape.unit.Player;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class MainScreen implements Screen {
    private Player player;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects = new Array<>();
    private Viewport viewport;

    @Override
    public void show() {
        batch = new SpriteBatch();
        player = new Player(GameConstants.PLAYER_FILE_PATH);

        camera = new OrthographicCamera();
        tiledMap = new TmxMapLoader().load(GameConstants.MAP_FILE_PATH);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Получаем размеры карты в пикселях
        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        float mapPixelWidth = mapWidth * tileWidth;
        float mapPixelHeight = mapHeight * tileHeight;

        // Создаем FitViewport (автомасштабирование + центрирование)
        viewport = new FitViewport(mapPixelWidth, mapPixelHeight, camera);
        viewport.apply();  // Применяем текущий размер окна

        loadCollisions();
    }

    private void loadCollisions() {
        collisionRects.clear(); // Очищаем перед загрузкой

        MapObjects objects = tiledMap.getLayers().get(GameConstants.COLLISION_LAYER_NAME).getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(rect);
            }
        }
        // TODO: взять максимум от всех player?
        TiledGraph.init(GameConstants.TILED_GRAPH_WIDTH, GameConstants.TILED_GRAPH_HEIGHT, GameConstants.TILED_SIZE, collisionRects, player.getCollisionWidth(), player.getCollisionHeight());
        player.updateGraphWithNewWalls();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обработка клика
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            player.goToCoords(clickPos.x, clickPos.y);
        }

        player.update(delta, collisionRects);
        //camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);  // Обновляем Viewport при изменении размера окна
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);  // Центрируем камеру
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }
}
