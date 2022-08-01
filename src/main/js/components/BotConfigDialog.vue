<template>
  <v-row justify="center">
    <v-dialog
        v-model="value"
        persistent
        :fullscreen="$vuetify.breakpoint.xsOnly"
        max-width="auto"
        scrollable
    >
      <v-card>
        <v-card-title>
          <span class="headline">{{ title }}</span>
        </v-card-title>
        <v-card-text>
          <v-form ref="form">
            <v-text-field
                label="Bot Name"
                v-model="editBot.name"
                :rules="nameRules"
                required>
            </v-text-field>
            <v-autocomplete
                v-model="editBot.pair"
                :items="pairItems"
                :loading="pairIsLoading"
                :rules="pairRules"
                clearable
                @change="changePair"
                hide-selected
                item-text="symbol"
                item-editBot="symbol"
                label="Search for a Pair..."
            >
              <template v-slot:no-data>
                <v-list-item>
                  <v-list-item-title>
                    Search CryptoCurrency Pair
                    <strong>Cryptocurrency</strong>
                  </v-list-item-title>
                </v-list-item>
              </template>
              <template v-slot:item="{ item }">
                <v-list-item-avatar
                    color="indigo"
                    class="headline font-weight-light white--text"
                >
                  {{ item.symbol.charAt(0) }}
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title v-text="item.symbol"></v-list-item-title>
                  <v-list-item-subtitle v-text="item.symbol"></v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <v-icon>mdi-bitcoin</v-icon>
                </v-list-item-action>
              </template>
            </v-autocomplete>
            <v-autocomplete
                v-model="editBot.timeframe"
                :rules="timeframeRule"
                :items="['1m','3m','5m',/*'15m',*/'30m','1h',/*'2h',*/'4h'/*,'6h','8h','12h','1d'*/]"
                :loading="pairIsLoading"
                chips
                clearable
                dense
                hide-selected
                label="TimeFrame"
            ></v-autocomplete>
            <v-autocomplete
                v-model="editBot.strategy"
                :rules="strategyRule"
                :items="strategies"
                chips
                clearable
                dense
                hide-selected
                @change="changeStrategy"
                label="Strategy "
            ></v-autocomplete>
          </v-form>
          <v-row>
            <v-col cols="12" sm="6">
              <strong>Trader Configuration</strong>
              <v-jsoneditor v-model="editBot.config" v-if="value" :options="options" :plus="false" height="400px"
                            @error="onError"></v-jsoneditor>
            </v-col>
            <v-col cols="12" sm="6">
              <strong>Strategy Config</strong>
              <v-jsoneditor v-model="editBot.strategyConfig" v-if="value" :options="options" :plus="false"
                            height="400px"
                            @error="onError"></v-jsoneditor>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              color="blue darken-1"
              text
              @click="close"
          >
            Close
          </v-btn>
          <v-btn
              color="blue darken-1"
              text
              @click="save"
          >
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import {mapGetters, mapState} from "vuex";
import utils from "@/utils";

export default {
  name: "BotConfigDialog",
  mixins: [utils],
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  data: () => ({
    options: {
      mode: 'code'
    },
    pairIsLoading: false,
    nameRules: [
      v => !!v || 'Name is required',
    ],
    pairRules: [
      v => !!v || 'pair is required',
    ],
    timeframeRule: [
      v => !!v || 'timeframe is required',
    ],
    strategyRule: [
      v => !!v || 'strategy is required',
    ],
  }),
  computed: {
    ...mapGetters({
      pairItems: 'BotConfigs/getPairConfigs',
      defaultTraderConfig: 'BotConfigs/getDefaultTraderConfig',
      strategiesConfig: 'BotConfigs/getStrategiesConfigs',
      strategies: 'BotConfigs/getStrategies'
    }),
    ...mapState('BotConfigs', [
      'editBot',
    ]),
    title() {
      return this.isEmpty(this.editBot._id) ? 'New bot Configuration' : 'Edit Bot Configuration'
    }
  },
  methods: {
    save() {
      if (this.validate()) {
        this.$emit('save');
      } else this.snackBarError("There are some errors")
    },
    close() {
      this.$emit('close');
      this.reset();
    },
    onError() {

    },
    validate() {
      return this.$refs.form.validate()
    },
    reset() {
      this.$refs.form.reset()
    },
    changeStrategy(val) {
      if (val) {
        this.editBot.strategyConfig = this.strategiesConfig[val];
      }
    },
    changePair(val) {
      if (val) {
        let {filters} = this.pairItems.find((el) => el.symbol === val);
        let {minNotional} = filters.find((el) => el.filterType === 'MIN_NOTIONAL');
        this.editBot.config = this.editBot.config ? this.editBot.config : {};
        this.editBot.config.minOrderQuoteAsset = Number(minNotional);
      }
    }
  },
  watch: {}
}
</script>

<style scoped>

</style>