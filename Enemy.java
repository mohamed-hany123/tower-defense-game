//file Enemy.java
package application;

public abstract class Enemy extends Entity {
	
	int reward;
	double speed = 0.7;
	
	Enemy(){}
	
	Enemy(int row, int size){
		super(row, 0, size);
		drawX = 1280;
		drawY = GRID_START_Y + row * CELL_HEIGHT + CELL_HEIGHT /2 - size;
		state = states.WALK;
	}
	public void updateDrawPosition() {
		drawX -= speed;
	}
	public abstract Enemy copy(int row); 
    public void walk() {
    	if (state == states.IDLE && lastState != states.WALK) state = states.WALK;
    	if(state == states.WALK) updateDrawPosition();
    }
}
