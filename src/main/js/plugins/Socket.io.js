
import Vue from 'vue'
import store from "@/store";
import VueSocketIO from 'vue-socket.io-extended';
import { io } from 'socket.io-client';


const ioInstance = io('', {
    path:'/socket',
    reconnection: true,
    reconnectionDelay: 500,
    maxReconnectionAttempts: Infinity
});
ioInstance.on('connect', function() {
    console.log("connected")
    ioInstance.emit('join', 'Hello World from client');
});
ioInstance.on('disconnect', function () {
    ioInstance.io.opts.query = {
        token: store.getters['Auth/getToken']
    }
    ioInstance.open();
});
ioInstance.on('reconnect_attempt', () => {
    ioInstance.io.opts.query = {
        token: store.getters['Auth/getToken']
    }
});
Vue.use(VueSocketIO, ioInstance, {
    store, // vuex store instance
    // actionPrefix: 'SOCKET_', // keep prefix in uppercase
    // //eventToActionTransformer: (actionName) => actionName // cancel camel case
});

export default ioInstance