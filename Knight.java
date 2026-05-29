//file Knight.java
package application;


import javafx.scene.image.Image;

public class Knight extends Defender {
    
	final static int SIZE = 200;

    Knight() {
    	cost = 100;
    	size = SIZE;
    }

    Knight(int row, int col) {
        super(row, col, SIZE);
        cost = 100;
        health = 250;
        damage = 35;
        range  = 35;
        attackCooldown = 0;
    }

    @Override
    public Defender copy(int row, int col) {
        return new Knight(row, col);
    }
    
    @Override
	public void setSprite(states state) {

        if (state == lastState) return;
        lastState = state;

        switch (state) {
            case IDLE:
                img = new Image("file:sprites/knight/IDLE.png");
                img_COL = 7;
                img_ROWS = 1;
                break;

            case ATTACK:
                img = new Image("file:sprites/knight/ATTACK 1.png");
                img_COL = 6;
                img_ROWS = 1;
                break;
            case DIEING:
                img = new Image("file:sprites/knight/DEATH.png");
                img_COL = 12;
                img_ROWS = 1;
                break;
            case WALK: return;
            default: return;
        }

        img_W = img.getWidth() / img_COL;
        img_H = img.getHeight() / img_ROWS;

        img_Frames = img_ROWS * img_COL;
        loops = 1;
        img_STEPS = img_Frames * loops;
        imgStep = 0;
    }
}