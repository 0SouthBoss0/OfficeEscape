package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    private Sprite sprite;
    private float speed = 150f;
    private Rectangle collisionBox;
    private float scale = 0.1f;

    // Для A* пути
    private GraphPath<Vector2> path;
    private int currentPathIndex;
    private boolean isMovingToTarget = false;
    private TiledGraph mapGraph;
    private IndexedAStarPathFinder<Vector2> pathFinder;
    private Heuristic<Vector2> heuristic;
    private Vector2 targetPosition;
    private float collisionWidth;
    private float collisionHeight;

    public Player(String texturePath, Array<Rectangle> walls) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(100, 200);

        // Размеры коллизии должны быть немного меньше спрайта
        this.collisionWidth = sprite.getWidth() * 0.45f;
        this.collisionHeight = sprite.getHeight() * 0.45f;
        this.collisionBox = new Rectangle(
            sprite.getX() + (sprite.getWidth() - collisionWidth) / 2,
            sprite.getY() + sprite.getHeight() * 0.2f,
            collisionWidth,
            collisionHeight
        );

        this.mapGraph = new TiledGraph(20, 20, 32f, walls, this.collisionWidth, this.collisionHeight);
        this.pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
        this.heuristic = new ManhattanDistanceHeuristic();
    }

    public void goToCoords(float targetX, float targetY) {
        this.targetPosition = new Vector2(targetX, targetY);
        Vector2 start = findNearestNode(getX(), getY());
        Vector2 target = findNearestNode(targetX, targetY);

        if (start == null || target == null) {
            isMovingToTarget = false;
            return;
        }

        path = new DefaultGraphPath<>();
        pathFinder.searchNodePath(start, target, heuristic, path);

        if (path.getCount() > 0) {
            currentPathIndex = 0;
            isMovingToTarget = true;
        } else {
            isMovingToTarget = false;
        }
    }

    public void updateGraphWithNewWalls(Array<Rectangle> walls) {
        this.mapGraph = new TiledGraph(20, 20, 32f, walls, this.collisionWidth, this.collisionHeight);
        this.pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
    }

    private Vector2 findNearestNode(float x, float y) {
        int nodeX = (int) (x / mapGraph.getTileSize());
        int nodeY = (int) (y / mapGraph.getTileSize());

        // Проверяем сначала точную позицию
        Vector2 node = mapGraph.getNodeAt(nodeX, nodeY);
        if (node != null && !mapGraph.isWall(node)) {
            return node;
        }

        // Если точная позиция стена, ищем ближайшую свободную
        int radius = 1;
        while (radius <= 3) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    if (Math.abs(dx) == radius || Math.abs(dy) == radius) {
                        node = mapGraph.getNodeAt(nodeX + dx, nodeY + dy);
                        if (node != null && !mapGraph.isWall(node)) {
                            return node;
                        }
                    }
                }
            }
            radius++;
        }
        return null;
    }

    public void setScale(float scale) {
        this.scale = scale;
        sprite.setSize(
            sprite.getTexture().getWidth() * scale,
            sprite.getTexture().getHeight() * scale
        );
        updateCollisionBox();
    }


    public void update(float deltaTime, Array<Rectangle> walls) {
        if (collisionBox == null) {
            updateCollisionBox(); // Инициализируем при первом вызове
        }
        if (isMovingToTarget && path != null && currentPathIndex < path.getCount()) {
            Vector2 target = path.get(currentPathIndex);
            float dx = target.x - getX();
            float dy = target.y - getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < 5f) {
                currentPathIndex++;
                if (currentPathIndex >= path.getCount()) {
                    float finalDistance = Vector2.dst(getX(), getY(), targetPosition.x, targetPosition.y);
                    if (finalDistance < 10f) {
                        isMovingToTarget = false;
                    } else {
                        goToCoords(targetPosition.x, targetPosition.y);
                    }
                    return;
                }
                target = path.get(currentPathIndex);
                dx = target.x - getX();
                dy = target.y - getY();
                distance = (float) Math.sqrt(dx * dx + dy * dy);
            }

            if (distance > 0) {
                dx /= distance;
                dy /= distance;

                float oldX = sprite.getX();
                float oldY = sprite.getY();
                float moveDistance = speed * deltaTime;

                // Двигаемся небольшими шагами с проверкой коллизий
                float remainingDistance = moveDistance;
                while (remainingDistance > 0) {
                    float step = Math.min(5f, remainingDistance); // Маленький шаг
                    sprite.translate(dx * step, dy * step);
                    updateCollisionBox();

                    if (checkCollisions(walls)) {
                        sprite.setPosition(oldX, oldY);
                        updateCollisionBox();
                        goToCoords(targetPosition.x, targetPosition.y);
                        return;
                    }

                    remainingDistance -= step;
                    oldX = sprite.getX();
                    oldY = sprite.getY();
                }
            }
        } else {
            // Старая логика WASD
            float oldX = sprite.getX();
            float oldY = sprite.getY();
            float moveX = 0, moveY = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

            if (moveX != 0 || moveY != 0) {
                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                moveX /= length;
                moveY /= length;

                sprite.translate(moveX * speed * deltaTime, moveY * speed * deltaTime);
                updateCollisionBox();

                if (checkCollisions(walls)) {
                    sprite.setPosition(oldX, oldY);
                    updateCollisionBox();
                }
            }
        }
    }

    private boolean checkCollisions(Array<Rectangle> walls) {
        if (walls == null || collisionBox == null) return false;

        for (Rectangle wall : walls) {
            if (collisionBox.overlaps(wall)) {
                return true;
            }
        }
        return false;
    }

    private void updateCollisionBox() {
        collisionBox.set(
            sprite.getX() + (sprite.getWidth() - collisionWidth) / 2,
            sprite.getY() + sprite.getHeight() * 0.2f,
            collisionWidth,
            collisionHeight
        );
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }

    public float getX() {
        return sprite.getX() + sprite.getWidth() / 2;
    }

    public float getY() {
        return sprite.getY() + sprite.getHeight() / 2;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }
}
