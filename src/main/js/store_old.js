import Vue from 'vue';
import Vuex from 'vuex';
import VuexPersist from 'vuex-persist';


Vue.use(Vuex)

const vuexPersist = new VuexPersist({
    key: 'binance-bot',
    storage: window.localStorage
});


export default new Vuex.Store({
    state: {
        user: {
            loggedIn: null,
            token: null,
            data: null
        }
    },
    mutations: {
        updateAuth(state, data) {
            state.user = {
                data,
                loggedIn: !!localStorage.getItem('token'),
                token: localStorage.getItem('token'),
            };
        },
        clearAuth(state) {
            state.user = {
                loggedIn: false,
                token: null,
                data: null
            }
        },
    },
    plugins: [vuexPersist.plugin],
})