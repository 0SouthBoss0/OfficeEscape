package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.GameProgress;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class Flash extends Item implements TakeableItem {
    public Flash(float x, float y, Character.Direction direction) {
        super(GameConstants.FLASH_FILE_PATH, x, y, GameConstants.FLASH_SCALE, direction);
    }


    @Override
    public void onTake(Player player, GameProgress gameProgress) {
        gameProgress.updateQuest(0, true);
        take(player);
    }
}
