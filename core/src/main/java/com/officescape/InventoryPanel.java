package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.officescape.item.Item;
import com.officescape.item.Key;
import com.officescape.item.Stepler;

public class InventoryPanel {
    private final ObjectMap<Class<? extends Item>, InventoryItem> inventoryItems;
    private final BitmapFont font;
    private final BitmapFont countFont;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout glyphLayout;
    private boolean visible = false;
    private final OrthographicCamera camera;

    private static class InventoryItem {
        Item item;
        int count;
        String name;
        Texture icon;

        InventoryItem(Item item, String name, Texture icon) {
            this.item = item;
            this.count = 0;
            this.name = name;
            this.icon = icon;
        }
    }

    public InventoryPanel(OrthographicCamera camera, Array<Item> worldItems) {
        this.camera = camera;
        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
        inventoryItems = new ObjectMap<>();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ArialRegular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20 * Math.round(Gdx.graphics.getDensity());
        parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!?()•-";
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);

        FreeTypeFontGenerator.FreeTypeFontParameter countParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        countParameter.size = 30 * Math.round(Gdx.graphics.getDensity());
        countParameter.characters = "0123456789";
        countFont = generator.generateFont(countParameter);
        countFont.setColor(Color.YELLOW);

        generator.dispose();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        countFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        for (Item item : worldItems) {
            if (item instanceof Key) {
                inventoryItems.put(Key.class, new InventoryItem(item, "Key", new Texture("items/key.png")));
            } else if (item instanceof Stepler) {
                inventoryItems.put(Stepler.class, new InventoryItem(item, "Stepler", new Texture("items/stepler.png")));
            }
        }

    }

    public void update(Array<Item> worldItems) {
        for (InventoryItem invItem : inventoryItems.values()) {
            invItem.count = 0;
        }

        for (Item worldItem : worldItems) {
            if (worldItem.isPickedUp) {
                if (worldItem instanceof Key) {
                    inventoryItems.get(Key.class).count++;
                } else if (worldItem instanceof Stepler) {
                    inventoryItems.get(Stepler.class).count++;
                }
            }
        }
    }

    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        if (!visible || inventoryItems.size == 0) return;

        // Размеры элементов
        float padding = 20f;
        float itemWidth = 100f;
        float itemHeight = 120f;
        float spacing = 10f;
        float panelHeight = itemHeight + 2 * padding;

        // Общая ширина панели
        float totalWidth = (inventoryItems.size * itemWidth) + ((inventoryItems.size - 1) * spacing) + 2 * padding;

        // Позиция панели (низ экрана, по центру)
        float panelX = (camera.viewportWidth - totalWidth) / 2;
        float panelY = 20f; // Отступ снизу

        // Рисуем фон панели
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        shapeRenderer.rect(panelX, panelY, totalWidth, panelHeight);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float currentX = panelX + padding;
        for (InventoryItem invItem : inventoryItems.values()) {
            if (invItem == null || invItem.icon == null) continue;

            // 1. Размер иконки (фиксированный, чтобы влезал в ячейку)
            float iconSize = 64f; // Фиксированный размер иконки
            float iconX = currentX + (itemWidth - iconSize) / 2; // Центрирование по горизонтали
            float iconY = panelY + padding + (itemHeight - iconSize) / 2 + 10; // Центрирование по вертикали + небольшой отступ

            // 2. Рисуем иконку (с масштабированием)
            batch.draw(
                invItem.icon,
                iconX,
                iconY,
                iconSize,
                iconSize
            );

            // 3. Рисуем количество (желтым, если > 0)
            if (invItem.count > 0) {
                String countText = "" + invItem.count;
                glyphLayout.setText(countFont, countText);
                countFont.draw(
                    batch,
                    countText,
                    currentX + (itemWidth - glyphLayout.width) / 2,
                    panelY + padding + 30
                );
            }

            // 4. Рисуем название (белым)
            glyphLayout.setText(font, invItem.name);
            font.draw(
                batch,
                invItem.name,
                currentX + (itemWidth - glyphLayout.width) / 2,
                panelY + padding + 15
            );

            currentX += itemWidth + spacing;
        }
        batch.end();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void dispose() {
        font.dispose();
        countFont.dispose();
        shapeRenderer.dispose();
        for (InventoryItem invItem : inventoryItems.values()) {
            invItem.icon.dispose();
        }
    }
}
