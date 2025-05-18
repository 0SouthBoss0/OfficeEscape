
package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

import java.util.Random;

public class NPC extends Character {
    private float moveTimer = 0;
    private static final float MOVE_INTERVAL = 5f; // 5 секунд
    private static final Random random = new Random();
    public static final Vector2[] NPC_WAYPOINTS = {
        new Vector2(100, 100),
        new Vector2(300, 200),
        new Vector2(500, 300),
        new Vector2(200, 400),
        new Vector2(500, 400),
        new Vector2(600, 400),
        new Vector2(120, 100)
    };

    public NPC(String texturePath, int x, int y) {
        super(texturePath, x, y);
    }

    @Override
    public void update(float deltaTime, Array<Rectangle> walls) {
        // Обновляем таймер
        moveTimer += deltaTime;

        // Если пришло время двигаться
        if (moveTimer >= MOVE_INTERVAL) {
            moveTimer = 0; // Сбрасываем таймер
            moveToRandomWaypoint(); // Перемещаемся на случайную точку
        }

        // Вызываем родительский update для обработки движения
        super.update(deltaTime, walls);
    }

    private void moveToRandomWaypoint() {
        if (NPC_WAYPOINTS.length == 0) return;

        // Выбираем случайную точку из массива
        Vector2 waypoint = NPC_WAYPOINTS[
            random.nextInt(NPC_WAYPOINTS.length)
            ];

        // Задаем новую цель
        this.goToCoords(waypoint.x, waypoint.y);
    }

}
