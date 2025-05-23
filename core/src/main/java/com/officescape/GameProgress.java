package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class GameProgress {
    private Array<Quest> quests;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout; // Для измерения текста

    public GameProgress(OrthographicCamera camera) {
        this.camera = camera;
        font = new BitmapFont(Gdx.files.internal("fonts/cyrillic.fnt"));
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout(); // Инициализация для измерения текста

        quests = new Array<>();
        quests.add(new Quest("Добыть диск от сервера", false));
        quests.add(new Quest("На сервере отключить камеру", false));
        quests.add(new Quest("Украсть ключ-карту у босса", false));
        quests.add(new Quest("Прокрасться мимо бабки!", false));
        quests.add(new Quest("Сбежать!", false));
        quests.add(new Quest("Не попасться...", false));
    }

    public void updateQuest(int index, boolean completed) {
        if (index >= 0 && index < quests.size) {
            quests.get(index).completed = completed;
        }
    }

    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        // Рассчитываем размеры текста
        float padding = 40f; // Отступы от краев
        float lineHeight = 40f;
        float titleHeight = 50f;

        // Измеряем ширину заголовка
        glyphLayout.setText(font, "Прогресс игры:");
        float titleWidth = glyphLayout.width;

        // Находим самую широкую строку квестов
        float maxQuestWidth = 0;
        for (Quest quest : quests) {
            glyphLayout.setText(font, "✓ " + quest.description);
            maxQuestWidth = Math.max(maxQuestWidth, glyphLayout.width);
        }

        // Определяем размеры панели
        float panelWidth = Math.max(titleWidth, maxQuestWidth) + 2 * padding;
        float panelHeight = titleHeight + (quests.size * lineHeight) + 2 * padding;

        // Позиция панели (центрируем)
        float cameraX = camera.position.x - camera.viewportWidth / 2;
        float cameraY = camera.position.y - camera.viewportHeight / 2;
        float panelX = cameraX + (camera.viewportWidth - panelWidth) / 2;
        float panelY = cameraY + (camera.viewportHeight - panelHeight) / 2;

        // Отрисовка фона и рамки
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.8f, 1);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        // Отрисовка текста
        float startX = panelX + padding;
        float startY = panelY + panelHeight - padding;

        batch.begin();
        font.draw(batch, "Прогресс игры:", startX, startY);

        for (int i = 0; i < quests.size; i++) {
            Quest quest = quests.get(i);
            float yPos = startY - titleHeight - (i * lineHeight);

            if (quest.completed) {
                font.setColor(Color.GREEN);
                font.draw(batch, "• " + quest.description, startX, yPos);
            } else {
                font.setColor(Color.RED);
                font.draw(batch, "• " + quest.description, startX, yPos);
            }
            font.setColor(Color.WHITE);
        }
        batch.end();
    }

    private static class Quest {
        String description;
        boolean completed;

        Quest(String description, boolean completed) {
            this.description = description;
            this.completed = completed;
        }
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
