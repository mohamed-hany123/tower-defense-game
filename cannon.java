package application;


import javafx.scene.image.Image;

public class cannon extends Defender{
	private static final int SIZE = 120;
    private static final double cb_SPEED = 5;
    private static final double cb_WIDTH = 50;
    private static final double cb_HEIGHT = 50;
    private static Image cannonImage;

    static {
        cannonImage = new Image("file:sprites/Cañon_bala1.png");
    }



    public cannon() {
        cost = 200;
        size = SIZE;
    }

    public cannon(int row, int col) {
        super(row, col, SIZE);
        cost = 200;
        health = 200;
        damage = 50;
        range = 400;
    }

    @Override
    public Defender copy(int row, int col) {
        return new cannon(row, col);
    }

    @Override
    public void setSprite(states state) {
        if (state == lastState) return;
        lastState = state;

        switch (state) {
            case IDLE:
                img = new Image("file:sprites/Cañon_quieto.png");
                img_COL = 1;
                img_ROWS = 1;
                break;
            case ATTACK:
                img = new Image("file:sprites/Cañon_animacion.gif");
                img_COL = 1;
                img_ROWS = 1;
                break;
            case DIEING:
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
        }
        fireProjectile();
    }
    
    private void fireProjectile() {
        double startX = drawX + size - 20;
        double startY = drawY + size / 2 - cb_HEIGHT / 2;
        Projectile p = new Projectile(startX, startY, cb_SPEED, damage,
                                      cb_WIDTH, cb_HEIGHT, cannonImage);
        Main.addProjectile(p);
    }
}
