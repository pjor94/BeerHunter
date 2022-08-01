import Vue from 'vue'
import StochRSI from '@/components/StochRSI';
import TransactionsHistory from "./views/TransactionsHistory";
import BotConfigurations from "./views/BotConfigurations";
import Login from './views/Login';
import VueRouter from "vue-router";
import store from "@/store";
import BalanceChart from "./views/BalanceChart";
import BackTesting from "./views/BackTesting";
import HistoricalData from "@/views/HistoricalData";
import Strategies from "@/views/Strategies";

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/balances',
        name: 'Balances',
        component: BalanceChart,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/stochrsi',
        component: StochRSI,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/transactions',
        component: TransactionsHistory,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/botConfiguration',
        component: BotConfigurations,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/backTesting',
        component: BackTesting,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/HistoricalData',
        name:'HistoricalData',
        component: HistoricalData,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/strategies',
        component: Strategies,
        meta: {
            requiresAuth: true
        }
    }
]

const router = new VueRouter({
    mode: 'history',
    routes // short for `routes: routes`,
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!store.getters['Auth/getIsLoggedIn']) {
            next({name: 'Login'})
        } else {
            next()
        }
    } else {
        next()
    }
})
export default router;
