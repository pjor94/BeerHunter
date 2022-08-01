<template>
  <v-row v-if="dataCollection">
    <v-col cols="12">
      <v-row>
        <v-col cols="12">
          <BarChart :chart-data="dataCollection" :styles="chartStyles"
                    :options="{responsive: true, maintainAspectRatio: false}"/>
        </v-col>
        <v-col cols="1">
          <v-radio-group v-model="chartCurrency" dark>
            <v-radio
                label="BTC"
                value="BTC"
            ></v-radio>
            <v-radio
                label="USD"
                value="USD"
            ></v-radio>
          </v-radio-group>
        </v-col>
        <v-col cols="1">
          <v-radio-group v-model="timeframe" dark>
            <v-radio
                label="2h"
                value="2h"
            ></v-radio>
            <v-radio
                label="1d"
                value="1d"
            ></v-radio>
          </v-radio-group>
        </v-col>
        <v-col cols="4">
          <div class="doughnutTitle">Current Balance:
            {{ this.chartCurrency === 'BTC' ? this.accountBalance + ' BTC' : '$ ' + this.accountBalance }}
          </div>
          <DoughnutChart :chart-data="doughnutData" :styles="chartStyles"
                         :options="{responsive: true, maintainAspectRatio: false}"/>
        </v-col>
      </v-row>
    </v-col>
  </v-row>

</template>

<script>
import axios from 'axios';
import moment from 'moment';
import BarChart from './charts/BarChart.js';
import DoughnutChart from './charts/DoughnutChart.js';

export default {
  components: {
    BarChart,
    DoughnutChart
  },
  data() {
    return {
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
      balanceData: null,
      accountBalance: null,
      dataCollection: null,
      chartCurrency: 'BTC',
      timeframe: '2h',
      colorArray: null,
      coinColors: {},
      chartData: {}
    }
  },
  async created() {
    this.getBalanceData();
  },
  methods: {
    async getBalanceData() {
      try {

        let {data} = await axios({
          method: 'GET',
          url: `/api/account/balances/${this.timeframe}`,
          headers: {
            authToken: this.$store.state.user.token
          }
        })

        this.balanceData = data

        this.assignCoinColors(this.balanceData);
        this.structureDougnutData(this.balanceData[this.balanceData.length - 1]);
        this.structureChartData(this.balanceData);

        if (this.chartCurrency === 'BTC') {
          this.accountBalance = this.balanceData[this.balanceData.length - 1]['balanceBTC'].toFixed(3)
        } else {
          this.accountBalance = this.balanceData[this.balanceData.length - 1]['balanceUSD'].toFixed(2)
        }

      } catch (error) {
        console.log(error);
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

      balances.assets.forEach(asset => {
        chartData.labels.push(asset.symbol)

        chartData.datasets[0].data.push(
            this.chartCurrency === 'BTC' ? asset.balanceBTC : asset.balanceUSD
        )

        chartData.datasets[0].backgroundColor.push(this.coinColors[asset.symbol]);
      })


      this.doughnutData = chartData;
    },
    structureChartData() {

      let newData = {
        labels: [],
        datasets: []
      }


      this.balanceData.forEach(balances => {

        newData.labels.push(moment.utc(balances.createdAt).local().format('MMM Do YY, HH:mm'));


        for (const assetStored in this.chartData) {
          let assetExistsInBalance = false;
          balances.assets.forEach(asset => {
            if (asset.symbol === assetStored) {
              if (this.chartCurrency === 'BTC') {
                this.chartData[asset.symbol].push(Math.round(1000 * asset.balanceBTC) / 1000);
              } else {
                this.chartData[asset.symbol].push(Math.round(asset.balanceUSD));
              }

              assetExistsInBalance = true;
            }
          });

          if (!assetExistsInBalance) {
            this.chartData[assetStored].push(0);
          }
        }
      })

      for (const symbol in this.chartData) {


        let dataset = {
          label: symbol,
          stack: 1,
          backgroundColor: this.coinColors[symbol],
          data: []
        };

        this.chartData[symbol].forEach(amount => {
          dataset.data.push(amount);
        })

        newData.datasets.push(dataset)
      }

      this.dataCollection = newData;
    },
    assignCoinColors(balances) {

      this.colorArray = ['#ff584d', '#ff944d', '#6bbd59', '#59bdab', '#595ebd', '#b359bd', '#babd59', '#59acbd', '#9c59bd'];

      let mostAssetsBalance = {};
      let mostAssets = 0;
      balances.forEach(balance => {
        if (balance.assets.length > mostAssets) {
          mostAssetsBalance = balance;
          mostAssets = balance.assets.length;
        }
      });

      for (const asset of mostAssetsBalance.assets) {

        this.chartData[asset.symbol] = [];
        this.coinColors[asset.symbol] = this.colorArray.pop();
      }

    }
  },
  computed: {
    chartStyles() {
      return {
        height: '400px',
        width: '98%'
      }
    }
  },
  watch: {
    chartCurrency() {
      this.getBalanceData();
    },
    timeframe() {
      this.getBalanceData();
    }
  }
}
</script>

<style scoped>

.doughnutTitle {
  color: rgb(207, 207, 207);
  font-family: 'Nunito';
  text-align: center;
  font-size: 25px;
}

</style>