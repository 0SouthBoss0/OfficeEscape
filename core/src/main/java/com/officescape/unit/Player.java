package com.officescape.unit;

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
import com.officescape.GameConstants;
import com.officescape.ManhattanDistance;
import com.officescape.TiledGraph;

public class Player {
    private Sprite sprite;
    private float speed = GameConstants.PLAYER_DEFAULT_SPEED;
    private Rectangle collisionBox;
    private float scale = GameConstants.PLAYER_DEFAULT_SCALE;

    // Для A* пути
    private GraphPath<Vector2> path;
    private int currentPathIndex;
    private boolean isMovingToTarget = false;
    private TiledGraph mapGraph;
    private IndexedAStarPathFinder<Vector2> pathFinder;
    private Heuristic<Vector2> heuristic;
    private Vector2 targetPosition;

    public float getCollisionWidth() {
        return collisionWidth;
    }

    public float getCollisionHeight() {
        return collisionHeight;
    }

    private float collisionWidth;
    private float collisionHeight;

    public Player(String texturePath) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(GameConstants.PLAYER_START_X, GameConstants.PLAYER_START_Y);

        // Размеры коллизии должны быть немного меньше спрайта
        this.collisionWidth = sprite.getWidth() * GameConstants.PLAYER_COLLISION_RATIO;
        this.collisionHeight = sprite.getHeight() * GameConstants.PLAYER_COLLISION_RATIO;
        this.collisionBox = new Rectangle(
            sprite.getX() + (sprite.getWidth() - collisionWidth) / 2,
            sprite.getY() + (sprite.getHeight() - collisionHeight) / 2,
            collisionWidth,
            collisionHeight
        );

        //this.mapGraph = new TiledGraph(20, 20, 32f, walls, this.collisionWidth, this.collisionHeight);
        //this.pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
        this.heuristic = new ManhattanDistance();
    }

    public GraphPath<Vector2> getCurrentPath() {
        return path;
    }

    // И измените метод goToCoords:
    public void goToCoords(float targetX, float targetY) {
        this.targetPosition = new Vector2(targetX, targetY);
        Vector2 start = findNearestNode(getX(), getY());
        Vector2 target = findNearestNode(targetX, targetY);

        if (start == null || target == null) {
            isMovingToTarget = false;
            path = null; // Очищаем путь при невозможности построения
            return;
        }

        path = new DefaultGraphPath<>();
        boolean pathFound = pathFinder.searchNodePath(start, target, heuristic, path);

        if (pathFound && path.getCount() > 0) {
            currentPathIndex = 0;
            isMovingToTarget = true;
        } else {
            isMovingToTarget = false;
            path = null; // Очищаем путь если не найден
        }
    }

    public void updateGraphWithNewWalls() {
        this.mapGraph = TiledGraph.getInstance();
        this.pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
    }

    private Vector2 findNearestNode(float x, float y) {
        // Преобразуем координаты клика в координаты узла
        int nodeX = Math.round((x - mapGraph.getTileSize() / 2) / mapGraph.getTileSize());
        int nodeY = Math.round((y - mapGraph.getTileSize() / 2) / mapGraph.getTileSize());

        // Проверяем сначала точное попадание
        Vector2 exactNode = mapGraph.getNodeAt(nodeX, nodeY);
        if (exactNode != null && !mapGraph.isWall(exactNode, this.collisionWidth, this.collisionHeight)) {
            return exactNode;
        }

        // Если точный узел недоступен, ищем ближайший
        Vector2 bestNode = null;
        float bestDistance = Float.MAX_VALUE;

        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                Vector2 node = mapGraph.getNodeAt(nodeX + dx, nodeY + dy);
                if (node != null && !mapGraph.isWall(node, this.collisionWidth, this.collisionHeight)) {
                    float dist = Vector2.dst2(x, y, node.x, node.y);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestNode = node;
                    }
                }
            }
        }
        return bestNode;
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

            if (distance < GameConstants.PATH_REACH_THRESHOLD) {
                currentPathIndex++;
                if (currentPathIndex >= path.getCount()) {
                    // Убедимся, что персонаж достигает именно конечной точки, а не последнего узла пути
                    float finalDx = targetPosition.x - getX();
                    float finalDy = targetPosition.y - getY();
                    float finalDistance = (float) Math.sqrt(finalDx * finalDx + finalDy * finalDy);

                    if (finalDistance < GameConstants.PATH_REACH_THRESHOLD) {
                        isMovingToTarget = false;
                    } else {
                        // Двигаемся напрямую к целевой точке, если она близко
                        float step = speed * deltaTime;
                        sprite.translate((finalDx / finalDistance) * step, (finalDy / finalDistance) * step);
                        updateCollisionBox();
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
                    float step = Math.min(GameConstants.STEP_THRESHOLD, remainingDistance); // Маленький шаг
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
            float oldX = sprite.getX();
            float oldY = sprite.getY();
            float moveX = 0, moveY = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

            if (moveX != 0 || moveY != 0) {
                // Нормализуем вектор движения
                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                moveX /= length;
                moveY /= length;

                // Пробуем двигаться по диагонали
                sprite.translate(moveX * speed * deltaTime, moveY * speed * deltaTime);
                updateCollisionBox();

                if (checkCollisions(walls)) {
                    // Если коллизия, пробуем двигаться только по X
                    sprite.setPosition(oldX, oldY);
                    sprite.translate(moveX * speed * deltaTime, 0);
                    updateCollisionBox();
                    boolean xCollision = checkCollisions(walls);

                    // Пробуем двигаться только по Y
                    sprite.setPosition(oldX, oldY);
                    sprite.translate(0, moveY * speed * deltaTime);
                    updateCollisionBox();
                    boolean yCollision = checkCollisions(walls);

                    // Применяем движение по свободной оси
                    if (!xCollision && !yCollision) {
                        // Если оба направления свободны (маловероятно), оставляем как есть
                        sprite.setPosition(oldX + moveX * speed * deltaTime, oldY + moveY * speed * deltaTime);
                    } else if (!xCollision) {
                        // Двигаемся только по X
                        sprite.setPosition(oldX + moveX * speed * deltaTime, oldY);
                    } else if (!yCollision) {
                        // Двигаемся только по Y
                        sprite.setPosition(oldX, oldY + moveY * speed * deltaTime);
                    } else {
                        // Оба направления заблокированы - не двигаемся
                        sprite.setPosition(oldX, oldY);
                    }
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
            sprite.getY() + (sprite.getHeight() - collisionHeight) / 2,
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

}

