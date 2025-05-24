package com.officescape.item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.officescape.GameConstants;
import com.officescape.TiledGraph;
import com.officescape.unit.NPC;
import com.officescape.unit.NPCFactory;
import com.officescape.unit.Player;

public class Stapler extends Item implements TakeableItem, ThrowableItem {
    private boolean isFlying = false;
    private Vector2 flightStart;
    private Vector2 flightDirection;
    private float flightDistance;
    private float flightProgress;
    private float disposeTimer = 0;

    public Stapler(float x, float y) {
        super(GameConstants.STAPLER_FILE_PATH, x, y, GameConstants.STAPLER_SCALE);
    }


    @Override
    public void onTake(Player player) {
        take(player);
    }

    @Override
    public void onThrow(Player player) {
        isTaken = false;
        this.flightDirection = player.getFacingDirection();
        this.flightStart = new Vector2(player.getX(), player.getY());
        this.flightDistance = GameConstants.ITEM_THROW_DISTANCE;
        this.flightProgress = 0;
        this.isFlying = true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isTaken && !isReadyToDispose()) {
            super.draw(batch);
        }
    }


    public void updateFlight(float deltaTime) {
        if (!isFlying && !isUsed) return;

        if (isFlying) {
            float flightSpeed = GameConstants.STAPLER_FLIGHT_SPEED;
            flightProgress += flightSpeed * deltaTime;
            float progress = Math.min(flightProgress / flightDistance, 1f);

            float currentX = flightStart.x + flightDirection.x * flightProgress;
            float currentY = flightStart.y + flightDirection.y * flightProgress;

            sprite.setPosition(
                currentX - sprite.getWidth() / 2,
                currentY - sprite.getHeight() / 2
            );

            if (checkWallCollision(currentX, currentY) || checkNPCHit(currentX, currentY) || progress >= 1f) {
                isFlying = false;
                this.isUsed = true;
            }
        }
        else {
            System.out.println("DISPOSING");
            disposeTimer += deltaTime;
        }
    }

    private boolean checkWallCollision(float x, float y) {
        TiledGraph graph = TiledGraph.getInstance();
        return graph.hasWallBetween(flightStart, new Vector2(x, y));
    }

    private boolean checkNPCHit(float x, float y) {
        for (NPC npc : NPCFactory.getInstance().getAllNPCs()) {
            if (npc.getCollisionBox().contains(x, y)) {
                System.out.println("KILLED: " + npc.getClass().getSimpleName());
                return true;
            }
        }
        return false;
    }

    public boolean isReadyToDispose() {
        return isUsed && disposeTimer >= GameConstants.STAPLER_DISPOSE_DELAY;
    }
}
