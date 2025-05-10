package com.officescape;

import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledGraph implements IndexedGraph<Vector2> {
    private Array<Vector2> nodes;
    private Array<Connection<Vector2>> connections;
    private int width, height;
    private float tileSize;
    private Array<Rectangle> walls;
    private float playerWidth;
    private float playerHeight;

    public TiledGraph(int width, int height, float tileSize, Array<Rectangle> walls, float collisionWidth, float collisionHeight) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.walls = walls;
        this.nodes = new Array<>(width * height);
        this.connections = new Array<>();
        this.playerWidth = collisionWidth;
        this.playerHeight = collisionHeight;

        // Создаем узлы и сразу помечаем стены
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2 node = new Vector2(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2);
                nodes.add(node);
            }
        }

        buildConnections();
    }

    public float getTileSize() {
        return tileSize;
    }

    private void buildConnections() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2 node = getNodeAt(x, y);
                if (node == null || isWall(node)) continue;

                // Проверяем соседей (4-направленные движения)
                checkAndAddConnection(node, x, y, x + 1, y); // правый
                checkAndAddConnection(node, x, y, x - 1, y); // левый
                checkAndAddConnection(node, x, y, x, y + 1); // верх
                checkAndAddConnection(node, x, y, x, y - 1); // низ

                // Диагональные движения только если оба ортогональных пути свободны
                if (getNodeAt(x + 1, y) != null && !isWall(getNodeAt(x + 1, y)) &&
                    getNodeAt(x, y + 1) != null && !isWall(getNodeAt(x, y + 1))) {
                    checkAndAddConnection(node, x, y, x + 1, y + 1); // верх-прав
                }
                if (getNodeAt(x - 1, y) != null && !isWall(getNodeAt(x - 1, y)) &&
                    getNodeAt(x, y + 1) != null && !isWall(getNodeAt(x, y + 1))) {
                    checkAndAddConnection(node, x, y, x - 1, y + 1); // верх-лев
                }
                if (getNodeAt(x + 1, y) != null && !isWall(getNodeAt(x + 1, y)) &&
                    getNodeAt(x, y - 1) != null && !isWall(getNodeAt(x, y - 1))) {
                    checkAndAddConnection(node, x, y, x + 1, y - 1); // низ-прав
                }
                if (getNodeAt(x - 1, y) != null && !isWall(getNodeAt(x - 1, y)) &&
                    getNodeAt(x, y - 1) != null && !isWall(getNodeAt(x, y - 1))) {
                    checkAndAddConnection(node, x, y, x - 1, y - 1); // низ-лев
                }
            }
        }
    }

    public boolean isWall(Vector2 node) {
        // Проверяем, попадает ли узел в стену с учетом размеров игрока
        Rectangle nodeRect = new Rectangle(
            node.x - playerWidth / 2 * 0.8f,
            node.y - playerHeight / 2 * 0.8f,
            playerWidth * 0.8f,
            playerHeight * 0.8f
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
        if (to != null && !isWall(to)) {
            // Проверяем, нет ли стены между узлами
            if (!hasWallBetween(from, to)) {
                connections.add(new DefaultConnection<>(from, to));
            }
        }
    }

    private boolean hasWallBetween(Vector2 from, Vector2 to) {
        float step = Math.min(playerWidth, playerHeight) / 5f; // Более точный шаг
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
