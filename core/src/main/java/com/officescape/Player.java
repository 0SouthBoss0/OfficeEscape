package com.officescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Player {
    private Sprite sprite;
    private float speed = 200f;
    private Rectangle collisionBox;
    private float scale = 0.1f;

    public Player(String texturePath) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        sprite = new Sprite(texture);

        // Инициализируем collisionBox ДО вызова setScale
        collisionBox = new Rectangle();

        setScale(scale);
        sprite.setPosition(100, 300);
        updateCollisionBox(); // Обновляем хитбокс после установки позиции
    }

    public void setScale(float scale) {
        this.scale = scale;
        sprite.setSize(
            sprite.getTexture().getWidth() * scale,
            sprite.getTexture().getHeight() * scale
        );
        updateCollisionBox();
    }

    public void update(float deltaTime, Array<Rectangle> walls) {
        float oldX = sprite.getX();
        float oldY = sprite.getY();

        float moveX = 0, moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1;

        if (moveX != 0 || moveY != 0) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;

            sprite.translate(moveX * speed * deltaTime, moveY * speed * deltaTime);
            updateCollisionBox();

            if (checkCollisions(walls)) {
                sprite.setPosition(oldX, oldY);
                updateCollisionBox();
            }
        }
    }

    private boolean checkCollisions(Array<Rectangle> walls) {
        if (walls == null) return false;

        for (Rectangle wall : walls) {
            if (collisionBox.overlaps(wall)) {
                return true;
            }
        }
        return false;
    }

    private void updateCollisionBox() {
        // Узкий хитбокс (30% ширины спрайта, 60% высоты)
        float width = sprite.getWidth() * 0.3f;
        float height = sprite.getHeight() * 0.3f;

        // Центрирование по горизонтали
        float x = sprite.getX() + (sprite.getWidth() - width) / 2;
        float y = sprite.getY() + sprite.getHeight() * 0.2f; // Смещение вверх

        collisionBox.set(x, y, width, height);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);

        // Для отладки можно отрисовать хитбокс
        // (нужно использовать ShapeRenderer)
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }

    public float getX() {
        return sprite.getX() + sprite.getWidth() / 2;
    }

    public float getY() {
        return sprite.getY() + sprite.getHeight() / 2;
    }

    // Геттеры для хитбокса (может пригодиться)
    public Rectangle getCollisionBox() {
        return collisionBox;
    }
}
