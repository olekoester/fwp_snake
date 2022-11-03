<template>
  <div class="main">
    <h1 class="welcome">Willkommen zum SnakeGame</h1>
    <p>Geben Sie bitte Ihren Usernamen in das Loginfeld ein, um das Spiel zu beginnen</p>

    <div id="login">
      <h3>Login</h3>
      <p>&nbsp;</p>
      <input @keyup.enter="login()" type="text" name="username" v-model="name" placeholder="Username" />
      <button type="button" v-on:click="login()">Login</button>
    </div>
    <p>Fortgeschrittene Webprogrammierung - Team02 <br /> <i>SS2022</i> </p>
  </div>
</template>
<script>

export default {
  name: 'GameLogin',
  data() {
    return {
      name: "",
    }
  },
  methods: {
    /**
     * Checks whether player already exists on the REST API
     * => Yes: get Highscore and username => fire Login Event
     * => No: create User in REST API => get Highscore(0) and username => fire Login Event
     */
    login() {
      if (this.name !== "") {
        const name = this.name;
        const reqOpts = {
          method: "PUT",
          "Accept": "application/json"
        }
        fetch('http://localhost:8082/user/add?name=' + name + "&score=0", reqOpts)
          .then((response) => response.text())
          .then((data) => {
            const obj = JSON.parse(data);
            this.$emit("login", obj);
            console.log("Success", data)
          })
          .catch((error) => {
            console.error('Error:', error);
          });
      }
    }
  },
}
</script>

<style scoped="true">
.main {
  margin: 10px;
  height: 600px;
  padding: 40px;
  text-align: center;
}

#login {
  position: relative;
  border-radius: 10px;
  border: 7px solid rgba(254, 254, 254, 0.6);
  margin: 80px auto;
  width: 300px;
  padding: 20px;
  background: forestgreen;
}
</style>