/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["betterform.ui.input.Date"]){dojo._hasResource["betterform.ui.input.Date"]=true;dojo.provide("betterform.ui.input.Date");dojo.require("betterform.ui.ControlValue");dojo.require("dijit._Widget");dojo.require("dijit._Templated");dojo.require("dijit.form.DateTextBox");dojo.declare("betterform.ui.input.Date",[betterform.ui.ControlValue,dijit.form.DateTextBox],{constructor:function $DA2j_(){this.incremental=true;},postMixInProperties:function $DA2k_(){this.inherited(arguments);this.applyProperties(dijit.byId(this.xfControlId),this.srcNodeRef);},postCreate:function $DA2l_(){this.inherited(arguments);this.setCurrentValue();},onChange:function $DA2m_(_1,_2){this.inherited(arguments);if(this.incremental){this.setControlValue();}},_onFocus:function $DA2n_(){this.inherited(arguments);this.handleOnFocus();},_onBlur:function $DA2o_(){this.handleOnBlur();this.inherited(arguments);},validate:function $DA2p_(_3){},getControlValue:function $DA2q_(){var _4;var _5=this.attr("value");if(_5==undefined){_4=this.focusNode.value;}else{_4=dojo.date.stamp.toISOString(_5,this.constraint);}if(_4.indexOf("T")!=-1){_4=_4.split("T")[0];}return _4;},_handleSetControlValue:function $DA2r_(_6){if(_6==undefined||_6==""){this._setValueAttr("");}else{this._setValueAttr(dojo.date.stamp.fromISOString(_6,this.constraint));}},_handleDOMFocusIn:function $DA2s_(){this.focused=true;var _7=dijit.byId(this.id);if(_7!=undefined){_7.focus();}}});}