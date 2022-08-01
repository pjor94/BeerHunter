import 'vuetify/dist/vuetify.min.css';
import Vue from 'vue';
import Vuetify,{ VTextField } from 'vuetify/lib'
import VCurrencyField from 'v-currency-field'
import VJsoneditor from 'v-jsoneditor'

Vue.use(VJsoneditor)

Vue.use(Vuetify,{
    components: {
        VTextField
    }
});


Vue.use(VCurrencyField, {
    locale: 'it-IT',
    decimalLength: 8,
    autoDecimalMode: true,
    min: null,
    max: null,
    defaultValue: 0,
    valueAsInteger: false,
    allowNegative: true
})

export default new Vuetify({
    theme: { dark: false },

});
