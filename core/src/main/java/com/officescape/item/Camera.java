package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class Camera extends Item {
    public Camera(float x, float y, Character.Direction direction) {
        super(GameConstants.CAMERA_GRAY_FILE_PATH, x, y, GameConstants.CAMERA_GRAY_SCALE, direction);
    }

}
