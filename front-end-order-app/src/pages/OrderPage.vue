<template>
    <h1> This is an orderPage</h1>
</template>

<script>
import {onMounted} from 'vue'
export default {
  name: 'OrderPage',
  setup() {
    console.log('router',this.$router)
    onMounted(() => {
      console.log('OrderPage has been mounted!')
    })
  },
}
</script>