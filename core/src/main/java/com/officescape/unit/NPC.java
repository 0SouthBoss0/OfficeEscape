
package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;
import com.officescape.item.BreakableItem;

import java.util.Random;

public abstract class NPC extends Character {
    public enum NPCState {
        NORMAL,
        STUNNED,
        PANIC
    }
    public NPCState currentState = NPCState.NORMAL;
    public float stunTimer = 0;
    private float panicTimer = 0;
    private static final float STUN_DURATION = 3f;
    private static final float PANIC_DURATION = 15f;

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
        switch (currentState) {
            case STUNNED:
                stunTimer += deltaTime;
                if (stunTimer >= STUN_DURATION) {
                    currentState = NPCState.PANIC;
                    panicTimer = 0;
                    // При панике все NPC начинают быстро перемещаться
                    for (NPC npc : NPCFactory.getInstance().getAllNPCs()) {
                        if (npc != this) {
                            npc.currentState = NPCState.PANIC;
                            npc.panicTimer = 0;
                        }
                    }
                }
                break;
            case PANIC:
                panicTimer += deltaTime;
                if (panicTimer >= PANIC_DURATION) {
                    currentState = NPCState.NORMAL;
                    moveTimer = 0;
                    delay = generateDelay(); // Возвращаем нормальную задержку
                } else {
                    // В режиме паники NPC быстро перемещаются между точками без задержки
                    if (!isMovingToTarget) {
                        moveToRandomWaypoint();
                    }
                }
                break;
            case NORMAL:
                if (currentBrokenItem != null) {
                    handleEmergencyResponse(deltaTime, walls);
                } else {
                    isResponding = false;
                    onCustomUpdate(deltaTime, walls);
                }
                break;
        }
        super.update(deltaTime, walls);
    }

    protected abstract void onCustomUpdate(float deltaTime, Array<Rectangle> walls);

    private void handleEmergencyResponse(float deltaTime, Array<Rectangle> walls) {
        if (currentState != NPCState.NORMAL) return;

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
        if (currentBrokenItem == null && currentState == NPCState.NORMAL) {
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
