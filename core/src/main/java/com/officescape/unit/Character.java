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
    private final Rectangle collisionBox;
    private float scale = GameConstants.PLAYER_DEFAULT_SCALE;

    //  animation
    private final TextureRegion idleTexture;
    private final TextureRegion stepTexture;
    private float animationTimer = 0;
    private boolean isMirrored = false;

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    private Direction currentDirection = Direction.DOWN;

    // for a* algo
    private GraphPath<Vector2> path;
    private int currentPathIndex;
    public boolean isMovingToTarget = false;
    private TiledGraph mapGraph;
    private IndexedAStarPathFinder<Vector2> pathFinder;
    private final Heuristic<Vector2> heuristic;
    private Vector2 targetPosition;
    private final float collisionWidth;
    private final float collisionHeight;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }
    private Direction finalDirection = null;

    public Character(String texturePath, int x, int y) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        idleTexture = new TextureRegion(texture);

        String stepTexturePath = texturePath.replace("_idle", "_go");
        stepTexture = new TextureRegion(new Texture(Gdx.files.internal(stepTexturePath)));

        sprite = new Sprite(idleTexture);
        sprite.setSize(texture.getWidth() * scale, texture.getHeight() * scale);
        sprite.setPosition(x, y);

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

    public void goToCoords(float targetX, float targetY, Direction direction) {
        this.goToCoords(targetX, targetY); // используем существующую логику движения
        this.finalDirection = direction; // сохраняем конечное направление
    }

    public void goToCoords(float targetX, float targetY) {
        this.targetPosition = new Vector2(targetX, targetY);
        Vector2 start = findNearestNode(getX(), getY());
        Vector2 target = findNearestNode(targetX, targetY);

        if (start == null || target == null) {
            isMovingToTarget = false;
            path = null;
            return;
        }

        path = new DefaultGraphPath<>();
        boolean pathFound = pathFinder.searchNodePath(start, target, heuristic, path);

        if (pathFound && path.getCount() > 0) {
            currentPathIndex = 0;
            isMovingToTarget = true;
        } else {
            isMovingToTarget = false;
            path = null;
        }
    }

    public void updateGraphWithNewWalls() {
        this.mapGraph = TiledGraph.getInstance();
        this.pathFinder = new IndexedAStarPathFinder<>(mapGraph, true);
    }

    private Vector2 findNearestNode(float x, float y) {
        // convert click coordinates to node coordinates
        int nodeX = Math.round((x - mapGraph.getTileSize() / 2) / mapGraph.getTileSize());
        int nodeY = Math.round((y - mapGraph.getTileSize() / 2) / mapGraph.getTileSize());

        // check for exact
        Vector2 exactNode = mapGraph.getNodeAt(nodeX, nodeY);
        if (exactNode != null &&
            !mapGraph.isWall(exactNode, this.collisionWidth, this.collisionHeight) &&
            !mapGraph.isFurniture(exactNode, this.collisionWidth, this.collisionHeight)) {
            return exactNode;
        }

        // check for closest
        Vector2 bestNode = null;
        float bestDistance = Float.MAX_VALUE;

        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                Vector2 node = mapGraph.getNodeAt(nodeX + dx, nodeY + dy);
                if (node != null &&
                    !mapGraph.isWall(node, this.collisionWidth, this.collisionHeight) &&
                    !mapGraph.isFurniture(node, this.collisionWidth, this.collisionHeight)) {
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


    public void update(float deltaTime, Array<Rectangle> walls) {
        if (collisionBox == null) {
            updateCollisionBox();
        }

        boolean isMoving = false;
        float moveX = 0, moveY = 0;

        float speed = GameConstants.PLAYER_DEFAULT_SPEED;
        if (isMovingToTarget && path != null && currentPathIndex < path.getCount()) {
            Vector2 target = path.get(currentPathIndex);
            float dx = target.x - getX();
            float dy = target.y - getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < GameConstants.PATH_REACH_THRESHOLD) {
                currentPathIndex++;
                if (currentPathIndex >= path.getCount()) {
                    isMovingToTarget = false;
                    if (finalDirection != null) {
                        currentDirection = finalDirection;
                        finalDirection = null;
                    } else {
                        float finalDx = targetPosition.x - getX();
                        float finalDy = targetPosition.y - getY();
                        updateDirection(finalDx, finalDy);
                    }


                    float finalDistance = Vector2.dst(getX(), getY(), targetPosition.x, targetPosition.y);
                    if (finalDistance < GameConstants.PATH_REACH_THRESHOLD) {
                        isMovingToTarget = false;
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
        } else if (this instanceof Player) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

            if (moveX != 0 || moveY != 0) {
                isMoving = true;
                updateDirection(moveX, moveY);

                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                moveX = (moveX / length) * speed * deltaTime;
                moveY = (moveY / length) * speed * deltaTime;
            }
        }

        if (isMoving) {
            float oldX = sprite.getX();
            float oldY = sprite.getY();

            sprite.translate(moveX, moveY);
            updateCollisionBox();

            if (checkCollisions(walls)) {
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
                }
                updateCollisionBox();
            }
            updateAnimation(deltaTime);
        } else {
            resetAnimation();
        }

    }

    private void updateDirection(float dx, float dy) {
        // find main direction
        if (Math.abs(dx) > Math.abs(dy)) {
            currentDirection = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = dy > 0 ? Direction.UP : Direction.DOWN;
        }
    }

    private void updateAnimation(float deltaTime) {
        animationTimer += deltaTime;

        float stepDuration = GameConstants.STEP_DURATION;
        if (animationTimer >= stepDuration) {
            animationTimer = 0;
            isMirrored = !isMirrored;
        }

        TextureRegion currentFrame;
        if (animationTimer < stepDuration / 2) {
            currentFrame = stepTexture;
        } else {
            currentFrame = idleTexture;
        }

        sprite.setFlip(false, false);
        sprite.setRotation(0);

        switch (currentDirection) {
            case UP -> sprite.setRegion(currentFrame);
            case DOWN -> {
                sprite.setRegion(currentFrame);
                sprite.setFlip(false, true);
            }
            case LEFT -> {
                sprite.setRegion(currentFrame);
                sprite.rotate(90);
            }
            case RIGHT -> {
                sprite.setRegion(currentFrame);
                sprite.rotate(-90);
            }
        }

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
        switch (currentDirection) {
            case UP:
                break;
            case DOWN:
                sprite.setFlip(false, true);
                break;
            case LEFT:
                sprite.setFlip(true, false);
                sprite.rotate(90);
                break;
            case RIGHT:
                sprite.rotate(-90);
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

        TiledGraph graph = TiledGraph.getInstance();
        if (graph != null) {
            Vector2 center = new Vector2(collisionBox.x + collisionBox.width / 2, collisionBox.y + collisionBox.height / 2);
            return graph.isFurniture(center, collisionBox.width, collisionBox.height);
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

    public void setScale(float scale) {
        this.scale = scale;
        sprite.setSize(
            sprite.getTexture().getWidth() * scale,
            sprite.getTexture().getHeight() * scale
        );
        updateCollisionBox();
    }

    public boolean canSee(Character other, Array<Rectangle> walls) {
        // check distance
        float distance = Vector2.dst(getX(), getY(), other.getX(), other.getY());
        if (distance > GameConstants.NPC_DETECTION_RANGE) {
            return false;
        }

        // check direction
        Vector2 npcToPlayer = new Vector2(other.getX() - getX(), other.getY() - getY()).nor();
        Vector2 npcFacingDirection = getFacingDirection();

        // check angle
        float angle = npcFacingDirection.angleDeg(npcToPlayer);
        if (Math.abs(angle) > GameConstants.NPC_FOV_ANGLE / 2f) {
            return false;
        }

        // check walls
        TiledGraph graph = TiledGraph.getInstance();
        Vector2 start = new Vector2(getX(), getY());
        Vector2 end = new Vector2(other.getX(), other.getY());

        return !graph.hasWallBetween(start, end);
    }

    public Vector2 getFacingDirection() {
        return switch (currentDirection) {
            case UP -> new Vector2(0, 1);
            case DOWN -> new Vector2(0, -1);
            case LEFT -> new Vector2(-1, 0);
            case RIGHT -> new Vector2(1, 0);
        };
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

    public float getCollisionWidth() {
        return collisionWidth;
    }

    public float getCollisionHeight() {
        return collisionHeight;
    }

    public GraphPath<Vector2> getCurrentPath() {
        return path;
    }
}
