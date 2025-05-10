package com.officescape;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledGraph implements IndexedGraph<Vector2> {
    private static TiledGraph instance;

    private Array<Vector2> nodes;
    private Array<Connection<Vector2>> connections;
    private int width;
    private int height;
    private float tileSize;
    private Array<Rectangle> walls;
    private float playerWidth;
    private float playerHeight;

    private TiledGraph() {
    }

    public static synchronized void init(int width, int height, float tileSize,
                                         Array<Rectangle> walls, float playerWidth,
                                         float playerHeight) {
        if (instance != null) {
            throw new IllegalStateException("TiledGraph already initialized!");
        }
        instance = new TiledGraph();
        instance.width = width;
        instance.height = height;
        instance.tileSize = tileSize;
        instance.walls = walls;
        instance.playerWidth = playerWidth;
        instance.playerHeight = playerHeight;
        instance.nodes = new Array<>(width * height);
        instance.connections = new Array<>();

        // Создаем узлы
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2 node = new Vector2(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2);
                instance.nodes.add(node);
            }
        }

        instance.buildConnections();
    }

    public static synchronized TiledGraph getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TiledGraph is not initialized! Call init() first.");
        }
        return instance;
    }

    public float getTileSize() {
        return tileSize;
    }

    private void buildConnections() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2 node = getNodeAt(x, y);
                if (node == null || isWall(node)) continue;

                // Проверяем соседей
                checkAndAddConnection(node, x, y, x + 1, y);
                checkAndAddConnection(node, x, y, x - 1, y);
                checkAndAddConnection(node, x, y, x, y + 1);
                checkAndAddConnection(node, x, y, x, y - 1);

                // Диагональные соединения
                if (isValidConnection(x, y, x + 1, y + 1)) {
                    checkAndAddConnection(node, x, y, x + 1, y + 1);
                }
                if (isValidConnection(x, y, x - 1, y + 1)) {
                    checkAndAddConnection(node, x, y, x - 1, y + 1);
                }
                if (isValidConnection(x, y, x + 1, y - 1)) {
                    checkAndAddConnection(node, x, y, x + 1, y - 1);
                }
                if (isValidConnection(x, y, x - 1, y - 1)) {
                    checkAndAddConnection(node, x, y, x - 1, y - 1);
                }
            }
        }
    }

    private boolean isValidConnection(int fromX, int fromY, int toX, int toY) {
        return getNodeAt(toX, toY) != null && !isWall(getNodeAt(toX, toY)) &&
            getNodeAt(fromX, toY) != null && !isWall(getNodeAt(fromX, toY)) &&
            getNodeAt(toX, fromY) != null && !isWall(getNodeAt(toX, fromY));
    }

    public boolean isWall(Vector2 node) {
        Rectangle nodeRect = new Rectangle(
            node.x - playerWidth / 2 * GameConstants.IS_WALL,
            node.y - playerHeight / 2 * GameConstants.IS_WALL,
            playerWidth * GameConstants.IS_WALL,
            playerHeight * GameConstants.IS_WALL
        );

        for (Rectangle wall : walls) {
            if (wall.overlaps(nodeRect)) {
                return true;
            }
        }
        return false;
    }

    private void checkAndAddConnection(Vector2 from, int fromX, int fromY, int toX, int toY) {
        if (toX < 0 || toX >= width || toY < 0 || toY >= height) return;

        Vector2 to = getNodeAt(toX, toY);
        if (to != null && !isWall(to) && !hasWallBetween(from, to)) {
            connections.add(new DefaultConnection<>(from, to));
        }
    }

    private boolean hasWallBetween(Vector2 from, Vector2 to) {
        float step = Math.min(playerWidth, playerHeight) / GameConstants.NODE_SEARCH_RADIUS;
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return false;
        dx /= distance;
        dy /= distance;

        for (float t = 0; t <= distance; t += step) {
            float x = from.x + dx * t;
            float y = from.y + dy * t;
            Rectangle playerBox = new Rectangle(
                x - playerWidth / 2,
                y - playerHeight / 2,
                playerWidth,
                playerHeight
            );

            for (Rectangle wall : walls) {
                if (playerBox.overlaps(wall)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Vector2 getNodeAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return nodes.get(y * width + x);
    }

    @Override
    public int getIndex(Vector2 node) {
        int x = (int) (node.x / tileSize);
        int y = (int) (node.y / tileSize);
        return y * width + x;
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromNode) {
        Array<Connection<Vector2>> result = new Array<>();
        for (Connection<Vector2> connection : connections) {
            if (connection.getFromNode().equals(fromNode)) {
                result.add(connection);
            }
        }
        return result;
    }
}
