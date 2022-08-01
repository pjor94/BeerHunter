<template>
  <div :id="id ? id: $options._componentTag +'-'+ _uid"
       :class="$options._componentTag">
    <slot></slot>
  </div>
</template>
<script>

import * as ace from 'ace-builds/src-noconflict/ace';
import  'ace-builds/src-noconflict/theme-chrome';
import  'ace-builds/src-noconflict/mode-javascript';

window.ace = ace;

export default {
  //  simplified model handling using `value` prop and `input` event for $emit
  props:['value','id','options'],
  data(){
    return {
      opt:null,
      oldValue:null
    }
  },
  watch:{
    value() {
      //  two way binding – emit changes to parent

      //  update value on external model changes
      if(this.oldValue !== this.value){
        this.editor.setValue(this.value, 1);
      }
    }
  },

  mounted(){
    //  editor
    this.editor = window.ace.edit(this.$el.id);

    //  deprecation fix
    this.editor.$blockScrolling = Infinity;

    //  ignore doctype warnings
    const session = this.editor.getSession();
    session.on("changeAnnotation", () => {
      const a = session.getAnnotations();
      const b = a.slice(0).filter( (item) => item.text.indexOf('DOC') == -1 );
      if(a.length > b.length) session.setAnnotations(b);
    });

    //  editor options
    //  https://github.com/ajaxorg/ace/wiki/Configuring-Ace
    this.opt = this.options || {};

    //  opinionated option defaults
    this.opt.maxLines = this.opt.maxLines || Infinity;
    this.opt.printMargin = this.opt.printMargin || false;
    this.opt.highlightActiveLine = this.opt.highlightActiveLine || false;

    //  hide cursor
    if(this.opt.cursor === 'none' || this.opt.cursor === false){
      this.editor.renderer.$cursorLayer.element.style.display = 'none';
      delete this.opt.cursor;
    }

    //  add missing mode and theme paths
    if(this.opt.mode && this.opt.mode.indexOf('ace/mode/')===-1) {
      this.opt.mode = `ace/mode/${this.opt.mode}`;
    }
    if(this.opt.theme && this.opt.theme.indexOf('ace/theme/')===-1) {
      this.opt.theme = `ace/theme/${this.opt.theme}`;
    }
    this.editor.setOptions(this.opt);


    //  set model value
    //  if no model value found – use slot content
    if(!this.value || this.value === ''){
      this.$emit('input', this.editor.getValue());
    } else {
      this.editor.setValue(this.value, -1);
    }

    let _self = this;
    //  editor value changes
    this.editor.on('change', () => {
      //  oldValue set to prevent internal updates
      _self.oldValue = _self.editor.getValue();
      _self.$emit('input', _self.editor.getValue())
    });


  },
  methods:{ editor(){ return this.editor } }
};
</script>
