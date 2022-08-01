import {axios} from "@/plugins/axios";


const state = {
    strategies:[],
    editStrategy:{
        name:'',
        config:'',
        strategy:'',
    },
}


// Getter functions
const getters = {
    getEditStrategy: (state)=> state.editStrategy,
    getStrategies:(state) => state.strategies,
}


// Actions
const actions = {
    loadStrategies({ commit }){
        return new Promise((resolve)=>{
            axios.get('/api/strategies/all')
                .then(({data})=> {
                    commit('updateStrategies', data);
                    resolve();
                })
        })
    },
    edit({ commit },strategy){
        return new Promise((resolve)=>{
            axios.get(`/api/strategies/edit/${strategy}`)
                .then(({data})=> {
                    commit('updateEditStrategy', data);
                    resolve();
                })
        })
    },
    saveStrategy({ state }){
        return new Promise((resolve)=>{
            axios.post(`/api/strategies/edit/${state.editStrategy.name}`,state.editStrategy)
                .then(()=> {
                    resolve();
                })
        })
    },
}
// Mutations
const mutations = {
    updateEditStrategy:(state,editBot) => state.editStrategy = editBot,
    updateStrategies:(state,strategies) =>{
        state.strategies =strategies
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}