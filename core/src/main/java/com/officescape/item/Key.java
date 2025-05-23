package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Key extends Item {
    public Key(float x, float y) {
        super(GameConstants.KEY_FILE_PATH, x, y, GameConstants.KEY_SCALE);
    }

    @Override
    public void onPickUp(Player player) {
        System.out.println("Key picked up!");
    }
}
