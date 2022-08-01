<template>
  <v-row justify="center">
    <v-dialog
        v-model="value"
        persistent
        :fullscreen="$vuetify.breakpoint.xsOnly"
        max-width="800"
        scrollable
    >
      <v-card>
        <v-card-title>
          <span class="headline">BACK-TESTING     -  {{ bot.name }}</span>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" v-if="!started">
            <v-row >
              <v-col cols="12" sm="6" class="pa-2">
                <v-currency-field
                    label="Start Asset"
                    required
                    :rules="assetRules"
                    v-model="backtesting.baseAsset"
                    :prefix="pairSetting.baseAsset"
                    locale="IT"
                ></v-currency-field>
              </v-col>
              <v-col cols="12" sm="6" class="pa-2">
                <v-currency-field
                    label="Start Asset"
                    required
                    locale="IT"
                    :rules="quoteRules"
                    v-model="backtesting.quoteAsset"
                    :prefix="pairSetting.quoteAsset"
                ></v-currency-field>
              </v-col>
            </v-row>
            <v-row >
              <v-col cols="12" sm="6" class="pa-2">
                <v-text-field readonly :rules="startRules"  v-model="start" label="Start Date" required></v-text-field>
                <v-date-picker
                    v-model="backtesting.start"
                    :max="historicalInfo ? moment(historicalInfo.end).toISOString() : null"
                    :min="historicalInfo ? moment(historicalInfo.start).add(2,'weeks').toISOString() : null"
                    color="green lighten-1"
                ></v-date-picker>
              </v-col>
              <v-col cols="12" sm="6" class="pa-2">
                <v-text-field readonly :rules="endRules"  v-model="end" label="End Date" required></v-text-field>
                <v-date-picker
                    v-model="backtesting.end"
                    :max="historicalInfo ? moment(historicalInfo.end).toISOString() : null"
                    :min="historicalInfo ? moment(historicalInfo.start).add(2,'weeks').toISOString() : null"
                    color="green lighten-1"
                    header-color="primary"
                ></v-date-picker>
              </v-col>
            </v-row>


          </v-form>
          <v-row v-else justify="center">
            <v-progress-circular
                :size="50"
                color="primary"
                indeterminate
            ></v-progress-circular>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              v-if="!started"
              color="blue darken-1"
              text
              @click="emitClose"
          >
            Close
          </v-btn>
          <v-btn
              v-if="!started"
              color="blue darken-1"
              text
              @click="emitStart"
          >
            Start
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import utils from "@/utils";
import {mapGetters, mapState} from "vuex";
import moment from "moment";


export default {
  name: "BackTestingDialog",
  components: {},
  mixins: [utils],
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data: () => ({

  }),
  computed: {
    ...mapGetters({
      bot: 'Backtesting/getBotBackTesting',
      pairItems: 'BotConfigs/getPairConfigs',
      historicalData: 'Backtesting/getHistoricalData',
      started: 'Backtesting/getStarted',
    }),
    ...mapState('Backtesting', [
      'backtesting',
    ]),
    pairSetting() {
      let el = this.pairItems.find((el) => el.symbol === this.bot.pair);
      return el ? el : {}
    },
    historicalInfo() {
      return  !this.isEmpty(this.historicalData) ? this.historicalData.find(el => el.pair === this.bot.pair) : {};
    },
    start(){
      return this.backtesting.start ? this.moment(this.backtesting.start).format('YYYY-MM-DD HH:mm:ss') : null
    },
    end(){
      return this.backtesting.end ? this.moment(this.backtesting.end).format('YYYY-MM-DD HH:mm:ss') : null
    },
    startRules(){
      return [
        v => !!v || 'start is required',
        v => moment(v).isBefore(moment(this.end)) || 'Start must be before End',
      ]
    },
    endRules(){
      return [
        v => !!v || 'End is required',
        v => !moment(v).isBefore(moment(this.start)) || 'Start must be before End',
      ]
    },
    assetRules(){
      return [
        v => !!v || 'Asset is required',
        () => this.backtesting.baseAsset > 0 || 'Asset is required',
      ]
    },
    quoteRules(){
      return [
        v => !!v || 'Asset is required',
        () => this.backtesting.quoteAsset > 0 || 'Asset is required',
      ]
    },

  },
  methods:{
    emitStart(){
      if (this.$refs.form.validate()) {
        this.$emit('start');
      } else this.snackBarError("There are some errors")
    },
    emitClose(){
      this.$refs.form.resetValidation();
      this.$emit('close');

    }
  }
}
</script>

<style scoped>

</style>