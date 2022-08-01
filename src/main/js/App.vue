<template>
  <v-app>
    <v-app-bar
        app
        flat
        v-if="loggedIn"
    >
      <v-container class="py-0 fill-height">
        <v-avatar
            class="mr-10"
            color="grey darken-1"
            size="50"
        >
          <img
              :src="logo"
              alt="Beer Hunter "
          >

        </v-avatar>
        <live-prices-banner></live-prices-banner>
      </v-container>
    </v-app-bar>
    <v-main>
      <v-container>
        <v-row>
          <v-col cols="2" v-if="loggedIn">
            <v-sheet >
              <main-menu></main-menu>
            </v-sheet>
          </v-col>
          <v-col cols="10">
            <v-sheet
                min-height="70vh"
                rounded="lg"
            >
              <!--              <v-row>-->
              <!--                <v-col cols="12">-->
              <router-view></router-view>
              <!--                </v-col>-->
              <!--              </v-row>-->
            </v-sheet>
          </v-col>
        </v-row>
        <Loader></Loader>
        <SnackBar></SnackBar>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import MainMenu from "@/views/MainMenu";
import logo from '@/assets/BeerHunterLogo.jpg'

import {mapActions, mapGetters} from 'vuex'
import Loader from "@/components/Loader"
import SnackBar from "@/components/SnackBar";
import LivePricesBanner from "@/components/LivePricesBanner";


export default {
  name: 'App',
  components: {
    LivePricesBanner,
    SnackBar,
    Loader,
    MainMenu
  },
  data() {
    return {
      logo,
      slide:null
    }
  },
  created() {
    this.checkLogin();
  },
  sockets: {
    connect() {
      console.log('socket connected')
    },
    customEmit() {
      console.log('this method was fired by the socket server. eg: io.emit("customEmit", data)')
    }
  },
  computed: {
    ...mapGetters({
      loggedIn:'Auth/getIsLoggedIn'
    })
  },
  methods:{
    ...mapActions({
      checkLogin:'Auth/checkLocalStorage'
    })
  }
}
</script>

<style>
</style>
