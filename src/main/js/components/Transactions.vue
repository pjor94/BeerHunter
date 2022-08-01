<template>
  <v-row  v-if="filteredTransactions.length">
    <v-col cols="5">
      <span class="transactionsHeader">Transaction History</span>
    </v-col>
    <v-col cols="1">
      <v-radio-group v-model="transactionTypeFilter" dark>
        <v-radio
            label="Buys"
            value="BUY"
        ></v-radio>
        <v-radio
            label="Sells"
            value="SELL"
        ></v-radio>
        <v-radio
            label="ALL"
            value="ALL"
        ></v-radio>
      </v-radio-group>
    </v-col>
    <v-col cols="3">
      <v-row>
        <v-col cols="6" class="statContainer pb-0"> Total # of Buys:
          <div>{{ statistics.numOfBuys }}</div>
        </v-col>
        <v-col cols="6" class="statContainer pb-0"> Total # of Sells:
          <div>{{ statistics.numOfSells }}</div>
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="12" class="pt-0">
      <v-card dark>
        <v-row style="margin:0 0 0 0 !important;">
          <v-col cols="12" class="pa-0">
            <v-row style="margin:0 0 0 0 !important; borderBottom:1px solid black !important;">
              <v-col cols="1" class="tableRowHeader">Type</v-col>
              <v-col class="tableRowHeader">Asset</v-col>
              <v-col class="tableRowHeader">Price</v-col>
              <v-col class="tableRowHeader">Quantity</v-col>
              <v-col class="tableRowHeader">Date</v-col>
            </v-row>
          </v-col>
          <v-col cols="12" class="px-0 pt-0">
            <v-row v-for="transaction in filteredTransactions.slice(0 + 15 * page, 15 + 15 * page)"
                   :key="transaction.createdAt" class="tableRow" align-content="center">
              <v-col cols="1" class="tableCell" :style="color(transaction.type)">
                {{ transaction.type }}
              </v-col>
              <v-col class="tableCell">
                {{ transaction.symbol }}
              </v-col>
              <v-col class="tableCell">
                $ {{ Number(transaction.price).toFixed(2) }}
              </v-col>
              <v-col class="tableCell">
                {{ transaction.quantity }}
              </v-col>
              <v-col class="tableCell">
                {{ moment.utc(transaction.createdAt).local().format('MMMM Do YY, h:mm a') }}
              </v-col>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col class="mx-3">
            <v-icon class="mx-2" @click="previousSet()">fas fa-chevron-left</v-icon>
            {{ this.page * 15 + 1 }} to {{
              (this.page + 1) * 15 > filteredTransactions.length ? filteredTransactions.length : (this.page + 1) * 15
            }}
            <v-icon class="mx-2" @click="nextSet()">fas fa-chevron-right</v-icon>
          </v-col>
        </v-row>
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
import axios from 'axios';
import moment from 'moment';

export default {
  data() {
    return {
      transactions: [],
      filteredTransactions: [],
      moment: moment,
      transactionTypeFilter: 'ALL',
      statistics: {},
      page: 0
    }
  },
  methods: {
    async getTransactionData() {
      const {data} = await axios({
        method: 'GET',
        url: '/api/account/transactions',
        headers: {
          authToken: this.$store.state.user.token
        }
      });


      this.filteredTransactions = data.transactions;
      this.transactions = data.transactions;
      this.statistics = data.statistics;

      // console.log(this.transactions)
    },
    color(type) {
      return type === 'BUY' ? {color: 'green'} : {color: 'red'};
    },
    previousSet() {
      if (this.page == 0) {
        // do nothing
      } else {
        this.page--;
      }
    },
    nextSet() {
      if (this.page >= Math.floor(this.filteredTransactions.length / 15)) {
        // do nothing
      } else {
        this.page++;
      }
    }
  },
  created() {
    this.getTransactionData();
  },
  watch: {
    transactionTypeFilter() {
      if (this.transactionTypeFilter === 'ALL') {
        this.filteredTransactions = this.transactions;
      } else {
        this.filteredTransactions = this.transactions.filter(transaction => {
          if (transaction.type === this.transactionTypeFilter) {
            return transaction;
          }
        });
      }
    }
  }
}
</script>

<style scoped>
.transactionsHeader {
  font-size: 40px;
  font-family: 'Nunito';
  border-bottom: 1px solid antiquewhite;
  margin-left: 10px;
  padding: 0 100px 5px 5px;
  color: rgb(250, 235, 215);
}


.statContainer {
  color: antiquewhite;
}

.tableRowHeader {
  text-align: center;
}

.tableRow {
  height: 35px;
  border-bottom: 1px solid rgb(48, 47, 46);
  padding-left: 0 !important;
  margin: 0 0 0 0 !important;
}

.tableCell {
  text-align: center;
}

table {
  font-family: 'Nunito';
  width: 100%;
}


</style>