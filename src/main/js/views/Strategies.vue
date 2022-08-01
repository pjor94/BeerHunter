<template>
  <v-card>
    <v-toolbar
        color="primary"
        dark
        dense
        extended
        flat
    >
      <v-toolbar-title>Strategies</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-icon @click="editStrategy(null)">fas fa-plus</v-icon>
      </v-toolbar-items>

    </v-toolbar>
    <StrategyDialog v-model="editDialog" @save="save" @close="close"></StrategyDialog>
    <v-dialog v-model="newStrategy" max-width="500px">
      <v-card>
        <v-card-title>
         Create New Strategy
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="newStrategyName" label="Strategy Name (whitout spaces)" item-value="text"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="closeNewDialog">Close</v-btn>
          <v-btn color="primary" text @click="saveNewDialog">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-card flat
            class="mx-5"
            style="margin-top: -48px;"
    >
      <v-data-table
          v-if="!isLoanding"
          :headers="headers"
          :items="stategiesItems"
      >
        <template v-slot:item.actions="{ item }">
          <v-icon small class="mr-2" @click="editStrategy(item.name)">fas fa-edit</v-icon>
        </template>
      </v-data-table>
      <v-row v-else justify="center">
        <v-progress-circular
            :size="50"
            color="primary"
            indeterminate
        ></v-progress-circular>
      </v-row>

    </v-card>
  </v-card>
</template>

<script>
import moment from "moment";
import {mapActions, mapGetters} from "vuex";
import utils from "@/utils";
import StrategyDialog from "@/components/StrategyDialog";

export default {
  name: "Strategies.vue",
  mixins:[utils],
  components:{StrategyDialog},
  data(){
    return {
      moment,
      headers: [
        {text: 'Strategy', value: 'name'},
        {text: 'Actions', value: 'actions', sortable: false},
      ],
      isLoanding: false,
      editDialog:false,
      newStrategy:false,
      newStrategyName:'',
    }
  },
  mounted() {
    this.isLoanding = true
    this.loadStrategies()
        .then(() => {
          this.isLoanding = false;
        })
  },
  computed: {
    ...mapGetters({
      strategiesConfig: 'BotConfigs/getStrategiesConfigs',
      strategies: 'Strategies/getStrategies'
    }),
    stategiesItems(){
      if(this.strategies)
      return this.strategies.map((state)=>{return {name:state}})
      else return []
    }
  },
  methods:{
    ...mapActions({
      edit: 'Strategies/edit',
      saveStrategy: 'Strategies/saveStrategy',
      loadStrategies:'Strategies/loadStrategies'
    }),
    close(){
      this.editDialog=false;
    },
    save(){
      this.setLoader(`Saving strategy`);
      this.saveStrategy()
      .then(()=>{
        this.editDialog=false;
        this.setLoader(false);
      })
    },
    closeNewDialog(){
      this.newStrategy = false;
      this.newStrategyName = '';
    },
    saveNewDialog(){
      this.setLoader(`Creating ${this.newStrategyName} strategy`);
      this.edit(this.newStrategyName)
          .then(()=>{
            this.editDialog=true;
            this.setLoader(false);
            this.closeNewDialog();
          })
    },
    editStrategy(item){
      if(item){
        this.setLoader(`Loading ${item} strategy`);
        this.edit(item)
        .then(()=>{
          this.editDialog=true;
          this.setLoader(false);
        })
      }else this.newStrategy = true;


    }
  }

}
</script>

<style scoped>

</style>