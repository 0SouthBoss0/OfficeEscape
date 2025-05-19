
package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public abstract class NPC extends Character {
    private float moveTimer = 0;
    private static final float MOVE_INTERVAL = 5f;
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
        moveTimer += deltaTime;
        if (moveTimer >= MOVE_INTERVAL) {
            moveTimer = 0;
            moveToRandomWaypoint();
        }
        super.update(deltaTime, walls);
    }

    private void moveToRandomWaypoint() {
        if (NPC_WAYPOINTS.length == 0) return;
        Vector2 waypoint = NPC_WAYPOINTS[random.nextInt(NPC_WAYPOINTS.length)];
        this.goToCoords(waypoint.x, waypoint.y);
    }

}
