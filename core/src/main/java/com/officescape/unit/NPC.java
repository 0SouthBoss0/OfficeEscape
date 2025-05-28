
package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;
import com.officescape.item.BreakableItem;

import java.util.Random;

public abstract class NPC extends Character {
    private float moveTimer = 0;
    private float delay = 0;
    private static final Random random = new Random();
    public static BreakableItem currentBrokenItem = null;
    public boolean isResponding = false;
    private float fixingTimer = 0;
    private static final float FIXING_TIME = 5f;
    public GameConstants.Position[] NPC_WAYPOINTS;

    public NPC(String texturePath, int x, int y, GameConstants.Position[] waypoints) {
        super(texturePath, x, y);
        this.NPC_WAYPOINTS = waypoints;
    }

    public static void reportBrokenItem(BreakableItem item) {
        if (currentBrokenItem == null) {
            currentBrokenItem = item;
        }
    }

    @Override
    public void update(float deltaTime, Array<Rectangle> walls) {
        if (currentBrokenItem != null) {
            handleEmergencyResponse(deltaTime, walls);
        } else {
            isResponding = false;
            onCustomUpdate(deltaTime, walls);
        }
        super.update(deltaTime, walls);
    }

    protected abstract void onCustomUpdate(float deltaTime, Array<Rectangle> walls);

    private void handleEmergencyResponse(float deltaTime, Array<Rectangle> walls) {
        Vector2 targetPos = currentBrokenItem.getPosition();
        float distanceToItem = Vector2.dst(getX(), getY(), targetPos.x, targetPos.y);

        if (!isResponding) {
            this.goToCoords(targetPos.x, targetPos.y);
            isResponding = true;
        }
        if (this instanceof Itshnik && distanceToItem < 1.5 * GameConstants.MAX_APPROACH_DISTANCE) {
            fixingTimer += deltaTime;
            if (fixingTimer >= FIXING_TIME) {
                currentBrokenItem.onFix(this);
                currentBrokenItem = null;
                fixingTimer = 0;
            }
        }
    }

    protected void walkThrough(float deltaTime) {
        if (currentBrokenItem == null) {
            if (!isMovingToTarget) {
                moveTimer += deltaTime;
                if (moveTimer >= delay) {
                    moveTimer = 0;
                    moveToRandomWaypoint();
                    delay = generateDelay();
                }
            }
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
