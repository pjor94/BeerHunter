

const state = {
    snackbar:false,
    msg:'',
    color:null,
}


// Getter functions
const getters = {
    getSnackbar: (state) => state.snackbar,
    getMsg: (state) => state.msg,
    getColor: (state) => state.color
}


// Actions
const actions = {
    setSnackbar({commit}, {snackbar, msg, color}){
        commit('updateSnackbar', snackbar);
        commit('updateMsg', msg ? msg : '');
        commit('updateColor', color);
    }
}
// Mutations
const mutations = {
    updateSnackbar : (state,snackbar) =>state.snackbar =snackbar,
    updateMsg : (state,msg) =>state.msg =msg,
    updateColor : (state,color) =>state.color =color,
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}