package application;

import javafx.scene.image.Image;

public class Boss extends Enemy{
	final static int SIZE = 200;

	Boss() {
    	size = SIZE;
    }

	Boss(int row) {
        super(row, SIZE);
        health = 300;
        damage = 40;
        range  = 35;
        reward = 20;
        frameDelay = 5;
        drawY += 30;
    }

    @Override
    public Enemy copy(int row) {
        return new Boss(row);
    }
    
    @Override
	public void setSprite(states state) {
        if (state == lastState) return;
        
        lastState = state;

        switch (state) {
            case WALK:
                img = new Image("file:sprites/mino/walk.png");
                img_COL = 12;
                img_ROWS = 1;
                break;

            case ATTACK:
                img = new Image("file:sprites/mino/attack.png");
                img_COL = 6;
                img_ROWS = 1;
                break;
            case IDLE:
                img = new Image("file:sprites/mino/idle.png");
                img_COL = 16;
                img_ROWS = 1;
                break;
            case DIEING:
                break;
        }

        img_W = img.getWidth() / img_COL;
        img_H = img.getHeight() / img_ROWS;

        img_Frames = img_ROWS * img_COL;
        loops = 1;
        img_STEPS = img_Frames * loops;
        imgStep = 0;
    }
    
}
