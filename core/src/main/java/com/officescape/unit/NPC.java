
package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

import java.util.Random;

public abstract class NPC extends Character {
    private float moveTimer = 0;
    private float delay = 0;
    private static final Random random = new Random();

    public GameConstants.Position[] NPC_WAYPOINTS;

    public NPC(String texturePath, int x, int y) {
        super(texturePath, x, y);
        setNPCWaypoints();
    }

    @Override
    public void update(float deltaTime, Array<Rectangle> walls) {
        onCustomUpdate(deltaTime, walls);
        super.update(deltaTime, walls);
    }

    protected abstract void onCustomUpdate(float deltaTime, Array<Rectangle> walls);

    protected abstract void setNPCWaypoints();

    protected void walkThrough(float deltaTime) {
        moveTimer += deltaTime;
        if (moveTimer >= delay) {
            moveTimer = 0;
            moveToRandomWaypoint();
            delay = generateDelay();
        }
    }

    protected boolean isPlayerNearby(Array<Rectangle> walls) {
        Player player = findPlayerInRange();
        if (player != null && this.canSee(player, walls)) {
            System.out.println(this.getClass().getSimpleName() + " sees me!");
            return true;
        }
        return false;
    }

    private void moveToRandomWaypoint() {
        if (NPC_WAYPOINTS.length == 0) return;
        GameConstants.Position waypoint = NPC_WAYPOINTS[random.nextInt(NPC_WAYPOINTS.length)];
        this.goToCoords(waypoint.x(), waypoint.y(), waypoint.direction());
        this.generateDelay();
    }

    private Player findPlayerInRange() {
        return NPCFactory.getInstance().getPlayer();
    }

    private float generateDelay() {
        return random.nextFloat() * (GameConstants.NPC_MAX_DELAY - GameConstants.NPC_MIN_DELAY) + GameConstants.NPC_MIN_DELAY;
    }
}
