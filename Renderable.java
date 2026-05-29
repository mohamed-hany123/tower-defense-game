//file Renderable.java
package application;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable {
	static final int CELL_WIDTH = 141;
    static final int CELL_HEIGHT = 100;
    static final int GRID_START_X = 20;
    static final int GRID_START_Y = 225;
    
    public void setSprite(states state);
    public abstract void draw(GraphicsContext gc);
}
