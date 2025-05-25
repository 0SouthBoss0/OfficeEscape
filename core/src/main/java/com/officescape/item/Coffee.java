package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class Coffee extends Item implements TakeableItem {
    public Coffee(float x, float y, Character.Direction direction) {
        super(GameConstants.COFFEE_FILE_PATH, x, y, GameConstants.COFFEE_SCALE, direction);
    }


    @Override
    public void onTake(Player player) {
        take(player);
    }
}
