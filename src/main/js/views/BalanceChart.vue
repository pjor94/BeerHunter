<template>

  <v-card >
    <v-toolbar
        color="primary"
        dark
        dense
        extended
        flat
    >
      <!--      <v-app-bar-nav-icon></v-app-bar-nav-icon>-->
      <v-toolbar-title>Current Balance</v-toolbar-title>
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
      <v-card-title  v-if="!isLoanding" >Total in USDT: {{ balanceUSDT.toFixed(2) }} Total in BTC {{ balanceBTC.toFixed(8) }}</v-card-title>
      <v-divider></v-divider>
      <v-card-text >
        <v-layout wrap v-if="!isLoanding" justify="center">
          <v-col cols="12" >
            <DoughnutChart :chart-data="doughnutData" :styles="chartStyles"
                           :options="{responsive: true, maintainAspectRatio: false}"/>
          </v-col>
          <v-col cols="12">
            <v-data-table
                :headers="headers"
                :items="balances"
                hide-default-footer
                class="elevation-1"
            ></v-data-table>

          </v-col>
        </v-layout>
        <v-row v-else  justify="center">
          <v-progress-circular
              :size="50"
              color="primary"
              indeterminate
          ></v-progress-circular>
        </v-row>
      </v-card-text>
    </v-card>
  </v-card>





</template>

<script>
import axios from "axios";
import DoughnutChart from '../components/charts/DoughnutChart';

export default {
  name: "BalanceChart",
  components: {DoughnutChart},

  data() {
    return {
      balances: [],
      headers: [
        {
          text: 'Coin',
          align: 'start',
          value: 'symbol',
        },
        {text: 'Quantity', value: 'quantity'},
        {text: 'USDT amount', value: 'balanceUSD'},
        {text: 'BTC amount', value: 'balanceBTC'},
        {text: 'Price USDT', value: 'priceUSD'},
        {text: 'Price BTC', value: 'priceBTC'},
      ],
      doughnutData: {
        labels: ['BTC', 'ADA'],
        datasets: [
          {
            label: ['BTC', 'ADA'],
            backgroundColor: ['red', 'blue'],
            data: [1000, 90]
          },
        ]
      },
      chartStyles: {
        height: '200px',
        //width: '400px'
      },
      colorArray: null,
      coinColors: {},
      chartData: {},
      isLoanding: true,
      balanceBTC:null,
      balanceUSDT:null

    }
  },
  mounted() {
    this.getDataFromApi()
  },
  methods: {
    getDataFromApi() {
      axios.get('/api/account/balance')
          .then(response => {
            this.balances = response.data.assets
            this.balanceBTC = response.data.balanceBTC
            this.balanceUSDT = response.data.balanceUSDT
            this.assignCoinColors(this.balances)
            this.structureDougnutData(this.balances)
            this.isLoanding = false;
          })

    },
    assignCoinColors(balances) {
      this.colorArray = ['#ff584d', '#ff944d', '#6bbd59', '#59bdab', '#595ebd', '#b359bd', '#babd59', '#59acbd', '#9c59bd'];
      for (const asset of balances) {
        this.chartData[asset.symbol] = [];
        this.coinColors[asset.symbol] = this.colorArray.pop();
      }
    },
    structureDougnutData(balances) {
      let chartData = {
        labels: [],
        datasets: [{
          label: 'ASSETS',
          backgroundColor: [],
          data: []
        }]
      };
      balances.forEach(asset => {
        chartData.labels.push(asset.symbol)
        chartData.datasets[0].data.push(
            this.chartCurrency === 'BTC' ? asset.balanceBTC : asset.balanceUSD
        )
        chartData.datasets[0].backgroundColor.push(this.coinColors[asset.symbol]);
      })
      this.doughnutData = chartData;
    },
  }
}
</script>

<style scoped>

</style>