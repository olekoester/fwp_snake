<template>
  <div class="main">
    <!-- shows current highscore and username, as long as you are not game over-->
    <h1>Willkommen {{player}} zum SnakeGame!</h1>
    <br />
    <h2 v-show="alive">Aktuelle Punktzahl: {{currentScore}}, Highscore: {{highscore}}</h2>
    <p>Wenn Sie nicht wissen, wie das Spiel funktionniert dann können Sie sich <a
        href="https://www.spielbar.de/node/145443">hier</a> informieren.
    </p>
    <!-- shows playing field, as long as you are not game over-->
    <div v-show="alive" id="canvas">
      <canvas id="myCanvas" width="400" height="400"></canvas>
    </div>
    <!-- shows Game Over Screen after losing for about 3 seconds-->
    <div v-show="!alive">
      <GameOver :finalscore="this.currentScore" />
    </div>
    <p>Fortgeschrittene Webprogrammierung - Team02 <br /> <i>SS2022</i> </p>
  </div>
</template>

<script>
import GameOver from "@/components/GameOver.vue";
export default {
  name: 'SnakeGame',
  props: ['player', 'highscore'],
  data() {
    return {
      gridsize: 10,
      ws: null,
      alive: true,
      currentScore: null,
    }
  },
  mounted() {
    let self = this;
    let canvas = document.getElementById("myCanvas");
    //get Context to draw in canvas
    this.context = canvas.getContext("2d");
    //Event listener on the keyboard
    window.addEventListener('keyup', function (event) {
      self.control(event);
    });
    //Event listener for mobile
    window.addEventListener('deviceorientation', function (event) {
      self.handleDeviceOrientation(event);
    });
    //declare websocket
    this.handleCon();
  },
  methods: {

    /**
     * Keylistener for keyboard
     * Arrow up 38 W 87
     * Arrow down 40 S 83
     * Arrow left 37 A 65
     * Arrow right 39 D 68
     * @param event
     */
    control(event) {
      if ((event.keyCode === 38 || event.keyCode === 87) && this.alive) {
        this.sendCommand(0);
      } else if ((event.keyCode === 40 || event.keyCode === 83) && this.alive) {
        this.sendCommand(2);
      } else if ((event.keyCode === 37 || event.keyCode === 65) && this.alive) {
        this.sendCommand(3);
      } else if ((event.keyCode === 39 || event.keyCode === 68) && this.alive) {
        this.sendCommand(1);
      }
    },

    /**
     * Handle orientation for mobile devices
     * @param event
     */
    handleDeviceOrientation(event) {
      if (event.beta > 20 && this.alive) {
        this.sendCommand(2);
      } else if (event.beta < -20 && this.alive) {
        this.sendCommand(0);
      } else if (event.gamma < -20 && this.alive) {
        this.sendCommand(3);
      } else if (event.gamma > 20 && this.alive) {
        this.sendCommand(1);
      }
    },

    /**
     * Handle connection to the websocket
     * handles on-open and close
     */
    handleCon() {
      const us = "ws://localhost:8085/socket"
      this.ws = new WebSocket(us);
      //Update playing field onmessage-event of websocket
      this.ws.onmessage = (event) => {
        this.clearCanvas(this.context);
        this.data = event.data
        let content = JSON.parse(event.data);
        // displays players on canvas
        this.playerPosition(content, this.context);
        // displays pickups on canvas
        this.generatePickups(content, this.context);
      };
      //sends message to the server to add a player to the gama
      this.ws.onopen = () => {
        this.ws.send('{"player": "' + this.player + '", "createNewPlayer": ' + true + '}');
      };
      //update highscore onclose
      this.ws.onclose = () => {
        this.updateScore();
      };
    },

    /**
     * sends messages to the websocket
     * @param {direction} direction 
     */
    sendCommand(direction) {
      let js = '{"player": "' + this.player + '","createNewPlayer": "' + false + '", "direction": ' + direction + '}'
      this.ws.send(js);
    },

    /**
     * Updates the highscore in the REST API
     * => changes only, if the score is actually higher than the highscore
     */
    updateScore() {
      const reqOpts = {
        method: "PATCH",
        "Accept": "application/json"
      }
      fetch('http://localhost:8082/user/update?name=' + this.player + "&score=" + this.currentScore, reqOpts)
        .then((response) => response.text())
        .then((data) => {
          this.$emit("updatescore", JSON.parse(data));
        })
        .catch((error) => {
          console.error('Error:', error);
        });
    },

    /**
     * Draws Apples and Stones on the playing field
     * @param data 
     * @param context 
     */
    generatePickups(data, context) {
      let pickups = data.Pickups;
      for (let key in pickups) {
        context.fillStyle = pickups[key].color;
        context.fillRect(pickups[key].x, pickups[key].y, this.gridsize, this.gridsize);
      }
    },

    /**
     * Draws players on the gameboard
     * @param data 
     * @param context 
     */
    playerPosition(data, context) {
      let players = data.player;
      for (let key in players) {
        //Updates Highscore of the player, when he dies
        if (this.player === players[key].username) {
          if (players[key].alive) {
            this.$emit("score", this.currentScore);
          } else {
            this.updateScore();
          }
          this.currentScore = players[key].score.valueOf();
          this.alive = players[key].alive;
        }
        //Draws the head with tongue (only with gridSize 10)
        //"direction" indicates how the triangle must be rotated
        context.beginPath();
        let direction = players[key].direction;
        if (this.gridsize != 10) {
          context.beginPath();
          context.fillStyle = players[key].color;
          context.fillRect(players[key].head.x, players[key].head.y, this.gridsize, this.gridsize);
          context.closePath();
        } if (direction == 1) {
          this.drawTriangle(context, players[key].head.x, players[key].head.y, players[key].head.x, players[key].head.y + 10, players[key].head.x + 5, players[key].head.y + 5, players[key].color);
          this.drawLine(context, players[key].head.x + 5, players[key].head.y + 5, players[key].head.x + 8, players[key].head.y + 5, "red");
          this.drawLine(context, players[key].head.x + 8, players[key].head.y + 5, players[key].head.x + 10, players[key].head.y, "red");
          this.drawLine(context, players[key].head.x + 8, players[key].head.y + 5, players[key].head.x + 10, players[key].head.y + 10, "red");
        } else if (direction == 3) {
          this.drawTriangle(context, players[key].head.x + 10, players[key].head.y, players[key].head.x + 10, players[key].head.y + 10, players[key].head.x + 5, players[key].head.y + 5, players[key].color);
          this.drawLine(context, players[key].head.x + 5, players[key].head.y + 5, players[key].head.x + 2, players[key].head.y + 5, "red");
          this.drawLine(context, players[key].head.x + 2, players[key].head.y + 5, players[key].head.x, players[key].head.y, "red");
          this.drawLine(context, players[key].head.x + 2, players[key].head.y + 5, players[key].head.x, players[key].head.y + 10, "red");
        } else if (direction == 0) {
          this.drawTriangle(context, players[key].head.x, players[key].head.y + 10, players[key].head.x + 10, players[key].head.y + 10, players[key].head.x + 5, players[key].head.y + 5, players[key].color);
          this.drawLine(context, players[key].head.x + 5, players[key].head.y + 5, players[key].head.x, players[key].head.y, "red");
        } else {
          this.drawTriangle(context, players[key].head.x, players[key].head.y, players[key].head.x + 10, players[key].head.y, players[key].head.x + 5, players[key].head.y + 5, players[key].color);
          this.drawLine(context, players[key].head.x + 5, players[key].head.y + 5, players[key].head.x, players[key].head.y + 10, "red");
        }
        //Paint tail of the snake
        context.beginPath();
        context.fillStyle = players[key].color;
        for (let crd in players[key].position) {
          context.fillRect(players[key].position[crd].x, players[key].position[crd].y, this.gridsize, this.gridsize)
        }
        context.closePath();
      }
    },

    /**
     * Removes everything from the canvas
     * @param context 
     */
    clearCanvas(context) {
      let element = document.getElementById("myCanvas");
      context.clearRect(0, 0, element.width, element.height);
    },
    /**
     * Draws a Triangle
     * @param {*} context 
     * @param {*} startX 
     * @param {*} startY => start indicates the starting point og the triangle
     * @param {*} hypotenuseX 
     * @param {*} hypotenuseY => hypotenuse is the end point of the hypotenuse
     * @param {*} pointX 
     * @param {*} pointY => remaining point of the triangle
     * @param {*} color => color of the triangle
     */
    drawTriangle(context, startX, startY, hypotenuseX, hypotenuseY, pointX, pointY, color) {
      context.beginPath();
      context.fillStyle = color;
      context.moveTo(startX, startY);
      context.lineTo(hypotenuseX, hypotenuseY);
      context.lineTo(pointX, pointY);
      context.closePath();
      context.fill();
    },
    /**
     * Draws a line in the canvas
     * @param {*} context 
     * @param {*} startX 
     * @param {*} startY  => Start Point
     * @param {*} endX 
     * @param {*} endY => End Point
     * @param {*} color => color of the line
     */
    drawLine(context, startX, startY, endX, endY, color) {
      context.beginPath();
      context.moveTo(startX, startY);
      context.lineTo(endX, endY);
      context.strokeStyle = color;
      context.stroke();
      context.closePath();
    }
  },
  components: { GameOver }
}
</script>
<style scoped="true">
.main {
  margin: 10px;
  height: 600px;
  padding: 40px;
  text-align: center;

}

#canvas {
  margin: 20px auto;
  width: 400px;
  height: 400px;
  /* padding: 10px;*/
  background: gray;
  position: relative;
  border-radius: 10px;
  border: 7px solid rgba(254, 254, 254, 0.6);
}
</style> 
