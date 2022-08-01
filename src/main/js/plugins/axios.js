import Vue from 'vue';
import _axios from "axios";


const TIMEOUT = 1000000;

let config = {};


const axios = _axios.create(config);

axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('jwt-beer-hunter');
        if (token) {
            if (!config.headers) {
                config.headers = {};
            }
            config.headers.Authorization = `${token}`;
        }
        config.timeout = TIMEOUT;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add a response interceptor
axios.interceptors.response.use(
    function(response) {
        // Do something with response data
        return response;
    },
    function(err) {
        // Do something with response error
        const status = err.status || err.response.status;
        if (status === 403 || status === 401) {
            console.error("Unauthorized")
        }
        return Promise.reject(err);
    }
);

Plugin.install = function(Vue/*, options*/) {
    Vue.axios = axios;
    window.axios = axios;
    Object.defineProperties(Vue.prototype, {
        axios: {
            get() {
                return axios;
            }
        },
        $axios: {
            get() {
                return axios;
            }
        },
    });
};

Vue.use(Plugin)
export {axios};

export default Plugin;
