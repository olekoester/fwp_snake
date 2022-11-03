package com.playerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.socket.WebSocketSession;

public class PlayerHead {

	private Point position;
	private String username;
	private boolean alive;
	private int reviveCounter;
	private List<Tail> tail = new ArrayList<>();
	private int score;
	private WebSocketSession session;
	private int direction;
	/* -1 is default value
	 * 0 up
	 * 1 right
	 * 2 down
	 * 3 left
	 * 3 > undifined
	 */
	
	//Used to calculate steps and positions
	private static final int PLAYER_STEP_SIZE = 10;


	public PlayerHead(String username, WebSocketSession session) {
		direction = -1;
		this.username = username;
		this.session = session;
		position = new Point((int)(Math.random()*40) * 10,(int)(Math.random()*40) * 10);
		score = 0;
		alive = true;
		reviveCounter = 0;
	}

	/**
	 * Updates the Player position
	 * Contains the respawn timer until the player gets Controls back
	 * Checks if the player runs into his own Tail
	 */
	public void updatePosition() {
		
		if(alive == false) {
			++reviveCounter;
			if(reviveCounter == 25) {
				score = 0;
				alive = true;
				reviveCounter = 0;
			}
		}
		
		//Changes the player head position on the new point depending on the direction
		Point tmpPoint = new Point(position);
		switch (direction) {
		case 0:
			position.y = position.y - PLAYER_STEP_SIZE;
			break;
		case 1:
			position.x = position.x + PLAYER_STEP_SIZE;
			break;
		case 2:
			position.y = position.y + PLAYER_STEP_SIZE;
			break;
		case 3:
			position.x = position.x - PLAYER_STEP_SIZE;
			break;
		case -1:
			break;
		default:
			;
		}
		
		//Sets the Tail on the previous point and save old position beforehand 
		Point prev = null;
		for (int i = 0; i < tail.size(); ++i) {
			
			//Checks if Snake intersects with itself
			prev = tail.get(i).getPosition();
			if(position.equals(prev)) {
				kill();
				break;
			}
			
			tail.get(i).setPosition(tmpPoint);
			tmpPoint = prev;

		}

	}
	
	/**
	 * @param position
	 * @return
	 * Checks if the position in the parameter intersects with the tail of player
	 */
	public boolean checkTail(Point position) {
		for (int i = 0; i < tail.size(); ++i) {
			if (position.equals(tail.get(i).getPosition())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Prepares the player after dying for the respawn
	 */
	public void kill() {
		tail.clear();
		direction = -1;
		alive = false;
		setPosition(new Point((int)(Math.random()*40) * 10,(int)(Math.random()*40) * 10));
	}

	/**
	 * Places the new Element at the End of the Snake
	 */
	public void addTailPart() {
		int x = 0;
		int y = 0;

		//If there aren't 2 tail elements the elements are placed in dependency of the direction of the player
		if(tail.size()  <= 1) {
			switch (direction) {
			case 0:
				x = position.x;
				y = position.y + PLAYER_STEP_SIZE;
				if(tail.size() == 1) {
					y += 10;
				}
				break;
			case 1:
				x = position.x - PLAYER_STEP_SIZE;
				y = position.y;
				if(tail.size() == 1) {
					x -= PLAYER_STEP_SIZE;
				}
				break;
			case 2:
				x = position.x;
				y = position.y - PLAYER_STEP_SIZE;
				if(tail.size() == 1) {
					y -= PLAYER_STEP_SIZE;
				}
				break;
			case 3:
				x = position.x + PLAYER_STEP_SIZE;
				y = position.y;
				if(tail.size() == 1) {
					x += PLAYER_STEP_SIZE;
				}
				break;
			case -1:
				break;
			default:
				;
			}
			
		}
		/*
		 * If there are more then 2 tail elements the position of the new element is calculated.
		 * to do that the last two points are necessary.
		 * from the last point the second to last point gets subtracted and then added on the last.
		 * This is the position of the new element.
		 */
		else if(tail.size() >= 2 ) {
			 Point last = tail.get(tail.size()-1).getPosition();
			 Point secLast = tail.get(tail.size()-2).getPosition();
			 x = (last.x - secLast.x) + last.x;
			 y = (last.y - secLast.y) + last.y;
		}
		
		tail.add(new Tail(x,y));
	}
	
	/**
	 * returns a JSON object of all tail elements
	 */
	public JSONArray createPositionJSON() {
		JSONArray tailJSON = new JSONArray();
		this.tail.forEach(part -> {
			JSONObject snakeParts = new JSONObject();
			snakeParts.put("x", part.getPosition().x);
			snakeParts.put("y", part.getPosition().y);
			tailJSON.put(snakeParts);
		});
		return tailJSON;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public String getUsername() {
		return username;
	}
	
	public WebSocketSession getSession() {
		return session;
	}
	
	public void incScore() {
		++score;
	}
}
