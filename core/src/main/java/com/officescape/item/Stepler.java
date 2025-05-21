package com.officescape.item;

import com.officescape.unit.Player;

public class Stepler extends Item {
    public Stepler(float x, float y) {
        super("items/stepler.png", x, y, 0.05f);
    }

    @Override
    public void onPickUp(Player player) {
        // Логика при подборе степлера
        System.out.println("Stepler picked up!");
    }


}
