package application;

import javafx.scene.image.Image;

public class SkeletonSpearman extends Enemy {
	final static int SIZE = 170;

	SkeletonSpearman() {
    	size = SIZE;
    }

	SkeletonSpearman(int row) {
        super(row, SIZE);
        health = 100;
        damage = 10;
        range  = 70;
        reward = 20;
        frameDelay = 15;
    }

    @Override
    public Enemy copy(int row) {
        return new SkeletonSpearman(row);
    }
    
    @Override
	public void setSprite(states state) {
        if (state == lastState) return;
        
        lastState = state;

        switch (state) {
            case WALK:
                img = new Image("file:sprites/Skeleton_Spearman/Run.png");
                img_COL = 6;
                img_ROWS = 1;
                break;

            case ATTACK:
                img = new Image("file:sprites/Skeleton_Spearman/Attack_1.png");
                img_COL = 4;
                img_ROWS = 1;
                break;
            case IDLE:
                img = new Image("file:sprites/Skeleton_Spearman/Idle.png");
                img_COL = 2;
                img_ROWS = 1;
                break;
            case DIEING:
                img = new Image("file:sprites/Skeleton_Spearman/Dead.png");
                img_COL = 5;
                img_ROWS = 1;
                frameDelay = 15;
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
