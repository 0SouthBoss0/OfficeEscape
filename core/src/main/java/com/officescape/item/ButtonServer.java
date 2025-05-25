package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class ButtonServer extends Item  {
    public ButtonServer(float x, float y, Character.Direction direction) {
        super(GameConstants.BUTTON_SERVER_FILE_PATH, x, y, GameConstants.BUTTON_SERVER_SCALE, direction);
    }



}
