<template>
 <br/>
 <div id="gameOver">
    <h1>GAME OVER</h1>
    <p>Sie haben folgenden Score erreicht: {{finalscore}}</p>
    <p> In kürze geht es für dich weiter :)</p>
 </div>
</template>
  
  <script>
  export default {
    name: "GameOver",
    props: ["finalscore"],
   data() {
     return {
     }
   },
  
    methods: {
    }
  }
  
  </script>
  
  <style scoped>
  #gameover {
    margin-top: auto;
  }

  
  </style>