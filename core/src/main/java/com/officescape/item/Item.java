package com.officescape.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.officescape.GameConstants;
import com.officescape.TiledGraph;
import com.officescape.unit.Player;

public abstract class Item {
    public Sprite sprite;
    public boolean isTaken = false;
    protected float scale;
    public boolean isUsed = false;

    public Item(String texturePath, float x, float y, float scale) {
        Texture texture = new Texture(texturePath);
        sprite = new Sprite(texture);
        this.scale = scale;
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }


    public boolean take(Player player) {
        if (isTaken) return false;

        float distance = Vector2.dst(getX(), getY(), player.getX(), player.getY());
        if (distance < GameConstants.ITEM_PICKUP_RANGE &&
            !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            )) {
            isTaken = true;
            return true;
        }
        return false;
    }


    public void draw(SpriteBatch batch) {
        if (!isTaken) {
            sprite.draw(batch);
        }
    }

    public void drawHighlight(ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(
            sprite.getX() - 2,
            sprite.getY() - 2,
            sprite.getWidth() + 4,
            sprite.getHeight() + 4
        );
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

    public boolean canBeTaken(Player player) {
        return this instanceof TakeableItem && !isTaken && !isUsed &&
            Vector2.dst(getX(), getY(), player.getX(), player.getY()) < GameConstants.ITEM_HIGHLIGHT_RANGE &&
            !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            );
    }

    public boolean canBeThrown(Player player) {
        return this instanceof ThrowableItem && isTaken && !isUsed;
    }

    public boolean canBeHidden(Player player) {
        return this instanceof HideableItem && !player.isPlayerHidden &&
            Vector2.dst(getX(), getY(), player.getX(), player.getY()) < GameConstants.HIDE_DISTANCE &&
            !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            );
    }
}
