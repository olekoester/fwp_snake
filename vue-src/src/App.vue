<template>
  <div class="welcome">
    <!-- Content of the Vue App-->
    <MainVue />
  </div>
</template>

<script>
import MainVue from './components/MainVue.vue'


export default {
  name: 'App',
  components: {
    MainVue
  }
}
</script>
<style>
html,
body {
  font-family: 'Roboto', sans-serif;
  background: black;
}

span,
p,
h1,
h2 {
  color: forestgreen;
  text-align: center;
}

p,
h1,
h2 {
  margin: 6px;
}

a {
  text-decoration: darkblue;
}

table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 75%;
  margin-left: auto;
  margin-right: auto;
}

td,
th {
  border: 1px forestgreen;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: forestgreen;
}
</style>

