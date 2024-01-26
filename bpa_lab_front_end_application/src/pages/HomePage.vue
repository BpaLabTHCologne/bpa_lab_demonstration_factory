<template>
    <div id="app">
    <Header />
    <Carousel />
    <!-- <Form /> -->
  </div>
  </template>

<script>
import Header from '../components/Header.vue'
import Carousel from '../components/Carousel.vue'
import Form from '../components/Form.vue'
//import axios from 'axios'
export default {
  name: 'HomePage',
  components: {
    Header,
    Carousel,
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>