package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen {
    private Player player;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // Для карты (пока просто фон)
    private Texture background;

    @Override
    public void show() {

        batch = new SpriteBatch();
        player = new Player("player.png"); // Замените на путь к текстуре вашего игрока

        // Создаем камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Загружаем фон (временное решение)
        background = new Texture("map.jpg"); // Замените на путь к вашей карте
    }

    @Override
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем игрока
        player.update(delta);

        // Обновляем камеру (следим за игроком)
        //camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        // Рендерим
        batch.begin();
        batch.draw(background, 0, 0);
        player.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
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
        background.dispose();
    }
}
