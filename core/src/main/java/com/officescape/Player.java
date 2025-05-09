package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private Sprite sprite;
    private float speed = 200f; // Скорость движения

    public Player(String texturePath) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);
        // Установите начальную позицию
        sprite.setPosition(100, 100);
    }

    public void update(float deltaTime) {
        // Обработка ввода
        float moveX = 0, moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

        // Нормализация вектора движения (чтобы диагональное движение не было быстрее)
        if (moveX != 0 || moveY != 0) {
            float length = (float)Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;

            // Перемещение с учетом времени кадра
            sprite.translate(moveX * speed * deltaTime, moveY * speed * deltaTime);
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }

    public float getX() {
        return sprite.getX() + sprite.getWidth() / 2; // Центр по X
    }

    public float getY() {
        return sprite.getY() + sprite.getHeight() / 2; // Центр по Y
    }
}
