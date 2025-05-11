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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;
import com.officescape.ManhattanDistance;
import com.officescape.TiledGraph;

public abstract class Character {
    private final Sprite sprite;
    private float speed = GameConstants.PLAYER_DEFAULT_SPEED;
    private final Rectangle collisionBox;
    private float scale = GameConstants.PLAYER_DEFAULT_SCALE;

    // Для анимации
    private TextureRegion idleTexture;
    private TextureRegion stepTexture;
    private float animationTimer = 0;
    private float stepDuration = GameConstants.STEP_DURATION;
    private boolean isMirrored = false;
    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;

    // Для A* пути
    private GraphPath<Vector2> path;
    private int currentPathIndex;
    private boolean isMovingToTarget = false;
    private TiledGraph mapGraph;
    private IndexedAStarPathFinder<Vector2> pathFinder;
    private final Heuristic<Vector2> heuristic;
    private Vector2 targetPosition;
    private final float collisionWidth;
    private final float collisionHeight;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Character(String texturePath) {
        // Загружаем текстуры для анимации
        // Texture texture = new Texture(Gdx.files.internal(texturePath));
        Texture texture = new Texture(Gdx.files.internal(texturePath.replace("_idle", "_go")));
        idleTexture = new TextureRegion(texture);

        // Предполагаем, что текстура шага имеет то же имя с суффиксом "_go"
        String stepTexturePath = texturePath.replace("_idle", "_go");
        stepTexture = new TextureRegion(new Texture(Gdx.files.internal(stepTexturePath)));

        sprite = new Sprite(idleTexture);
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(GameConstants.PLAYER_START_X, GameConstants.PLAYER_START_Y);

        this.collisionWidth = sprite.getWidth() * GameConstants.PLAYER_COLLISION_RATIO;
        this.collisionHeight = sprite.getHeight() * GameConstants.PLAYER_COLLISION_RATIO;
        this.collisionBox = new Rectangle(
            sprite.getX() + (sprite.getWidth() - collisionWidth) / 2,
            sprite.getY() + (sprite.getHeight() - collisionHeight) / 2,
            collisionWidth,
            collisionHeight
        );
        this.heuristic = new ManhattanDistance();
    }

    public float getCollisionWidth() {
        return collisionWidth;
    }

    public float getCollisionHeight() {
        return collisionHeight;
    }

    public GraphPath<Vector2> getCurrentPath() {
        return path;
    }


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
            updateCollisionBox();
        }

        // Сбрасываем состояние движения перед обработкой
        isMoving = false;
        float moveX = 0, moveY = 0;

        if (isMovingToTarget && path != null && currentPathIndex < path.getCount()) {
            Vector2 target = path.get(currentPathIndex);
            float dx = target.x - getX();
            float dy = target.y - getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < GameConstants.PATH_REACH_THRESHOLD) {
                currentPathIndex++;
                if (currentPathIndex >= path.getCount()) {
                    float finalDx = targetPosition.x - getX();
                    float finalDy = targetPosition.y - getY();
                    float finalDistance = (float) Math.sqrt(finalDx * finalDx + finalDy * finalDy);

                    if (finalDistance < GameConstants.PATH_REACH_THRESHOLD) {
                        isMovingToTarget = false;
                    } else {
                        moveX = (finalDx / finalDistance) * speed * deltaTime;
                        moveY = (finalDy / finalDistance) * speed * deltaTime;
                        isMoving = true;
                        updateDirection(moveX, moveY);
                    }
                    return;
                }
                target = path.get(currentPathIndex);
                dx = target.x - getX();
                dy = target.y - getY();
                distance = (float) Math.sqrt(dx * dx + dy * dy);
            }

            if (distance > 0) {
                moveX = (dx / distance) * speed * deltaTime;
                moveY = (dy / distance) * speed * deltaTime;
                isMoving = true;
                updateDirection(dx, dy);
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

            if (moveX != 0 || moveY != 0) {
                isMoving = true;
                updateDirection(moveX, moveY);

                // Нормализуем вектор движения
                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                moveX = (moveX / length) * speed * deltaTime;
                moveY = (moveY / length) * speed * deltaTime;
            }
        }

        // Обработка движения и коллизий
        if (isMoving) {
            float oldX = sprite.getX();
            float oldY = sprite.getY();

            // Двигаемся
            sprite.translate(moveX, moveY);
            updateCollisionBox();

            if (checkCollisions(walls)) {
                // Обработка коллизий (как в оригинальном коде)
                sprite.setPosition(oldX, oldY);
                sprite.translate(moveX, 0);
                updateCollisionBox();
                boolean xCollision = checkCollisions(walls);

                sprite.setPosition(oldX, oldY);
                sprite.translate(0, moveY);
                updateCollisionBox();
                boolean yCollision = checkCollisions(walls);

                if (!xCollision && !yCollision) {
                    sprite.setPosition(oldX + moveX, oldY + moveY);
                } else if (!xCollision) {
                    sprite.setPosition(oldX + moveX, oldY);
                } else if (!yCollision) {
                    sprite.setPosition(oldX, oldY + moveY);
                } else {
                    sprite.setPosition(oldX, oldY);
                    isMoving = false;
                }
                updateCollisionBox();
            }

            // Обновляем анимацию только при движении
            updateAnimation(deltaTime);
        } else {
            // Если не движемся, сбрасываем анимацию в idle
            resetAnimation();
        }
    }

    private void updateDirection(float dx, float dy) {
        // Определяем основное направление движения
        if (Math.abs(dx) > Math.abs(dy)) {
            currentDirection = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = dy > 0 ? Direction.UP : Direction.DOWN;
        }
    }

    private void updateAnimation(float deltaTime) {
        animationTimer += deltaTime;

        if (animationTimer >= stepDuration) {
            animationTimer = 0;
            isMirrored = !isMirrored; // Переключаем зеркальное отражение
        }

        // Выбираем текущий кадр анимации
        TextureRegion currentFrame;
        if (animationTimer < stepDuration / 2) {
            currentFrame = stepTexture;
        } else {
            currentFrame = idleTexture;
        }

        // Сбрасываем все трансформации перед применением новых
        sprite.setFlip(false, false);
        sprite.setRotation(0);

        // Применяем трансформации в зависимости от направления
        switch (currentDirection) {
            case UP:
                sprite.setRegion(currentFrame);
                break;
            case DOWN:
                sprite.setRegion(currentFrame);
                sprite.setFlip(false, true); // Отражаем по вертикали
                break;
            case LEFT:
                sprite.setRegion(currentFrame);
                sprite.rotate(90); // Отражаем по горизонтали
                break;
            case RIGHT:
                sprite.setRegion(currentFrame);
                sprite.rotate(-90);
                break;
        }

        // Применяем зеркальное отражение для второго шага
        if (isMirrored) {
            sprite.flip(true, false);
        }
    }

    private void resetAnimation() {
        animationTimer = 0;
        isMirrored = false;
        sprite.setRegion(idleTexture);
        sprite.setFlip(false, false);
        sprite.setRotation(0);

        // Применяем трансформации для idle-анимации
        switch (currentDirection) {
            case UP:
                // Без изменений
                break;
            case DOWN:
                sprite.setFlip(false, true);
                break;
            case LEFT:
                sprite.setFlip(true, false);
                sprite.rotate(90); // Добавляем поворот для LEFT
                break;
            case RIGHT:
                sprite.rotate(-90); // Добавляем поворот для RIGHT
                break;
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
