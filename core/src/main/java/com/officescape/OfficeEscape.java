// OfficeEscape.java
package com.officescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class OfficeEscape extends Game {
    private boolean showRules = true;

    @Override
    public void create() {
        if (screen != null) {
            screen.dispose();
        }

        // Всегда создаем GameRulesScreen из OfficeEscape
        setScreen(new GameRulesScreen(this));
    }

    public void onRulesCompleted() {
        showRules = false;
        // MainScreen будет создан из GameRulesScreen
    }

    public void restartGame() {
        showRules = false; // При перезапуске не показываем правила
        setScreen(new MainScreen(this)); // Сразу создаем MainScreen
    }
}
