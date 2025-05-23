package com.officescape.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;
import com.officescape.TiledGraph;
import com.officescape.unit.Player;

public abstract class Item {
    public Sprite sprite;
    protected boolean isHighlighted = false;
    public boolean isPickedUp = false;
    protected float scale;

    public Item(String texturePath, float x, float y, float scale) {
        Texture texture = new Texture(texturePath);
        sprite = new Sprite(texture);
        this.scale = scale;
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }

    public void update(Player player) {
        if (isPickedUp) return;

        // get distance to player
        float distance = Vector2.dst(getX(), getY(), player.getX(), player.getY());

        // check if player can pick up
        if (distance < GameConstants.ITEM_HIGHLIGHT_RANGE) {
            isHighlighted = !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            );
        } else {
            isHighlighted = false;
        }
    }

    public boolean pickUp(Player player) {
        if (isPickedUp) return false;

        float distance = Vector2.dst(getX(), getY(), player.getX(), player.getY());
        if (distance < GameConstants.ITEM_PICKUP_RANGE &&
            !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            )) {
            isPickedUp = true;
            onPickUp(player);
            return true;
        }
        return false;
    }

    public abstract void onPickUp(Player player);

    public void draw(SpriteBatch batch) {
        if (!isPickedUp) {
            sprite.draw(batch);
        }
    }

    public void drawHighlight(ShapeRenderer shapeRenderer) {
        if (isHighlighted && !isPickedUp) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 1); // Белый цвет
            shapeRenderer.rect(
                sprite.getX() - 2,
                sprite.getY() - 2,
                sprite.getWidth() + 4,
                sprite.getHeight() + 4
            );
        }
    }

    public float getX() {
        return sprite.getX() + sprite.getWidth() / 2;
    }

    public float getY() {
        return sprite.getY() + sprite.getHeight() / 2;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}
