<template>
  <v-row justify="center">
    <v-dialog
        v-model="value"
        persistent
        fullscreen
        max-width="auto"
        scrollable
    >
      <v-card>

        <v-card-title class="pa-0">
          <v-toolbar extended>
            <v-toolbar-title>Edit Strategy {{editStrategy.name}}</v-toolbar-title>
            <template v-slot:extension>
              <v-tabs v-model="tabs">
                <v-tab :href="`#tab-0`">Strategy</v-tab>
                <v-tab  :href="`#tab-1`">Default Config</v-tab>
              </v-tabs>
            </template>
          </v-toolbar>
          <span class="headline"></span>


        </v-card-title>
        <v-card-text height="100%" class="pa-0" v-if="value">
          <v-tabs-items v-model="tabs">
            <v-tab-item
                :value="`tab-0`"
            >
              <aceEditor
                  v-model="editStrategy.strategy"
                  :options="editoroptions"
                  id="strategyEditor">
              </aceEditor>
            </v-tab-item>
            <v-tab-item
                :value="`tab-1`"
            >
              <aceEditor height="100%"
                  v-model="editStrategy.config"
                  :options="editoroptions"
                  id="configEditor">
              </aceEditor>
            </v-tab-item>
          </v-tabs-items>


        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click.native="close()">Close</v-btn>
          <v-btn color="blue darken-1" text @click.native="save()">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import utils from "@/utils";
import aceEditor from "./ace-editor"
import {mapGetters, mapState} from "vuex";

export default {
  name: "StrategyDialog",
  mixins: [utils],
  components:{aceEditor},
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  computed:{
    ...mapGetters({

    }),
    ...mapState('Strategies', [
      'editStrategy',
    ]),
  },
  data(){
    return {
      tabs:'tab-0',
      //  options object
      //  https://github.com/ajaxorg/ace/wiki/Configuring-Ace
      editoroptions:{
        mode:'javascript',
        theme: 'chrome',
        fontSize: 11,
        fontFamily: 'monospace',
        highlightActiveLine: false,
        highlightGutterLine: false
      },
    }
  },
  methods:{
    save(){
      this.$emit("input",false);
      this.$emit("save");
    },
    close(){
      this.$emit("close")
    }
  }
}
</script>

<style scoped>

</style>