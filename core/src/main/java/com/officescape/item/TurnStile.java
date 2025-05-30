package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.GameProgress;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class TurnStile extends Item implements UsableItem {
    public TurnStile(float x, float y, Character.Direction direction) {
        super(GameConstants.TURNSTILE_FILE_PATH, x, y, GameConstants.TURNSTILE_SCALE, direction);
    }


    @Override
    public void onUse(GameProgress gameProgress) {
        this.isUsed = true;
        gameProgress.updateQuest(3, true);
        gameProgress.updateQuest(4, true);
    }
}
