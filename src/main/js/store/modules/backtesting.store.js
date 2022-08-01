import {axios} from "@/plugins/axios";


const state = {
    pageOptions: {
        page: 0,
        size: 10
    },
    pagedData: {}
}

const getters ={
    pageOptions: (state) => state.pageOptions,
    pagedData: (state) => state.pagedData
}

const getPagedApi = ({url, page = 0, size = 10}) => {
    return axios.get(`${url}?size=${size}&page=${page}`)
}


// Actions
const actions = {
    loadData({commit}, {page, size}) {
        commit("updatePageOptions", {page, size})
        let url = '/api/historicalData/all';
        return getPagedApi({url, page, size})
            .then(({data}) => commit("updatePagedData", data))
    }

}
// Mutations
const mutations = {
    updatePageOptions: (state, payload = {page: 0, size: 10}) => state.pageOptions = payload,
    updatePagedData: (state, payload) => state.pagedData = payload,
    updatePage: (state, payload) => state.pageOptions.page = payload

}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}