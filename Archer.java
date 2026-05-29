package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Archer extends Defender {

    private static final int SIZE = 120;
    private static final double ARROW_SPEED = 3;
    private static final double ARROW_WIDTH = 50;
    private static final double ARROW_HEIGHT = 10;
    private static Image arrowImage;

    static {
        arrowImage = new Image("file:sprites/Archer/arrow.png");
    }

    private boolean arrowReleased = false;

    public Archer() {
        cost = 100;
        size = SIZE;
    }

    public Archer(int row, int col) {
        super(row, col, SIZE);
        cost = 100;
        health = 100;
        damage = 20;
        range = 1200;
    }

    @Override
    public Defender copy(int row, int col) {
        return new Archer(row, col);
    }

    @Override
    public void setSprite(states state) {
        if (state == lastState) return;
        lastState = state;

        switch (state) {
            case IDLE:
                img = new Image("file:sprites/Archer/idle.png");
                img_COL = 5;
                img_ROWS = 1;
                break;
            case ATTACK:
                img = new Image("file:sprites/Archer/atk.png");
                img_COL = 7;
                img_ROWS = 1;
                break;
            case DIEING:
                img = new Image("file:sprites/Archer/death.png");
                img_COL = 6;
                img_ROWS = 1;
                break;
            default:
                return;
        }

        img_W = img.getWidth() / img_COL;
        img_H = img.getHeight() / img_ROWS;
        img_Frames = img_ROWS * img_COL;
        loops = 1;
        img_STEPS = img_Frames * loops;
        imgStep = 0;
    }

    @Override
    public void attack(Entity e) {
        if (health <= 0 || state == states.DIEING || toRemove) return;
        if (state != states.ATTACK) {
            state = states.ATTACK;
            arrowReleased = false;
            imgStep = 0;
        }
    }
    @Override
    public void draw(GraphicsContext gc) {
        int oldStep = imgStep;
        states oldState = state;
        double savedSteps = img_STEPS;

        super.draw(gc);

        if (oldState == states.ATTACK && !toRemove) {
            boolean justFinished = (oldStep < savedSteps && (imgStep == 0 || imgStep >= savedSteps));
            if (justFinished && !arrowReleased) {
                fireProjectile();
                arrowReleased = true;
            }
            if (state != states.ATTACK) {
                arrowReleased = false;
            }
        }
    }


    private void fireProjectile() {
        double startX = drawX + size - 20;
        double startY = drawY + size / 2 + 20 - ARROW_HEIGHT / 2;
        Projectile p = new Projectile(startX, startY, ARROW_SPEED, damage,
                                      ARROW_WIDTH, ARROW_HEIGHT, arrowImage);
        Main.addProjectile(p);
    }
    
    @Override
    public void updateDrawPosition(int row, int col) {
        drawX = GRID_START_X + col * CELL_WIDTH + (CELL_WIDTH - size) / 2;
        drawY = GRID_START_Y + row * CELL_HEIGHT + (CELL_HEIGHT - size) - 50;
    }

}