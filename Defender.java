//file Defender.java
package application;

public abstract class Defender extends Entity{
    
	double cost;

    Defender() {}

    Defender(int row, int col, int size) {
    	super(row, col, size);
    }


    public abstract Defender copy(int row, int col);

}