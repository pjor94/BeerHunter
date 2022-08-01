<template>
  <v-snackbar
      v-model="snack"
      :color="color"
      top
      right
  >
    {{ msg }}

    <template v-slot:action="{ attrs }">
      <v-btn
          text
          v-bind="attrs"
          @click="snack = false"
      >
        Close
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import {mapGetters, mapMutations, mapState} from "vuex";
export default {
  name: "SnackBar",
  methods:{
    ...mapMutations({
      updateSnackbar:'Notify/updateSnackbar',
    })
  },
  computed:{
    ...mapGetters({
      msg:'Notify/getMsg',
      color:'Notify/getColor',
    }),
    ...mapState('Notify',[
      'snackbar',
    ]),
    snack:{
      get(){
        return this.snackbar;
      },
      set(val){
        this.updateSnackbar(val)
      }
    }
  }
}
</script>

<style scoped>

</style>