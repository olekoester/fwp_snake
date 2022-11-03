package de.hbv.assignment01;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Produces;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class PlayersController {


    List<Player> playerList;
    Players players = new Players();
    public PlayersController(){

        playerList = players.initPlayers();
    }



    // get list of all players
    @GetMapping("/user/get")
    @Produces({"application/json", "application/xml"})
    public List<Player> getPlayers(){
        return playerList;
    }


    //get player by username
    @GetMapping("/user/find")
    @Produces({"application/json", "application/xml"})
    public List<Player> findPlayer(@RequestParam("name") String name) {
        ArrayList<Player> foundPlayers = new ArrayList<>();
            for(Player p : playerList){
            if(p.getUsername().equals(name))
            	foundPlayers.add(p);
        }
        return foundPlayers;
    }

    //Add new player
    @PutMapping("/user/add")
    @Produces({"application/json", "application/xml"})
    public Player addPlayer(@RequestParam("score") int score, @RequestParam("name") String name) {
       Player newPlayer = null;
        for(Player p : playerList) {
            if (p.getUsername().equals(name)) {
                newPlayer = p;
            }
        }
        if(newPlayer == null){
            newPlayer = new Player(score, name);
            playerList.add(newPlayer);
        }

        return newPlayer;
    }


    //update player scores
    @PatchMapping("/user/update")
    @Produces({"application/json", "application/xml"})
    public Player updateScore(@RequestParam("name") String name, @RequestParam("score") int score) {
        Player tmpPlayer = null;
        for(Player p : getPlayers()){
            if(p.getUsername().equals(name)){
                if( score > p.getHighscore())
                    p.setHighscore(score);
                tmpPlayer = p;
            }
        }
        return tmpPlayer;
    }

    // remove player
    @DeleteMapping("/user/remove")
    @Produces({"application/json", "application/xml"})
    public String removerPlayer(@RequestParam("name") String name) {
        List<Player> p = playerList;
        p.removeIf(py -> py.getUsername().equals(name));
        return "Player " +name+" removed";
    }
}