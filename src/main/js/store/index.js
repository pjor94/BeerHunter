import Vue from 'vue'
import Vuex from 'vuex'
import modules from './modules';
Vue.use(Vuex);
const store = new Vuex.Store({
    modules, // all your modules automatically imported :)
})

export default store;