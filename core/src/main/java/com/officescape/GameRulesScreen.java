package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

public class GameRulesScreen implements Screen {
    private final OfficeEscape game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout glyphLayout;
    private final SpriteBatch batch;

    private float padding;
    private float lineHeight;
    private float scaleFactor;

    public GameRulesScreen(OfficeEscape game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.glyphLayout = new GlyphLayout();
        this.batch = new SpriteBatch();

        initCamera();
        updateScaleParameters();
        initFont();
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void updateScaleParameters() {
        // Более точный расчет масштабирования
        scaleFactor = Math.min(
            Gdx.graphics.getWidth() / 800f,  // Базовое разрешение по ширине
            Gdx.graphics.getHeight() / 600f  // Базовое разрешение по высоте
        );
        padding = 15 * scaleFactor;
        lineHeight = 20 * scaleFactor;
    }

    private void initFont() {
        if (font != null) font.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(GameConstants.FONT_FILE_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(GameConstants.FONT_SIZE_RULES * scaleFactor * 0.8f); // Уменьшаем шрифт на 20%
        parameter.characters = GameConstants.FONT_CHARS;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setColor(Color.WHITE);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);


        String title = "Побег из офиса";
        String[] rules = {
            "Вы - обычный офисный работник, застрявший в рутине. Сегодня пятница,",
            "и вы отчаянно хотите уйти пораньше, но для этого нужно выполнить задания! ",
            "",
            "Цель:",
            "1. Тайком выполнить все задания",
            "2. Собрать все необходимые предметы (флешку, пропуск)",
            "3. Незаметно пробраться к выходу ",
            "",
            "Опасности:",
            "- Если тебя увидят в запретных зонах (кабинет босса, серверная) - мгновенный проигрыш",
            "- Если тебя увидят, что ты ломаешь вещи или прячешься - мгновенный проигрыш",
            "- Не спрятаться после удара степлером, когда коллеги тебя ищут - мгновенный проигрыш",
            "",
            "Управление:",
            "- WASD: перемещение",
            "- TAB: список заданий",
            "- I: инвентарь",
            "- E: сломать или подобрать вещь",
            "- Н: спрятаться",
            "- T: бросить степлер в коллегу",
            "- С: выпить кофе",
            "",
            "Нажмите ENTER или ESC чтобы начать побег"
        };

        // Расчет размеров с учетом текущего масштаба
        glyphLayout.setText(font, title);
        float titleWidth = glyphLayout.width;

        float maxTextWidth = 0;
        for (String rule : rules) {
            glyphLayout.setText(font, rule);
            maxTextWidth = Math.max(maxTextWidth, glyphLayout.width);
        }

        float panelWidth = Math.max(titleWidth, maxTextWidth) + 2 * padding;
        float panelHeight = (rules.length + 1) * lineHeight + 2 * padding;

        float panelX = (Gdx.graphics.getWidth() - panelWidth) / 2;
        float panelY = (Gdx.graphics.getHeight() - panelHeight) / 2;

        // Отрисовка
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.8f, 1);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();


        batch.begin();

        font.setColor(Color.ORANGE);
        font.draw(batch, title, panelX + padding, panelY + panelHeight - padding);


        font.setColor(Color.WHITE);
        for (int i = 0; i < rules.length; i++) {
            float yPos = panelY + panelHeight - padding - (i + 1) * lineHeight;


            if (rules[i].startsWith("Цель:")) {
                font.setColor(Color.GREEN);
            } else if (rules[i].startsWith("Управление:")) {
                font.setColor(Color.YELLOW);

            } else if (rules[i].startsWith("Опасности:")) {
                font.setColor(Color.RED);
            }else if (rules[i].isEmpty()) {
                font.setColor(Color.WHITE);
            } else {
                font.setColor(Color.LIGHT_GRAY);
            }

            font.draw(batch, rules[i], panelX + padding, yPos);
        }
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.onRulesCompleted();
            game.setScreen(new MainScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем viewport камеры
        camera.setToOrtho(false, width, height);
        camera.update();

        // Пересчитываем все параметры
        updateScaleParameters();
        initFont();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
