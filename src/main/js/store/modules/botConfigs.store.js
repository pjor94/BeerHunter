

// State object
import {axios} from "@/plugins/axios";

const state = {
    botConfigs : [],
    editBot: {},
    strategies:[],
    strategiesConfigs:[],
    defaultTraderConfig:{},
    pairConfigs:[],
}


// Getter functions
const getters = {
    getBotConfigs: (state)=> state.botConfigs,
    getEditBot:(state)=> state.editBot,
    getStrategies:(state) => state.strategies,
    getStrategiesConfigs: (state) =>  state.strategiesConfigs,
    getDefaultTraderConfig: (state) =>  state.defaultTraderConfig,
    getPairConfigs: (state) =>  state.pairConfigs,

}


// Actions
const actions = {
    loadBotConfigs({ commit }){
        return new Promise((resolve)=>{
            axios.get('/api/botConfig/all')
                .then(({data})=> {
                    commit('updateBotConfigs', data);
                    resolve();
                })
        })
    },
    saveBotConfig({commit,dispatch,state}){
        return new Promise((resolve)=>{
            axios.post('/api/botConfig/save',state.editBot)
                .then(({data})=> {
                    dispatch('loadBotConfigs', data)
                        .then(()=>{
                            commit('updateEditBot',{})
                            resolve();
                        })

                })
        })
    },
    loadDefaultTraderConfig({ commit }){
        return new Promise((resolve)=>{
            axios.get('/api/botConfig/getDefaultTradeConfig')
                .then(({data})=> {
                    commit('updateDefaultTraderConfig', data);
                    resolve();
                })
        })
    },
    loadStrategies({ commit }){
        return new Promise((resolve)=>{
            axios.get('/api/botConfig/getStrategies')
                .then(({data:{strategies, strategiesConfig}})=> {
                    commit('updateStrategies', strategies);
                    commit('updateStrategiesConfigs', strategiesConfig);
                    resolve();
                })
        })
    },
    loadPairConfigs({ commit }){
        return new Promise((resolve)=>{
            axios.get('/api/backTesting/getAccountInfo')
                .then(({data:{symbols}})=> {
                    commit('upadatePairConfigs', symbols.filter((el) => el.isSpotTradingAllowed));
                    resolve();
                })
        })
    },
    loadDialogData({dispatch}){
        return Promise.all([
            dispatch('loadPairConfigs'),
            dispatch('loadStrategies'),
            dispatch('loadDefaultTraderConfig'),
        ])
    }
}

// Mutations
const mutations = {
    updateBotConfigs: (state, payload) => {
        state.botConfigs = payload;
    },
    updateEditBot:(state,playload)=>{
        if(playload){
            state.editBot = playload;
        }else{
            state.editBot = {
                config:state.defaultTraderConfig,
                strategyConfig:{}
            };
        }
    },
    updateStrategies:(state,strategies) =>{
        state.strategies =strategies
    },
    updateStrategiesConfigs:(state,strategiesConfigs) =>{
        state.strategiesConfigs =strategiesConfigs
    },
    updateDefaultTraderConfig:(state,defaultTraderConfig) =>{
        state.defaultTraderConfig =defaultTraderConfig
    },
    upadatePairConfigs:(state,pairConfigs) =>{
        state.pairConfigs =pairConfigs
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}