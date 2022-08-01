<template>
	<div class="NavContainer">
		<v-row class="NavBar">
			<v-col cols="12">
				<v-row @click="routeTo('/balances')" :style="tabStyling('/balances')" >
					<v-spacer></v-spacer>
					<v-col cols="7" class="NavTab" >
						<span>Balances</span>
					</v-col>
					<v-col cols="3" class="iconContainer">
						<v-icon :color="iconColor('/balances')">fas fa-balance-scale-right</v-icon> 
					</v-col>
				</v-row>
			</v-col>
			<v-col cols="12">
				<v-row @click="routeTo('/transactions')" :style="tabStyling('/transactions')">
					<v-spacer></v-spacer>
					<v-col cols="7" class="NavTab" >
						<span>Transactions</span>
					</v-col>
					<v-col cols="3" class="iconContainer">
						<v-icon :color="iconColor('/transactions')">fas fa-dollar-sign</v-icon> 
					</v-col>
				</v-row>
			</v-col>
			<v-col cols="12">
				<v-row @click="routeTo('/stochrsi')" :style="tabStyling('/stochrsi')">
					<v-spacer></v-spacer>
					<v-col cols="7"  class="NavTab" >
						<span>Stoch RSI</span>
					</v-col>
					<v-col cols="3" class="iconContainer">
						<v-icon :color="iconColor('/stochrsi')">fas fa-chart-area</v-icon> 
					</v-col>
				</v-row>
			</v-col>
		</v-row>
		<v-row style="height:100%">
			<v-col cols="2" style="position: absolute !important; bottom: 0 !important;">
				<v-row :style="tabStyling('/sdfsdf')" @click="logout()">
				<v-spacer></v-spacer>
					<v-col cols="7"  class="NavTab" >
						<span>Log out</span>
					</v-col>
					<v-col cols="3" class="iconContainer">
						<v-icon :color="iconColor('/stochrsi')">fas fa-sign-out-alt</v-icon> 
					</v-col>
				</v-row>
			</v-col>
		</v-row>
	</div>

</template>

<script>
export default {
	data() {
		return {
			currentTab: this.$router.history.current.fullPath
		}
	},
	methods: {
		logout() {
			this.$router.push('/login');
			this.$store.commit('clearAuth');
		},
		routeTo(route) {
			this.currentTab = route;
			this.$router.push(route);
		},
		tabStyling(tab) {

			let defaultCss = {
				'font-family': 'Nunito',
				'font-size': '18px',
				'padding': '10px 0 10px 0',
				'transition': '0.4s'
			};

			
			if (tab === this.currentTab) {
				return {
					...defaultCss,
					color:'rgb(27, 116, 0)',
					borderRight: '1px solid rgb(27, 116, 0)',
				}
			} else {
				return {
					...defaultCss,
					color: 'antiquewhite'
				}
			}
		},
		iconColor(path) {
			return this.currentTab === path ? 'rgb(27, 116, 0)' : 'rgb(250, 235, 215)';
		}
	}

}
</script>

<style scoped>

.NavContainer {
	background-color: rgb(36, 36, 36);
	height:100vh;
}

.NavBar {
	margin: 0 0 0 0 !important;
}

.NavTab:hover {
	cursor: pointer;
	color: rgb(147, 204, 131) !important;
}

.iconContainer {
	text-align: center;
}

</style>