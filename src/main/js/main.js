import Vue from 'vue'
import App from './App.vue'
import vuetify from '@/plugins/vuetify';
import store from './store';
import  '@/plugins/Socket.io'


//routes
import router from './router.js';




new Vue({
     vuetify,
    store,
    render: h => h(App),
    router
}).$mount('#app')

import VCurrencyField from 'v-currency-field'

Vue.use(VCurrencyField, {
    locale: 'it-IT',
    decimalLength: 8,
    autoDecimalMode: true,
    min: null,
    max: null,
    defaultValue: 0,
    valueAsInteger: false,
    allowNegative: true
})

