package com.officescape.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.officescape.GameConstants;
import com.officescape.InventoryPanel;
import com.officescape.TiledGraph;
import com.officescape.unit.Character;
import com.officescape.unit.NPC;
import com.officescape.unit.Player;

import java.util.Random;

public abstract class Item {
    public Sprite sprite;
    public boolean isTaken = false;
    protected float scale;
    public boolean isUsed = false;
    boolean isBroken = false;
    private static final Random random = new Random();


    public Item(String texturePath, float x, float y, float scale, Character.Direction direction) {
        Texture texture = new Texture(texturePath);
        sprite = new Sprite(texture);
        this.scale = scale;
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        switch (direction) {
            case UP:
                break;
            case DOWN:
                sprite.rotate(180);
                break;
            case LEFT:
                sprite.rotate(90);
                break;
            case RIGHT:
                sprite.rotate(-90);
                break;
        }
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
        float[] vertices = sprite.getVertices();

        float x1 = vertices[0], y1 = vertices[1];
        float x2 = vertices[5], y2 = vertices[6];
        float x3 = vertices[10], y3 = vertices[11];
        float x4 = vertices[15], y4 = vertices[16];

        shapeRenderer.line(x1, y1, x2, y2);
        shapeRenderer.line(x2, y2, x3, y3);
        shapeRenderer.line(x3, y3, x4, y4);
        shapeRenderer.line(x4, y4, x1, y1);
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
        return this instanceof BreakableItem && !isBroken && NPC.currentBrokenItem == null && isCloserThanToPlayer(player, GameConstants.BROKE_DISTANCE);
    }

    public boolean canBeUsed(Player player, ObjectMap<Class<? extends Item>, InventoryPanel.InventoryItem> items) {
        return this instanceof UsableItem && !isUsed && validateUsage(items) && isCloserThanToPlayer(player, GameConstants.BROKE_DISTANCE);
    }

    public boolean validateUsage(ObjectMap<Class<? extends Item>, InventoryPanel.InventoryItem> items) {
        if (this instanceof ButtonServer) {
            return (items.get(Flash.class).count > 0);
        }
        if (this instanceof TurnStile) {
            return (items.get(KeyCard.class).count > 0);
        }
        return false;
    }

    public Vector2 getPosition() {
        TiledGraph graph = TiledGraph.getInstance();

        // Начинаем с минимального расстояния и постепенно увеличиваем его
        float currentDistance = GameConstants.MIN_APPROACH_DISTANCE;

        while (true) {
            // Пробуем несколько случайных углов для текущего расстояния
            for (int i = 0; i < 360; i += 10) { // Проверяем каждые 10 градусов
                float angle = i + random.nextFloat() * 10f; // Добавляем небольшую случайность
                float x = getX() + (float) Math.cos(Math.toRadians(angle)) * currentDistance;
                float y = getY() + (float) Math.sin(Math.toRadians(angle)) * currentDistance;
                Vector2 position = new Vector2(x, y);

                if (!graph.isWall(position, GameConstants.MAX_PLAYER_WIDTH, GameConstants.MAX_PLAYER_HEIGHT) &&
                    !graph.isFurniture(position, GameConstants.MAX_PLAYER_WIDTH, GameConstants.MAX_PLAYER_HEIGHT)) {
                    return position; // Возвращаем первую найденную валидную позицию
                }
            }

            // Увеличиваем расстояние для следующей итерации
            currentDistance += (GameConstants.MAX_APPROACH_DISTANCE - GameConstants.MIN_APPROACH_DISTANCE) / 10f;

            // Если превысили максимальное расстояние, начинаем с минимального снова
            if (currentDistance > GameConstants.MAX_APPROACH_DISTANCE) {
                currentDistance = GameConstants.MIN_APPROACH_DISTANCE;
            }
        }
    }
}
