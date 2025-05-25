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
import com.officescape.item.Flash;
import com.officescape.item.Stapler;
import com.officescape.item.TakeableItem;

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

        InventoryItem(Item item) {
            this.item = item;
            this.count = 0;
            this.name = item.getClass().getSimpleName();
            this.icon = item.sprite.getTexture();
        }
    }

    public InventoryPanel(OrthographicCamera camera, Array<Item> worldItems) {
        this.camera = camera;
        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
        inventoryItems = new ObjectMap<>();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(GameConstants.FONT_FILE_PATH));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = GameConstants.FONT_SIZE_INVENTORY * Math.round(Gdx.graphics.getDensity());
        parameter.characters = GameConstants.FONT_CHARS;
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);

        FreeTypeFontGenerator.FreeTypeFontParameter countParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        countParameter.size = GameConstants.NUMS_SIZE_INVENTORY * Math.round(Gdx.graphics.getDensity());
        countParameter.characters = GameConstants.FONT_NUMS;
        countFont = generator.generateFont(countParameter);
        countFont.setColor(Color.YELLOW);

        generator.dispose();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        countFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        for (Item item : worldItems) {
            if (item instanceof TakeableItem) {
                inventoryItems.put(item.getClass(), new InventoryItem(item));
            }
        }
    }

    public void update(Array<Item> worldItems) {
        for (InventoryItem invItem : inventoryItems.values()) {
            invItem.count = 0;
        }

        for (Item worldItem : worldItems) {
            if (worldItem.isTaken) {
                if (worldItem instanceof Flash) {
                    inventoryItems.get(Flash.class).count++;
                } else if (worldItem instanceof Stapler) {
                    inventoryItems.get(Stapler.class).count++;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!visible || inventoryItems.size == 0) return;

        // Размеры элементов
        float padding = GameConstants.PADDING_INVENTORY;
        float itemWidth = GameConstants.ITEM_WIDTH_INVENTORY;
        float itemHeight = GameConstants.ITEM_HEIGHT_INVENTORY;
        float spacing = GameConstants.SPACING_INVENTORY;
        float panelHeight = itemHeight + 2 * padding;

        float totalWidth = (inventoryItems.size * itemWidth) + ((inventoryItems.size - 1) * spacing) + 2 * padding;

        float panelX = (camera.viewportWidth - totalWidth) / 2;
        float panelY = GameConstants.PADDING_PANEL_DOWN_INVENTORY;

        // draw background and border
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        shapeRenderer.rect(panelX, panelY, totalWidth, panelHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.8f, 1);
        shapeRenderer.rect(panelX, panelY, totalWidth, panelHeight);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float currentX = panelX + padding;
        for (InventoryItem invItem : inventoryItems.values()) {
            if (invItem == null || invItem.icon == null) continue;

            float iconSize = GameConstants.ICON_SIZE_INVENTORY;
            float iconX = currentX + (itemWidth - iconSize) / 2;
            float iconY = panelY + padding + (itemHeight - iconSize) / 2 + GameConstants.PADDING_FOR_ICON_INVENTORY;
            batch.draw(invItem.icon, iconX, iconY, iconSize, iconSize);

            if (invItem.count >= 0) {
                String countText = "" + invItem.count;
                glyphLayout.setText(countFont, countText);
                countFont.draw(batch, countText, currentX + (itemWidth - glyphLayout.width) / 2,
                    panelY + padding + 50);
            }
            glyphLayout.setText(font, invItem.name);
            font.draw(batch, invItem.name, currentX + (itemWidth - glyphLayout.width) / 2, panelY + padding + GameConstants.PADDING_FOR_NAME_INVENTORY
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
