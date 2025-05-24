package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Flash extends Item implements TakeableItem {
    public Flash(float x, float y) {
        super(GameConstants.FLASH_FILE_PATH, x, y, GameConstants.FLASH_SCALE);
    }


    @Override
    public void onTake(Player player) {
        take(player);
    }
}
