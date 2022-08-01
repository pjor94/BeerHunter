<template>
  <v-dialog
      v-model="loader"
      persistent
      width="300"
  >
    <v-card
        color="primary"
        dark
    >
      <v-card-text>
        {{msg}} ...
        <v-progress-linear
            indeterminate
            color="white"
            class="mb-0"
        ></v-progress-linear>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters, mapState} from "vuex";

export default {
  name: "Loader",
  computed:{
    ...mapGetters({
      msg:'Loader/getMsg',
    }),
    ...mapState('Loader',[
      'loader',
    ])
  }
}
</script>

<style scoped>

</style>