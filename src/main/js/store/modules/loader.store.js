

const state = {
    loader:false,
    msg:'',
}


// Getter functions
const getters = {
    getLoader: (state) => state.loader,
    getMsg: (state) => state.msg
}


// Actions
const actions = {
    setLoader({commit}, {loader, msg}){
        commit('updateLoader', loader);
        commit('updateMsg', msg);
    }
}
// Mutations
const mutations = {
    updateLoader : (state,loader) =>state.loader =loader,
    updateMsg : (state,msg) =>state.msg =msg,
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}