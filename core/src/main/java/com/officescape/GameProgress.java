package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class GameProgress {
    private final Array<Quest> quests = new Array<>();
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final GlyphLayout glyphLayout;

    public GameProgress(OrthographicCamera camera) {
        this.camera = camera;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(GameConstants.FONT_FILE_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = GameConstants.FONT_SIZE_PROGRESS * Math.round(Gdx.graphics.getDensity());
        parameter.characters = GameConstants.FONT_CHARS;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.setColor(Color.WHITE);

        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();

        for (String quest_desc : GameConstants.QUEST_LIST) {
            quests.add(new Quest(quest_desc));
        }
    }

    public void updateQuest(int index, boolean completed) {
        if (index >= 0 && index < quests.size) {
            quests.get(index).completed = completed;
        }
    }


    public boolean getQuestStatus(int index) {
        return quests.get(index).completed;

    }

    public BitmapFont getFont() {
        return font;
    }


    public void render(SpriteBatch batch) {
        float padding = GameConstants.PADDING_PROGRESS;
        float lineHeight = GameConstants.LINE_HEIGHT_PROGRESS;
        float titleHeight = GameConstants.TITLE_HEIGHT_PROGRESS;

        glyphLayout.setText(font, "Прогресс игры:");
        float titleWidth = glyphLayout.width;

        float maxQuestWidth = 0;
        for (Quest quest : quests) {
            glyphLayout.setText(font, "• " + quest.description);
            maxQuestWidth = Math.max(maxQuestWidth, glyphLayout.width);
        }

        float panelWidth = Math.max(titleWidth, maxQuestWidth) + 2 * padding;
        float panelHeight = titleHeight + (quests.size * lineHeight) + 2 * padding;

        // center panel
        float cameraX = camera.position.x - camera.viewportWidth / 2;
        float cameraY = camera.position.y - camera.viewportHeight / 2;
        float panelX = cameraX + (camera.viewportWidth - panelWidth) / 2;
        float panelY = cameraY + (camera.viewportHeight - panelHeight) / 2;

        // draw background and border
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.8f, 1);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        // draw text
        float startX = panelX + padding;
        float startY = panelY + panelHeight - padding;

        batch.begin();
        font.draw(batch, GameConstants.QUEST_LABEL, startX, startY);

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


    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }

    public boolean areAllQuestsCompleted() {
        for (Quest quest : quests) {
            if (!quest.completed) {
                return false;
            }
        }
        return true;
    }
}
