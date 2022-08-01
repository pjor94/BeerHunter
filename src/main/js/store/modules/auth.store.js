

// State object
import {axios} from "@/plugins/axios";
import jwt from "jsonwebtoken";

const state = {
    user: null,
    loggedIn:null,
    token:null,
    data:null,
    authorities:[]
}


// Getter functions
const getters = {
    getUser( state ) {
        return state.user;
    },
    getIsLoggedIn( state ) {
        return state.loggedIn;
    },
    getToken(state){
        return state.token;
    },
    getAuthorities(state){
        return state.authorities;
    }

}


// Actions
const actions = {
    doLogin({commit}, {username,password}){
        return new Promise((resolve,reject)=>{
            axios.post('/login', {username,password})
                .then(({data: {token, authorities}})=> {
                    commit('updateToken',token);
                    commit('updateAuth',jwt.decode(token));
                    commit('updateAuthorities', authorities);
                    resolve();
                })
                .catch(()=>{
                    reject();
                })
        })
    },
    doLogout({commit}){
        return new Promise((resolve)=>{
            commit('clearAuth')
            resolve();
        });
    },
    checkLocalStorage({commit}){
        const token = localStorage.getItem('token')
        if(token){
            commit('updateToken',token);
            commit('updateAuth',jwt.decode(token));
        }else{
            commit('clearAuth')
        }
    }
}

// Mutations
const mutations = {
    updateAuth(state, data) {
        state.user = data;
        state.loggedIn = !!state.token;
        //state.token = localStorage.getItem('token')
    },
    clearAuth(state) {
        state.user = null;
        state.loggedIn = false
        state.token = null
        localStorage.clear();
    },
    updateToken(state,token){
        localStorage.setItem("jwt-beer-hunter",token) ;
        state.token = token;
    },
    updateAuthorities(state,token){
        state.authorities = token;
    },
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}