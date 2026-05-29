package application;

import javafx.scene.image.Image;

public class SkeletonWarrior extends Enemy{
	
	final static int SIZE = 170;

    SkeletonWarrior() {
    	size = SIZE;
    }

    SkeletonWarrior(int row) {
        super(row, SIZE);
        health = 100;
        damage = 10;
        range  = 35;
        reward = 10;
        frameDelay = 15;
    }

    @Override
    public Enemy copy(int row) {
        return new SkeletonWarrior(row);
    }
    
    @Override
	public void setSprite(states state) {
        if (state == lastState) return;
        
        lastState = state;

        switch (state) {
            case WALK:
                img = new Image("file:sprites/Skeleton_Warrior/Walk.png");
                img_COL = 7;
                img_ROWS = 1;
                break;

            case ATTACK:
                img = new Image("file:sprites/Skeleton_Warrior/Attack_1.png");
                img_COL = 5;
                img_ROWS = 1;
                break;
            case IDLE:
                img = new Image("file:sprites/Skeleton_Warrior/Idle.png");
                img_COL = 7;
                img_ROWS = 1;
                break;
            case DIEING:
                img = new Image("file:sprites/Skeleton_Warrior/Dead.png");
                img_COL = 4;
                img_ROWS = 1;
                frameDelay = 20;
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
