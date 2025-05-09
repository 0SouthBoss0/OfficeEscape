package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen {
    private Player player;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // Для TMX карты
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects = new Array<>();


    @Override
    public void show() {
        batch = new SpriteBatch();
        player = new Player("player.png", collisionRects); // Передаем стены

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        tiledMap = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        loadCollisions();
    }

    private void loadCollisions() {
        collisionRects.clear(); // Очищаем перед загрузкой

        // Убедитесь, что слой называется именно "walls" (как в TMX файле)
        MapObjects objects = tiledMap.getLayers().get("walls").getObjects();

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(rect);
            }
        }

        // После загрузки стен обновите граф у игрока
        if (player != null) {
            player.updateGraphWithNewWalls(collisionRects);
        }
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
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
