//file Entity.java
package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

public abstract class Entity implements Renderable, Attackable {
    int row, col;
    private ColorAdjust hitFlash = new ColorAdjust();
    int size;
    double drawX;
    double drawY;
    double health;
    double range;
    double damage;
    private boolean isBlinking = false;
    public boolean toRemove = false;
    private long blinkStartTime = 0;
    private static final long BLINK_DURATION_NANOS = 150_000_000;
    protected double attackCooldown = 0.8;
    protected double currentCooldown = 0.0;


    public void startBlink() {
        isBlinking = true;
        blinkStartTime = System.nanoTime();
    }
    states lastState = null;
    protected boolean isBlinkingNow() {
        if (isBlinking) {
            if (System.nanoTime() - blinkStartTime > BLINK_DURATION_NANOS) {
                isBlinking = false;
            }
        }
        return isBlinking;
    }
    @Override
    public void attack(Entity e) {
    	if (health <= 0 || state == states.DIEING || toRemove || e.health <= 0) return;;
        if (state == states.IDLE || state == states.WALK) {
            state = states.ATTACK;
            e.Hurt(damage);
        }
    }
    

    
    @Override
    public void Hurt(double damage) {
		health -= damage;
		startBlink();
		
    }
    public void draw(GraphicsContext gc) {
        if (toRemove) return;
        setSprite(state);

        int frame = imgStep % (int) img_Frames;
        

        
        if (isBlinkingNow()) {
            hitFlash.setBrightness(0.6);
            hitFlash.setSaturation(-0.5);
            gc.setEffect(hitFlash);
        } else {
            gc.setEffect(null);
        }

        gc.drawImage(img,
                (frame % (int) img_COL) * img_W,
                (frame / (int) img_COL) * img_H,
                img_W, img_H,
                drawX, drawY,
                size, size);
        gc.setEffect(null);

        delayCounter++;
        if (delayCounter >= frameDelay) {
            imgStep++;
            delayCounter = 0;
        }

        if (imgStep >= img_STEPS) {
            if (state == states.DIEING) {
                toRemove = true;
                return;
            }
            frameDelay = 15;
            if (state != states.WALK) state = states.IDLE;
            imgStep = 0;
        }
    }

    public void updateDrawPosition(int row, int col) {
        drawX = GRID_START_X + col * CELL_WIDTH + (CELL_WIDTH - size) / 2;
        drawY = GRID_START_Y + row * CELL_HEIGHT + CELL_HEIGHT - size;
    }
    Image img;
    states state = states.IDLE;
    
	int imgStep = 0;
    int frameDelay = 10;
    int delayCounter = 0;
    double img_W;
    double img_ROWS;
    double img_COL;
    double img_H;
    double img_Frames;
    double loops;
    double img_STEPS;
    
    public Entity() {}
    public Entity(int row, int col, int size) {
    	this.row = row;
    	this.col = col;
    	this.size = size;
    	updateDrawPosition(row, col);
    }
}
