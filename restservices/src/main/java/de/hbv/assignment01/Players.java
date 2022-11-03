package de.hbv.assignment01;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "players")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Players implements Serializable {

	Players players;
	public JAXBContext jaxbContext;
    public Players(){}
    private List<Player> player;

    @XmlElement
    public List<Player> getPlayer() {
        return player;
    }
    
    
    //unmarshal XML file for  player initialization
    
    public List<Player> initPlayers() {
        InputStream xmlFile;
       try {
            xmlFile = new ClassPathResource("players.xml").getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            jaxbContext = JAXBContext.newInstance(Players.class);
            Unmarshaller jaxbUnmarshaller;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            players = (Players) jaxbUnmarshaller.unmarshal(xmlFile);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return players.getPlayer();
    }

}

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.PROPERTY)
class Player {


    private int highscore;
    private String username; //unique
    public Player(){}
    public Player(int highscore, String username) {
        this.highscore = highscore;
        this.username = username;
    }

    @XmlElement
    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
