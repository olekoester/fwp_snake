package com.connection;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.json.*;

import com.snake.Logic;

public class WebSocketHandler extends AbstractWebSocketHandler {

	ArrayList<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
	
	private synchronized void addSession(WebSocketSession session) {
		sessions.add(session);
		if (sessions.size() == 1) {
			Logic.GameLoop(this);
		}
	}
	
	private synchronized void removeSession(WebSocketSession session) {
		sessions.remove(session);
		Logic.removePlayer(session);
		if (sessions.size() == 0) {
			Logic.stop();
		}
	}
	
	/**
	 * Overrides the methods of AbstractWebSocketHandler to handle the WebSocket
	 */
	
	
	/**
	 * Adds the Session to the ArrayList and starts the Game loop when the first connection is established 
	 * @param session
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		addSession(session);
	}

	/**
	 * Removes the Session from the ArrayList and the player from the game
	 * Stops the Game loop if all players left
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		removeSession(session);
	}

	/**
	 * Receives Messages from the WebSocket
	 * Converts message in JSON and looks for values to either create a new player or
	 * change the direction a player is heading
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		JSONObject json = new JSONObject(message.getPayload());
		if (json.get("createNewPlayer").equals(true)) {
			Logic.setNewPlayer(json.getString("player"), session);
			return;
		}
		Logic.setDirection(json.getString("player"), json.getInt("direction"));
	}

	/**
	 * Implemented not used
	 */
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
		session.sendMessage(message);
	}
	
	/**
	 * Sending message to the WebSoket
	 * @param message
	 */
	public  synchronized void sendMessage(String message) {
		for (int i = 0; i < sessions.size(); ++i) {
			try {
				sessions.get(i).sendMessage(new TextMessage(message));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}