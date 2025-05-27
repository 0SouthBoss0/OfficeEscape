package com.officescape.item;

import com.badlogic.gdx.math.Vector2;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public interface BreakableItem {

    void onBreak(Player player);

    void onFix(Character character);
    Vector2 getPosition();
}
