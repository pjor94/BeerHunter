<template>
	<div v-if="this.chartData">
		<line-chart :chartdata="chartData" :styles="chartStyles" :options="{responsive: true, maintainAspectRatio: false}"/>

	</div>
</template>

<script>
import axios from 'axios';
import LineChart from './charts/LineChart.js';
import moment from 'moment';

export default {
	components: {
		LineChart
	},
	data() {
		return {
			stochRSIData: [],
			chartData: null,
		}
	},
	methods:{
		async getStochRSIData() {
			try {

				const { data } = await axios({
					method:'GET',
					url:'/api/indicator/stochrsi',
					headers: {
						authToken: this.$store.state.user.token
					}
				})

				this.stochRSIData = data;

				// {
				// 	labels: [],
				// 	datasets: [
				// 		{
                //      label: symbol,
                //      stack: 1,
                //      backgroundColor: this.coinColors[symbol],
                //      data: []
                //  }];
				// }

				this.structureChartData(data)
			}catch(error) {
				console.log(error)
			}
		},
		structureChartData(data) {
			this.chartData = {
				labels: data.timestamps.map(timestamp => moment.utc(timestamp).local().format('h:mm a')),
				datasets: [
					{
						label: 'K',
						backgroundColor: '#4147ba',
						borderWidth:1,
						fill: false,
						borderColor:'#4147ba',
						data: data.stochrsi.map(point => point.k)
					},
					{
						label: 'D',
						backgroundColor: '#db841a',
						borderWidth:1,
						fill: false,
						borderColor:'#db841a',
						data: data.stochrsi.map(point => point.d)
					},
					{
						label: 'Overbought',
						backgroundColor: '#5bab74',
						borderWidth:1,
						fill: false,
						pointRadius: 0,
						borderDash:[4],
						borderColor: '#5bab74',
						data: data.stochrsi.map(point => point ? 80 : 80)
					},
					{
						label: 'Oversold',
						backgroundColor: '#ba4747',
						borderWidth:1,
						fill: false,
						pointRadius: 0,
						borderDash:[4],
						borderColor: '#ba4747',
						data: data.stochrsi.map(point => point ? 20 : 20)
					}
				]
			};

			// console.log('chartdata',this.chartData)
		},
	},
	computed: {
        chartStyles() {
            return {
				height: '400px',
				width: '95%'
            }
        }
    },
	created() {
		this.getStochRSIData()
	}
}
</script>

<style>

</style>