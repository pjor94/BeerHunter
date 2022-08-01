<template>
  <v-card>
    <v-toolbar
        color="primary"
        dark
        dense
        extended
        flat
    >
      <v-toolbar-title>Bot Configurations</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-icon @click="editBot(null)">fas fa-plus</v-icon>
      </v-toolbar-items>
    </v-toolbar>
    <BotConfigDialog v-model="editDialog" @close="closeDialog" @save="save"></BotConfigDialog>
    <BackTestingDialog v-model="backtestingDialog" @close="backTestingClose"
                       @save="backTestingStart"></BackTestingDialog>
    <v-card flat
            class="mx-5"
            style="margin-top: -48px;"
    >
      <v-data-table
          v-if="!isLoanding"
          :headers="headers"
          :items="botConfigs"
      >
        <template v-slot:item.active="{ item }">
          <v-chip
              :color="getColor(item.active)"
              dark
          >
            {{ item.active ? 'ON' : 'OFF' }}
          </v-chip>
        </template>
        <template v-slot:item.updatedAt="{ item }">
          {{moment(item.updatedAt).format('YYYY-MM-DD HH:mm:ss')}}
        </template>
        <template v-slot:item.actions="{ item }">
          <div class="text-center">
            <v-menu>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="primary"
                    dark
                    v-bind="attrs"
                    v-on="on"
                    icon
                >
                  <v-icon>fas fa-cogs</v-icon>
                </v-btn>
              </template>
              <v-list-item-group
                  color="primary"
              >
                <v-list>
                  <v-list-item @click="editBot(item)">
                    <v-list-item-icon>
                      <v-icon>fas fa-edit</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>edit</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-icon>
                      <v-icon :color="!item.active ? 'success' : 'error'">
                        {{ item.active ? 'far fa-times-circle' : 'far fa-check-circle' }}
                      </v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>{{ item.active ? 'disable' : 'enable' }}</v-list-item-title>
                  </v-list-item>
                  <v-list-item @click="backTestingDialog(item)">
                    <v-list-item-icon>
                      <v-icon>fas fa-running</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>back Testing</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-list-item-group>
            </v-menu>
          </div>
        </template>
      </v-data-table>
      <v-row v-else justify="center">
        <v-progress-circular
            :size="50"
            color="primary"
            indeterminate
        ></v-progress-circular>
      </v-row>
      <!--      </v-card-text>-->
    </v-card>
  </v-card>
</template>

<script>
import BotConfigDialog from "../components/BotConfigDialog";
import {mapActions, mapGetters, mapMutations} from "vuex";
import utils from "@/utils";
import BackTestingDialog from "@/components/BackTestingDialog";


export default {
  name: "BotConfigurations",
  mixins: [utils],
  components: {BackTestingDialog, BotConfigDialog},
  data() {
    return {
      editDialog: false,
      backtestingDialog: false,
      headers: [
        {
          text: 'Bot Name',
          align: 'center',
          value: 'name',
        },
        {
          text: 'Pair',
          align: 'center',
          value: 'pair',
        },
        {
          text: 'Strategy', align: 'center',
          value: 'strategy'
        },
        {
          text: 'Time Frame', align: 'center',
          value: 'timeframe'
        },
        {
          text: 'Status', align: 'center',
          value: 'active'
        },
        {
          text: 'Updated', align: 'center',
          value: 'updatedAt'
        },
        {text: 'Actions', value: 'actions', sortable: false},

      ],
      isLoanding: true
    }
  },
  mounted() {
    this.isLoanding = true
    this.loadBotConfigs()
        .then(() => {
          this.isLoanding = false;
        })
  },
  computed: {
    ...mapGetters({
      botConfigs: 'BotConfigs/getBotConfigs'
    })

  },
  methods: {
    ...mapActions({
      loadBotConfigs: 'BotConfigs/loadBotConfigs',
      saveBotConfig: 'BotConfigs/saveBotConfig',
      loadDialogData: 'BotConfigs/loadDialogData',
      loadHistoricalData: 'Backtesting/loadHistoricalData',
    }),
    ...mapMutations({
      updateEditBot: 'BotConfigs/updateEditBot',
      updateBotBackTesting: 'Backtesting/updateBotBackTesting',
      updateBacktesting: 'Backtesting/updateBacktesting'
    }),
    getColor(active) {
      return active ? 'success' : 'error'
    },
    editBot(item) {
      this.setLoader('Loading Data')
      this.loadDialogData()
          .then(() => {
            this.updateEditBot(item);
            this.editDialog = true;
            this.setLoader(false)
          })
    },
    save() {
      this.setLoader("Saving changes")
      this.saveBotConfig()
          .then(() => {
            this.closeDialog();
            this.setLoader(false);
            this.snackBarSuccess("Changes saved");
          })
          .catch(() => this.snackBarError("Something Wrong Happend"))
    },
    closeDialog() {
      //
      this.updateEditBot({});
      this.editDialog = false;

    },
    backTestingDialog(item) {
      this.setLoader('Loading Data')
      this.loadDialogData()
          .then(() => {
            this.loadHistoricalData()
                .then(() => {
                  this.updateBotBackTesting(item);
                  this.updateBacktesting({});
                  this.backtestingDialog = true;
                  this.setLoader(false);
                })
          })
    },
    backTestingClose() {
      this.backtestingDialog = false;
    },
    backTestingStart() {

    }

  }
}
</script>

<style scoped>

</style>