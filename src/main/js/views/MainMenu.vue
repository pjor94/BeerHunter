<template>
  <v-list rounded
          color="primary"
          dark
  >
    <v-list-item
        v-for="n in menu"
        :key="n.title"
        link
        :to="n.to"
    >
      <v-list-item-content>
        <v-list-item-title>
          {{ n.title }}
          <v-spacer></v-spacer>
        </v-list-item-title>
      </v-list-item-content>
      <v-list-item-action>
        <v-icon>{{ n.icon }}</v-icon>
      </v-list-item-action>
    </v-list-item>

    <v-divider class="my-2"></v-divider>

    <v-list-item
        link
        color="grey lighten-4"
        @click="logout"
    >
      <v-list-item-content>
        <v-list-item-title>
          Log Out
        </v-list-item-title>
      </v-list-item-content>
      <v-list-item-action>
        <v-icon>fas fa-sign-out-alt</v-icon>
      </v-list-item-action>
    </v-list-item>
  </v-list>
</template>

<script>
import {mapActions} from "vuex";

export default {
  name: "MainMenu",
  data() {
    return {
      menu: [
        {
          title: 'Balances',
          to: '/balances',
          icon: 'fas fa-balance-scale-right'
        },
        {
          title: 'Transactions',
          to: '/transactions',
          icon: 'fas fa-dollar-sign'
        },
        {
          title: 'Bot Configs',
          to: '/botConfiguration',
          icon: 'fas fa-dollar-sign'
        },
        {
          title: 'Strategies',
          to: '/strategies',
          icon: 'fas fa-dollar-sign'
        },
        {
          title: 'Historical Data',
          to: '/HistoricalData',
          icon: 'fas fa-dollar-sign'
        },
        {
          title: 'Stoch RSI',
          to: '/stochrsi',
          icon: 'fas fa-chart-area'
        },
        {
          title: 'BackTesting',
          to: '/backTesting',
          icon: 'fas fa-chart-area'
        },
        {
          title: 'Settings',
          to: '/settings',
          icon: 'fas fa-cogs'
        },
      ]
    }
  },
  methods: {
    ...mapActions({
      doLogout:'Auth/doLogout'
    }),
    logout(){
      this.doLogout()
      .then(()=>{
        this.$router.push("/login")
      })
    }
  }

}
</script>

<style scoped>

</style>