/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["betterform.ui.select.CheckBoxGroup"]){dojo._hasResource["betterform.ui.select.CheckBoxGroup"]=true;dojo.provide("betterform.ui.select.CheckBoxGroup");dojo.require("dijit._Widget");dojo.require("betterform.ui.ControlValue");dojo.require("betterform.ui.select.CheckBox");dojo.declare("betterform.ui.select.CheckBoxGroup",betterform.ui.ControlValue,{buildRendering:function $DA3y_(){this.domNode=this.srcNodeRef;},postMixInProperties:function $DA3z_(){this.inherited(arguments);this.applyProperties(dijit.byId(this.xfControlId),this.srcNodeRef);if(dojo.attr(this.srcNodeRef,"incremental")==undefined||dojo.attr(this.srcNodeRef,"incremental")==""||dojo.attr(this.srcNodeRef,"incremental")=="true"){this.incremental=true;}else{this.incremental=false;}},postCreate:function $DA30_(){this.inherited(arguments);var _1="";dojo.query("*[checked]",this.domNode).forEach(function(_2){_1+=_2.value+" ";});if(_1!=""){_1=_1.replace(/\s+$/g,"");}this.setCurrentValue(_1);},_onFocus:function $DA31_(){this.inherited(arguments);this.handleOnFocus();},_onBlur:function $DA32_(){this.inherited(arguments);this.handleOnBlur();},getControlValue:function $DA33_(){var _3="";dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput",this.domNode).forEach(function(_4){_3=dijit.byId(dojo.attr(_4,"id")).getControlValue()+" "+_3;});return _3.replace(/\s+$/g,"");},_handleSetControlValue:function $DA34_(_5){var _6=new Array();_6=_5.split(" ");dojo.query(".dijitCheckBoxInput",this.domNode).forEach(function(_7){if(dojo.indexOf(_6,dijit.byId(_7.id).currentValue)!=-1){dijit.byId(_7.id).setChecked(true);}else{dijit.byId(_7.id).setChecked(false);}});},_setCheckBoxGroupValue:function $DA35_(){var _8=dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput",this.domNode);var _9=undefined;dojo.forEach(_8,function(_a,_b,_c){var id=dojo.attr(_a,"id");id=id.substring(0,id.length-6);if(_9==undefined){_9=id;}else{_9=_9+";"+id;}});if(_9==undefined){_9="";}fluxProcessor.dispatchEventType(this.xfControl.id,"DOMActivate",_9);if(this.incremental){this.xfControl.setControlValue(this.getControlValue());}},applyState:function $DA36_(){if(this.xfControl.isReadonly()){this.setDisabled(true);}else{this.setDisabled(false);}},setDisabled:function $DA37_(_e){dojo.forEach(dojo.query(".dijitCheckBoxInput",this.domNode),function(_f){var _10=dojo.attr(_f,"id");var _11=dijit.byId(_10);if(_11!=undefined){_11.attr("disabled",_e);}else{_11=new betterform.ui.select.CheckBox({},_10);_11.attr("disabled",_e);}});}});}