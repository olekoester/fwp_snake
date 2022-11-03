package com.snake;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.Timer;

import com.collection.PickUpCollection;
import com.connection.WebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playerInfo.PlayerHead;

import org.json.*;
import org.springframework.web.socket.WebSocketSession;

public class Logic {

	private static int MAX_PLAYER = 6;
	private static PlayerHead[] players =  new PlayerHead[MAX_PLAYER];
	private static PickUps[] pick = {new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps(),new PickUps()};
	private static final Dimension PLAY_SIZE = new Dimension(400,400);
	private static WebSocketHandler w;
	
	/**
	 * Timer is the tickrate of the Server in which everything get changed
	 */
	private static Timer timer = new Timer(100, new ActionListener() {
	    public synchronized void actionPerformed(ActionEvent evt) {
	        OOB();
	        for(PlayerHead player : players) {
	        	if(player != null) {
		        	player.updatePosition();
		        	updatePickUps(player);
	        	}
	        }
	        checkCollision();
			try {
				Logic.w.sendMessage(buildJson());
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

		}
	});
	
	/**
	 * checks if two player are on the same position
	 */
	public static synchronized void checkCollision() {
		for(int i = 0;i < players.length;++i) {
			for(int j = 0;j < players.length;++j) {
				if (players[i] != null && players[j] != null) {
					if (players[i] != players[j] && players[i].getPosition().equals(players[j].getPosition())) {
						//Both Player die
						players[i].kill();
						players[j].kill();
					} else if (players[i] != players[j] && players[j].checkTail(players[i].getPosition())) {
						//Player i dies
						players[i].kill();
					}
					//Check for J not necessary because the other player will check it!
				}
			}
			updatePickUps(players[i]);
		}
	}
	
	/**
	 * Check if a player has left the play area
	 */
	public static synchronized void OOB() {
		for (PlayerHead player : players) {
			if (player != null) {
				if (player.getPosition().x >= PLAY_SIZE.width || player.getPosition().x < 0 || player.getPosition().y >= PLAY_SIZE.height || player.getPosition().y < 0) {
					player.kill();
				}
			}
		}
	}
	
	/**
	 * Checks if a player intersects with any PickUps.
	 * Executes the results of the PickUp and generates a new one 
	 * @param player
	 */
	private static void updatePickUps(PlayerHead player) {
		if (player != null) {
			for (int i = 0; i < pick.length; ++i) {
				if (pick[i].getPosition().equals(player.getPosition())) {
					if(pick[i].getType() == PickUpCollection.APPLE) {
						player.incScore();
						player.addTailPart();
						pick[i] = new PickUps(PickUpCollection.APPLE);
					}else if(pick[i].getType() == PickUpCollection.STONE) {
						player.kill();
						pick[i] = new PickUps(PickUpCollection.STONE);
					}
				}
				
			}
		}
	}
	
	/**
	 * Sets the direction of the player if the usernames equal.
	 * @param username
	 * @param direction
	 */
	public static synchronized void setDirection(String username,int direction) {
		for (PlayerHead player : players) {
			if (player != null) {
				if (username.equals(player.getUsername())) {
					player.setDirection(direction);
				}
			}
		}
	}
	
	/**
	 * Starts the game loop and sets the WebSocket
	 */
	public static void GameLoop(WebSocketHandler w) {
		if(!timer.isRunning()) {
			Logic.w = w;
			timer.start();
		}
	}
	
	/**
	 * Stops the game loop
	 */
	public static void stop() {
		if(timer.isRunning()) {
			timer.stop();
			Logic.w = null;
		}
	}

	/**
	 * Sets a new player and saves him with his current Session
	 * @param username
	 * @param session
	 */
	public static synchronized void setNewPlayer(String username, WebSocketSession session) {
		PlayerHead newPlayer = new PlayerHead(username, session);
		int position = checkPlayers();
		if (position >= 0) {
			players[position] = newPlayer;
		}
	}
	
	/**
	 * Remove a player from the game with the session
	 * @param session
	 */
	public static synchronized void removePlayer(WebSocketSession session) {
		for(int i = 0; i < players.length; ++i) {
			if(players[i] != null && players[i].getSession() == session) {
				players[i] = null; 
			}
		}
	}

	/**
	 * Checks if a player slot is free and returns the index
	 * @return
	 */
	public static synchronized int checkPlayers() {
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * create JSON object which contains the player information
	 * @return
	 */
	private static synchronized JSONArray createUserJSON() {
		JSONArray userJSON = new JSONArray();
		for (PlayerHead player : players) {
			if (player != null) {
				JSONObject tmp = new JSONObject();
				JSONObject head = new JSONObject();
				tmp.put("username", player.getUsername());
				tmp.put("score", player.getScore());
				tmp.put("alive", player.isAlive());
				tmp.put("direction", player.getDirection());
				head.put("x", player.getPosition().x);
				head.put("y", player.getPosition().y);
				tmp.put("head", head);
				tmp.put("position", player.createPositionJSON());
				if(player.equals(players[0])) {
					tmp.put("color", "yellow");
				}else if(player.equals(players[1])) {
					tmp.put("color", "blue");
				}else if(player.equals(players[2])) {
					tmp.put("color", "green");
				}else if(player.equals(players[3])) {
					tmp.put("color", "orange");
				}else if(player.equals(players[4])) {
					tmp.put("color", "aqua");
				}else if(player.equals(players[5])) {
					tmp.put("color", "white");
				}else {
					tmp.put("color", "pink");
				}
				userJSON.put(tmp);
			}
		}
		return userJSON;
	}
	
	/**
	 * Creates JSON object for the PickUps
	 * @return
	 */
	private static JSONArray createPickupsJSON() {
		JSONArray itemJSON = new JSONArray();
		for (PickUps picks : pick) {
			JSONObject tmp = new JSONObject();
			tmp.put("type", picks.getType());
			tmp.put("color", "#"+Integer.toHexString(picks.getColor().getRGB()).substring(2));
			tmp.put("x", picks.getPosition().x);
			tmp.put("y", picks.getPosition().y);
			itemJSON.put(tmp);
		}

		return itemJSON;
	}
	/**
	 * Builds JSON ready to be send to the Websocket
	 * @return
	 * @throws JsonProcessingException
	 */
	private static String buildJson() throws JsonProcessingException {
		JSONObject json = new JSONObject();
		json.put("player", createUserJSON());
		json.put("Pickups", createPickupsJSON());
		return json.toString();
	}
}
