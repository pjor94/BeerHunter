<template>
  <v-sheet
      class="mx-auto"
      max-width="700"
      color="rgb(255 255 255 / 0%)"
  >
    <v-slide-group
        center-active
        v-model="slide"
    >
      <v-slide-item
          v-for="candle of live_prices"
          :key="candle.pair"
          v-slot="{ /*active, toggle*/ }"
      >
        <v-chip
            class="ma-2"
            :color="candle.close> candle.open ? 'success' : 'error'"
            outlined
        >
          <v-icon left>
            mdi-server-plus
          </v-icon>
          {{candle.pair}} : {{candle.close}}
        </v-chip>

      </v-slide-item>

    </v-slide-group>
  </v-sheet>

</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "LivePricesBanner",
  data(){
    return {
      slide:null
    }
  },
  computed:{
    ...mapGetters({
      live_prices:'Socket/getLivePricesAsCandles'
    }),
  }
}
</script>

<style scoped>

</style>