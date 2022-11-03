<template>

  <!-- Shows login screen, if property username is empty-->
  <div v-if="this.username ===''">
    <GameLogin @login="setUserData" />
  </div>
  <!-- Shows snake game, if property username is not empty-->
  <div v-if="this.username !==''">
    <SnakeGame @updatescore="setHighscore" :player="this.username" :highscore="this.highscore" />
  </div>
  <div style="text-align: center;">
    <input type="checkbox" v-on:change="showTopPlayers" checked="true" id="showPlayers" class="showPlayersCSS" />
    <label for="showPlayers" class="showPlayersCSS">Show Top Players</label>
  </div>
  <div v-if="this.showPlayers">
    <TopPlayers />
  </div>

</template>
  
<script>
import SnakeGame from "@/components/SnakeGame";
import GameLogin from "@/components/GameLogin.vue";
import TopPlayers from "./TopPlayers.vue";

export default {
  name: 'MainVue',
  data() {
    return {
      username: "",
      highscore: "",
      showPlayers: true
    }
  },
  components: {
    SnakeGame,
    GameLogin,
    TopPlayers
  },
  methods: {
    //set Username and Highscore after login
    setUserData(data) {
      this.username = data.username;
      this.highscore = data.highscore;
    },
    //sets new high score after new best
    setHighscore(data) {
      if (this.highscore < data.highscore) {
        this.highscore = data.highscore;
        this.sendMsg();
      }
    },
    sendMsg() {
      let highscore = this.highscore;
      Notification.requestPermission(function (result) {
        if (result === "granted") {
          navigator.serviceWorker.ready.then(function (registration) {
            const options = { body: "Neuer Highscore: " + highscore };
            registration.showNotification("Glückwunsch! Sie haben ihren Highscore gebrochen!", options);
          });
        } else {
          console.log("No permission to send notifications!");
        }
      });
    },
    showTopPlayers(event) {
      this.showPlayers = event.target.checked;
    }

  }
}
</script>
  
<style scoped="true">
.showPlayersCSS{
  color: white;
}
</style>