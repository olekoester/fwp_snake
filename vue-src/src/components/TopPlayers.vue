<template>
    <br />
    <div id="topPlayers">
        <h1>Top Players</h1>
        <table>
            <tr>
                <th> Position </th>
                <th> Spieler</th>
                <th> Highscore</th>
            </tr>
            <tr v-for="(player, index) in players" :key="index">
                <td> {{index +1}}</td>
                <td>{{player.username}}</td>
                <td>{{player.highscore}}</td>
            </tr>
        </table>
    </div>
</template>
     
<script>
export default {
    name: "TopPlayers",
    data() {
        return {
            players: ""
        }
    },
    mounted() {
        this.getPlayers();
    },

    methods: {
        getPlayers() {
            fetch('http://localhost:8082/user/get')
                .then((response) => response.text())
                .then((data) => {
                    const obj = JSON.parse(data);
                    obj.sort((a, b) => parseFloat(b.highscore) - parseFloat(a.highscore));
                    this.players = obj;
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
    }
}

</script>
     
<style scoped="true">
#topPlayers {
    color: white;
}
</style>