<template>
  <!--  <v-container>-->
  <!--    <v-form >-->
  <!--      <v-row>-->
  <!--        <v-col-->
  <!--            cols="12"-->
  <!--            md="6">-->
  <!--          <v-autocomplete-->
  <!--              v-model="botConfig.pair"-->
  <!--              :items="pairItems"-->
  <!--              :loading="pairIsLoading"-->
  <!--              :search-input.sync="pairSearch"-->
  <!--              chips-->
  <!--              clearable-->
  <!--              dense-->
  <!--              hide-selected-->
  <!--              item-text="symbol"-->
  <!--              item-value="symbol"-->
  <!--              label="Search for a coin..."-->
  <!--          >-->
  <!--            <template v-slot:no-data>-->
  <!--              <v-list-item>-->
  <!--                <v-list-item-title>-->
  <!--                  Search for your favorite-->
  <!--                  <strong>Cryptocurrency</strong>-->
  <!--                </v-list-item-title>-->
  <!--              </v-list-item>-->
  <!--            </template>-->
  <!--            <template v-slot:selection="{ attr, on, item, selected }">-->
  <!--              <v-chip-->
  <!--                  v-bind="attr"-->
  <!--                  :input-value="selected"-->
  <!--                  color="blue-grey"-->
  <!--                  class="white&#45;&#45;text"-->
  <!--                  v-on="on"-->
  <!--              >-->
  <!--                <v-icon left>-->
  <!--                  mdi-bitcoin-->
  <!--                </v-icon>-->
  <!--                <span v-text="item.symbol"></span>-->
  <!--              </v-chip>-->
  <!--            </template>-->
  <!--            <template v-slot:item="{ item }">-->
  <!--              <v-list-item-avatar-->
  <!--                  color="indigo"-->
  <!--                  class="headline font-weight-light white&#45;&#45;text"-->
  <!--              >-->
  <!--                {{ item.symbol.charAt(0) }}-->
  <!--              </v-list-item-avatar>-->
  <!--              <v-list-item-content>-->
  <!--                <v-list-item-title v-text="item.symbol"></v-list-item-title>-->
  <!--                <v-list-item-subtitle v-text="item.symbol"></v-list-item-subtitle>-->
  <!--              </v-list-item-content>-->
  <!--              <v-list-item-action>-->
  <!--                <v-icon>mdi-bitcoin</v-icon>-->
  <!--              </v-list-item-action>-->
  <!--            </template>-->
  <!--          </v-autocomplete>-->
  <!--        </v-col>-->
  <!--        <v-col-->
  <!--            cols="12"-->
  <!--            md="4">-->
  <!--          <v-text-field-->
  <!--              v-model="botConfig.pair"-->
  <!--              :counter="10"-->
  <!--              label="First name"-->
  <!--              required-->
  <!--          ></v-text-field>-->
  <!--        </v-col>-->

  <!--        <v-col-->
  <!--            cols="12"-->
  <!--            md="4"-->
  <!--        >-->
  <!--          <v-text-field-->
  <!--              v-model="botConfig.pair"-->
  <!--              :counter="10"-->
  <!--              label="Last name"-->
  <!--              required-->
  <!--          ></v-text-field>-->
  <!--        </v-col>-->
  <!--      </v-row>-->
  <!--    </v-form>-->

  <!--  </v-container>-->
  <v-card >
    <v-toolbar
        color="primary"
        dark
        dense
        extended
        flat
    >
<!--      <v-app-bar-nav-icon></v-app-bar-nav-icon>-->
      <v-toolbar-title>Back Testing</v-toolbar-title>
    </v-toolbar>

    <v-card flat
        class="mx-5"
        style="margin-top: -48px;"
    >
<!--      <v-toolbar flat dense>-->
<!--        <v-toolbar-title class="grey&#45;&#45;text"> Bot Configuration</v-toolbar-title>-->
<!--        <v-spacer></v-spacer>-->
<!--      </v-toolbar>-->
      <v-divider></v-divider>
      <v-card-title>Pippo pluto</v-card-title>
      <v-divider></v-divider>
      <v-card-text >
      </v-card-text>
    </v-card>
  </v-card>
</template>

<script>


export default {
  name: "BackTesting",
  data() {
    return {
      botConfig: {
        StochasticRSI: {
          rsiPeriod: 14,
          stochasticPeriod: 14,
          kPeriod: 5,
          dPeriod: 2
        },
        MACD: {
          fastPeriod: 12,
          slowPeriod: 26,
          signalPeriod: 9
        },
        CCI: {
          period: 20
        },
        IchimokuCloud: {
          conversionPeriod: 9,
          basePeriod: 26,
          spanPeriod: 52,
          displacement: 26
        },
        EMA: {
          period: 9
        }
      }
    }
  }
}
</script>

<style scoped>

</style>