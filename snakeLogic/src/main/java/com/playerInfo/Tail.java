package com.playerInfo;

import java.awt.Point;

public class Tail {
	
	private Point position;

	/**
	 * Constructor x and y coordinate needed for positioning
	 * @param x
	 * @param y
	 */
	public Tail(int x,int y) {
		position = new Point(x, y);
	}
	
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
	

}

