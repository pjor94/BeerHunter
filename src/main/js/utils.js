
import {mapActions, mapGetters, mapMutations, mapState} from "vuex";
import moment from "moment";

export default {
    data(){
        return {
            moment
        }
    },
    computed: {
        ...mapGetters({
            //botConfigs: 'BotConfigs/getBotConfigs'
        }),
        ...mapState('Loader',{
            __loader:'loader'
        }),
        ...mapState('Notify',{
            __snackbar:'snackbar',
        }),

    },
    methods: {
        ...mapActions({
            _loader: 'Loader/setLoader',
            setSnackbar: 'Notify/setSnackbar'
        }),
        ...mapMutations({
            //updateEditBot: 'BotConfigs/updateEditBot'
        }),
        setLoader(val){
            let msg = 'Loanding',loader = !this.__loader;
            val = this.isEmpty(val) ? false : val;
            msg = (typeof val === 'string' || val instanceof String) ? val : msg;
            loader = (typeof val === "boolean"|| val instanceof Boolean) ? val : loader;
            this._loader({loader,msg})
        },
        snackBarInfo(msg){
            this.setSnackbar({msg,color:"info",snackbar:true})
        },
        snackBarWarn(msg){
            this.setSnackbar({msg,color:"warning",snackbar:true})
        },
        snackBarError(msg){
            this.setSnackbar({msg,color:"error",snackbar:true})
        },
        snackBarSuccess(msg){
            this.setSnackbar({msg,color:"success",snackbar:true})
        },
        /**
         * Returns true if the passed value is empty, false otherwise. The value is deemed to be empty if it is either:
         *
         * - `null`
         * - `undefined`
         * - a zero-length array
         * - a zero-length string (Unless the `allowEmptyString` parameter is set to `true`)
         *
         * @param {Object} value The value to test.
         * @param {Boolean} [allowEmptyString=false] `true` to allow empty strings.
         * @return {Boolean}
         */
        isEmpty: function(value, allowEmptyString) {
            return (value == null) || (!allowEmptyString ? value === '' : false) || (this.isArray(value) && value.length === 0);
        },

        /**
         * Returns `true` if the passed value is a JavaScript Array, `false` otherwise.
         *
         * @param {Object} target The target to test.
         * @return {Boolean}
         * @method
         */
        isArray: ('isArray' in Array) ? Array.isArray : function(value) {
            return toString.call(value) === '[object Array]';
        },
    }
}