package application;

import javafx.scene.image.Image;

public class Projectile {
    double x, y, width, height, damage, speed;
    Image image;

    public Projectile(double x, double y, double speed, double damage,
                      double width, double height, Image image) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public void update() {
        x += speed;
    }

    public boolean overlaps(double ex, double ey, double ew, double eh) {
        return x < ex + ew && x + width > ex && y < ey + eh && y + height > ey;
    }
}