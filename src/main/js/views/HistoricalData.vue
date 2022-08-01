<template>
  <v-card>
    <v-toolbar
        color="primary"
        dark
        dense
        extended
        flat
    >
      <v-toolbar-title>Historical Data</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-icon small class="mr-2" @click="openNew">fas fa-plus</v-icon>
      <v-icon small class="mr-2" @click="load(this.pageOptions)">fas fa-sync</v-icon>
    </v-toolbar>
    <v-card flat
            color="#385F73"

            dark
            class="mx-5"
            style="margin-top: -48px;"
    >
      <v-data-table
          v-if="!isLoanding"
          :page="page"
          :pageCount="historicalData.totalPages"
          :headers="headers"
          :items="historicalData.content"
          :options.sync="options"
          :server-items-length="historicalData.totalElements"
          class="elevation-1"
      >
        <template v-slot:item.status="{ item }">
          <v-chip
              :color="getColor(item.status)"
              dark
          >
            {{ item.status }}
          </v-chip>
        </template>
        <template v-slot:item.end="{ item }">
          {{ moment(item.end).format('DD-MM-YYYY HH:mm') }}
        </template>
        <template v-slot:item.start="{ item }">{{ moment(item.start).format('DD-MM-YYYY HH:mm') }}</template>
        <template v-slot:item.actions="{ item }">
          <v-icon small class="mr-2" @click="update(item.pair)">fas fa-sync</v-icon>
        </template>
      </v-data-table>
      <v-row v-else justify="center">
        <v-progress-circular
            :size="50"
            color="primary"
            indeterminate
        ></v-progress-circular>

      </v-row>
      <v-snackbar v-model="snackbar" dark top color="warning" multi-lineine>
        {{ snackbarText }}
        <template v-slot:action="{ attrs }">
          <v-btn dark text v-bind="attrs" @click="snackbar = false">Close</v-btn>
        </template>
      </v-snackbar>


      <v-dialog
          v-model="newDialog"
          persistent
          max-width="600px"
      >
        <v-card>
          <v-card-title>
            <span class="headline">Download Historical Data</span>
          </v-card-title>
          <v-card-text>
            <v-autocomplete
                v-model="newModel"
                :items="historicalData.content"
                :loading="exchangeInfoLoading"
                chips
                clearable
                dense
                hide-selected
                item-text="symbol"
                item-value="symbol"
                label="Search for a Pair..."

            >
              <template v-slot:no-data>
                <v-list-item>
                  <v-list-item-title>
                    Search for your favorite
                    <strong>Cryptocurrency</strong>
                  </v-list-item-title>
                </v-list-item>
              </template>
              <template v-slot:selection="{ attr, on, item, selected }">
                <v-chip
                    v-bind="attr"
                    :input-value="selected"
                    color="blue-grey"
                    class="white--text"
                    v-on="on"
                >
                  <v-icon left>
                    mdi-bitcoin
                  </v-icon>
                  <span v-text="item.symbol"></span>
                </v-chip>
              </template>
              <template v-slot:item="{ item }">
                <v-list-item-avatar
                    color="indigo"
                    class="headline font-weight-light white--text"
                >
                  {{ item.symbol.charAt(0) }}
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title v-text="item.symbol"></v-list-item-title>
                  <v-list-item-subtitle v-text="item.symbol"></v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <v-icon>mdi-bitcoin</v-icon>
                </v-list-item-action>
              </template>
            </v-autocomplete>

          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="newModel = null ;newDialog = false">Close</v-btn>
            <v-btn color="blue darken-1" text @click="download()">Download</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!--      </v-card-text>-->
    </v-card>
  </v-card>
</template>

<script>
import moment from "moment";
import {axios} from "@/plugins/axios";
import {mapGetters, mapActions} from "vuex";

moment.locale("it")

export default {
  name: "HistoricalData",
  data() {
    return {
      options:{},
      editConfig: {},
      exchangeInfo: {},
      exchangeInfoLoading: false,
      headers: [
        {text: 'Pair', value: 'pair'},
        {text: 'Start', value: 'start'},
        {text: 'End', value: 'end'},
        {text: 'Status', value: 'status'},
        {text: 'Update', value: 'actions', sortable: false},
      ],
      isLoanding: true,
      snackbar: false,
      snackbarText: '',
      newDialog: false,
      newModel: null,
      moment
    }
  },
  mounted() {
    this.load(this.pageOptions).then(()=>this.isLoanding = false)
  },
  computed: {
    ...mapGetters({
      historicalData: 'Backtesting/pagedData',
      pageOptions: 'Backtesting/pageOptions'
    }),
    page() {
      return this.pageOptions.page +1
    }
  },
  watch: {
    options: {
      deep: true,
      handler({page, itemsPerPage}) {
        this.load({page:page-1,size:itemsPerPage})
      },
    },
  },
  methods: {
    ...mapActions({
      load: 'Backtesting/loadData',
    }),
    getColor(status) {
      return status === 'updating' ? 'warning' : 'success'
    },
    async update(pair) {
      await axios.get('/api/historicalData/update/' + pair);
      await this.getDataFromApi();
    },
    async getExchangeInfoFromApi() {
      const {data} = await axios.get('/api/backTesting/getAccountInfo');
      this.exchangeInfo = data;
    },
    async openNew() {
      await this.getDataFromApi();
      let isAlreadyUpdating = this.historicalData.find((el) => el.status === 'updating');
      if (isAlreadyUpdating) {
        this.snackbarText = 'Can Only download One pair at time. try later'
        this.snackbar = true;
      } else {
        this.newDialog = true;
      }
    },
    async download() {
      if (this.newModel) {
        await this.update(this.newModel);
        await this.getDataFromApi();
        this.newModel = null;
        this.newDialog = false;

      }
    }
  }
}
</script>

<style scoped>

</style>