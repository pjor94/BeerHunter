<template>
  <v-card>
    <v-toolbar
        color="grey"
        dark
        dense
        extended
        flat
    >
      <!--      <v-app-bar-nav-icon></v-app-bar-nav-icon>-->
      <v-toolbar-title>Transaction History</v-toolbar-title>
    </v-toolbar>

    <v-card flat
            class="mx-5"
            style="margin-top: -48px;"
    >

      <v-divider></v-divider>
      <v-card-title> Totals {{ total }} BUYS {{ totalBuys }} SELLS {{ totalSells }}</v-card-title>
      <v-divider></v-divider>
      <!--      <v-card-text>-->
      <v-data-table
          v-if="!isLoanding"
          :headers="headers"
          :items="transaction"
      >
        <template v-slot:item.type="{ item }">
          <v-chip
              :color="getColor(item.type)"
              dark
          >
            {{ item.type }}
          </v-chip>
        </template>
        <template v-slot:item.createdAt="{ item }">
          {{ moment.utc(item.createdAt).local().format('MMMM Do YY, h:mm a') }}
        </template>

      </v-data-table>
      <v-row v-else justify="center">
        <v-progress-circular
            :size="50"
            color="primary"
            indeterminate
        ></v-progress-circular>
      </v-row>
      <!--      </v-card-text>-->
    </v-card>
  </v-card>
</template>

<script>
import axios from "axios";
import moment from 'moment';


export default {
  name: "Transactions",
  data() {
    return {
      transaction: [],
      headers: [
        {
          text: 'Type',
          align: 'start',
          value: 'type',
        },
        {text: 'Asset', value: 'symbol'},
        {text: 'Quantity', value: 'price'},
        {text: 'Quantity', value: 'quantity'},
        {text: 'Date', value: 'createdAt'},
      ],
      isLoanding: true
    }
  },
  mounted() {
    this.getDataFromApi();
  },
  methods: {
    async getDataFromApi() {
      const {data} = await axios.get('/api/account/transactions');
      const {transactions} = data;
      this.transaction = transactions;
      this.isLoanding = false
    },
    getColor(type) {
      return type === "BUY" ? 'success' : 'error'
    }
  },
  computed: {
    totalSells() {
      return this.transaction.filter((el) => el.type === 'SELL').length
    },
    totalBuys() {
      return this.transaction.filter((el) => el.type === 'BUY').length
    },
    total() {
      return this.transaction.length;
    },
    moment
  }
}
</script>

<style scoped>

</style>