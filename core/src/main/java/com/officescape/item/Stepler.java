package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Stepler extends Item {
    public Stepler(float x, float y) {
        super(GameConstants.STEPLER_FILE_PATH, x, y, GameConstants.STEPLER_SCALE);
    }

    @Override
    public void onPickUp(Player player) {
        System.out.println("Stepler picked up!");
    }


}
