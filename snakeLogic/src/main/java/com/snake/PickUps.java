package com.snake;

import java.awt.Color;
import java.awt.Point;

import com.collection.PickUpCollection;

public class PickUps {
	
	private PickUpCollection type;
	private Color color;
	private Point position;
	
	/**
	 * constructor for random generation
	 */
	public PickUps() {
		if(Math.random()*100+1 > 20) {
			type = PickUpCollection.APPLE;
			color = Color.RED;
		}else {
			type = PickUpCollection.STONE;
			color = Color.BLACK;
		}
		position = new Point((int)(Math.random()*40) * 10,(int)(Math.random()*40) * 10);
	}
	
	/**
	 * constructor for specific generation
	 */
	public PickUps(PickUpCollection type) {
		this.type = type;
		if(type == PickUpCollection.STONE) {
			color = Color.BLACK;
		}else {
			color = Color.RED;
		}
		position = new Point((int)(Math.random()*40) * 10,(int)(Math.random()*40) * 10);
	}
	
	public PickUpCollection getType() {
		return type;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Point getPosition() {
		return position;
	}

}
