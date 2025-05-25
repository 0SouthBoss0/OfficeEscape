package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.GameProgress;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class Fish extends Item implements TakeableItem {
    public Fish(float x, float y, Character.Direction direction) {
        super(GameConstants.FISH_FILE_PATH, x, y, GameConstants.FISH_FILE_SCALE, direction);
    }

    @Override
    public void onTake(Player player, GameProgress gameProgress) {
        take(player);
    }
}
