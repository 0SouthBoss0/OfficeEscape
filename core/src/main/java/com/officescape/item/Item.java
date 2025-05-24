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
    boolean isBroken = false;


    public Item(String texturePath, float x, float y, float scale) {
        Texture texture = new Texture(texturePath);
        sprite = new Sprite(texture);
        this.scale = scale;
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }


    public void take(Player player) {
        isTaken = canBeTaken(player);
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

    private boolean isCloserThanToPlayer(Player player, float distance) {
        return Vector2.dst(getX(), getY(), player.getX(), player.getY()) < distance &&
            !TiledGraph.getInstance().hasWallBetween(
                new Vector2(player.getX(), player.getY()),
                new Vector2(getX(), getY())
            );
    }

    public boolean canBeTaken(Player player) {
        return this instanceof TakeableItem && !isTaken && !isUsed && isCloserThanToPlayer(player, GameConstants.ITEM_PICKUP_RANGE);
    }

    public boolean canBeThrown(Player player) {
        return this instanceof ThrowableItem && isTaken && !player.isHidden;
    }

    public boolean canBeHidden(Player player) {
        return this instanceof HideableItem && !player.isHidden && isCloserThanToPlayer(player, GameConstants.HIDE_DISTANCE);
    }

    public boolean canBeBroken(Player player) {
        return this instanceof BreakableItem && !isBroken && isCloserThanToPlayer(player, GameConstants.BROKE_DISTANCE);
    }
}
