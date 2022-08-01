<template>
  <v-row
      class="mb-6"
      justify="center"
      no-gutters
  >
    <v-col sm="6" cols="12">
      <v-card>
        <v-img
            :src="logo"
            aspect-ratio="1.2"


        ></v-img>
        <v-card-text class="pt-4">
          <v-form ref="form">
            <v-text-field
                v-model="username"
                type="text"
                label="Username"
                autocomplete="new-password"
                required
            ></v-text-field>
            <v-text-field
                label="Enter your password"
                v-model="password"
                min="8"
                :error-messages="error"
                autocomplete="new-password"
                :append-icon="e1 ? 'far fa-eye' : 'far fa-eye-slash'"
                @click:append="() => (e1 = !e1)"
                :type="e1 ? 'password' : 'text'"
                counter
                required
            ></v-text-field>

          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="login" text>Login</v-btn>
        </v-card-actions>
      </v-card>

    </v-col>
  </v-row>

</template>

<script>
import logo from '@/assets/BeerHunterLogo.jpg'
import {mapGetters, mapActions} from "vuex";
import utils from "@/utils";

export default {
  components: {},
  mixins: [utils],
  data() {
    return {
      username: null,
      password: null,
      register: false,
      error: null,
      e1: true,
      logo

    }
  },
  methods: {
    ...mapActions({
      doLogin: 'Auth/doLogin'
    }),
    login() {
      let {password, username} = this;
      this.doLogin({password, username})
          .then(() => {
            this.snackBarSuccess("loged successful")
            this.$router.push({
              name: 'HistoricalData'
            });
          })
          .catch(() => {
            this.snackBarError("username or password invalid")
            this.error = "username or password invalid";
          })
    }
  },
  computed: {
    ...mapGetters({
      loggedIn: 'Auth/getIsLoggedIn'
    })
  },
  created() {
    if (this.loggedIn) {
      this.$router.push({
        name: 'HistoricalData'
      });
    }
  }
}
</script>

<style>

</style>