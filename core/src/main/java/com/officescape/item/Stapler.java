package com.officescape.item;

import com.badlogic.gdx.math.Vector2;
import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Stapler extends Item implements TakeableItem, ThrowableItem {
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
        Vector2 direction = player.getFacingDirection();
        float throwDistance = GameConstants.ITEM_THROW_DISTANCE;
        sprite.setPosition(
            player.getX() + direction.x * throwDistance - sprite.getWidth() / 2,
            player.getY() + direction.y * throwDistance - sprite.getHeight() / 2
        );
        System.out.println("Stapler thrown!");
    }
}
