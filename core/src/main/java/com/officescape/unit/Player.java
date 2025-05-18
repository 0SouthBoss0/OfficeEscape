package com.officescape.unit;


import com.officescape.GameConstants;

public class Player extends Character {
    public Player(String texturePath) {
        super(texturePath, GameConstants.PLAYER_START_X, GameConstants.PLAYER_START_Y);
    }
}

