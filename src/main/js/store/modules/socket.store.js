



const state = {
    livePrices: {},
    livePricesAsCandle: {},
}


// Getter functions
const getters = {
    getLivePrices: (state)=> state.livePrices,
    getLivePricesAsCandles: (state)=> state.livePricesAsCandle,
}


// Actions
const actions = {

    socket_livePrices({commit,getters},candle){
        commit('updatePrices',candle)
        commit('updatePricesAsCandle',Object.keys(getters.getLivePrices).map((el)=>getters.getLivePrices[el]))
    }
}
// Mutations
const mutations = {
    updatePrices: (state,candle) => state.livePrices[candle.pair] = candle,
    updatePricesAsCandle: (state,livePricesAsCandle) => state.livePricesAsCandle = livePricesAsCandle,
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}