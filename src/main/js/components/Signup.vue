<template>
<!--	<v-row>-->
<!--		<v-col>-->
<!--			<input v-model="username" type="text" style="backgroundColor: white !important;">-->
<!--			<input v-model="password" type="password" style="backgroundColor: white !important;">-->
<!--			<v-btn @click="signup()">Signup</v-btn>-->
<!--		</v-col>-->
<!--	</v-row>-->

  <v-card>
    <v-card-text class="pt-4">
      <div>
        <v-form  ref="form">
          <v-text-field
              v-model="username"
              type="text"
              required
          ></v-text-field>
          <v-text-field
              label="Enter your password"
              v-model="password"
              min="8"
              :append-icon="e1 ? 'visibility' : 'visibility_off'"
              :append-icon-cb="() => (e1 = !e1)"
              :type="e1 ? 'password' : 'text'"
              counter
              required
          ></v-text-field>
          <v-layout justify-space-between>
            <v-btn @click="signup" >Signup</v-btn>
          </v-layout>
        </v-form>
      </div>
    </v-card-text>
  </v-card>
  
</template>

<script>
import axios from 'axios';

export default {
	data() {
		return {
			username: null,
			password: null,
      e1:false
		}
	},
	methods: {
		async signup() {
			let jwt = await axios.post('/api/user/signup', { username: this.username, password: this.password});
			console.log(jwt)
		}
	},
	created() {
	}
}
</script>

<style>

</style>