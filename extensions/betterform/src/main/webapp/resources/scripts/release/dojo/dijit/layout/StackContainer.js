/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dijit.layout.StackContainer"]){dojo._hasResource["dijit.layout.StackContainer"]=true;dojo.provide("dijit.layout.StackContainer");dojo.require("dijit._Templated");dojo.require("dijit.layout._LayoutWidget");dojo.requireLocalization("dijit","common",null,"ROOT,ar,ca,cs,da,de,el,es,fi,fr,he,hu,it,ja,ko,nb,nl,pl,pt,pt-pt,ru,sk,sl,sv,th,tr,zh,zh-tw");dojo.require("dojo.cookie");dojo.declare("dijit.layout.StackContainer",dijit.layout._LayoutWidget,{doLayout:true,persist:false,baseClass:"dijitStackContainer",_started:false,postCreate:function $DOt_(){this.inherited(arguments);dojo.addClass(this.domNode,"dijitLayoutContainer");dijit.setWaiRole(this.containerNode,"tabpanel");this.connect(this.domNode,"onkeypress",this._onKeyPress);},startup:function $DOu_(){if(this._started){return;}var _1=this.getChildren();dojo.forEach(_1,this._setupChild,this);if(this.persist){this.selectedChildWidget=dijit.byId(dojo.cookie(this.id+"_selectedChild"));}else{dojo.some(_1,function(_2){if(_2.selected){this.selectedChildWidget=_2;}return _2.selected;},this);}var _3=this.selectedChildWidget;if(!_3&&_1[0]){_3=this.selectedChildWidget=_1[0];_3.selected=true;}dojo.publish(this.id+"-startup",[{children:_1,selected:_3}]);if(_3){this._showChild(_3);}this.inherited(arguments);},_setupChild:function $DOv_(_4){this.inherited(arguments);dojo.removeClass(_4.domNode,"dijitVisible");dojo.addClass(_4.domNode,"dijitHidden");_4.domNode.title="";return _4;},addChild:function $DOw_(_5,_6){this.inherited(arguments);if(this._started){dojo.publish(this.id+"-addChild",[_5,_6]);this.layout();if(!this.selectedChildWidget){this.selectChild(_5);}}},removeChild:function $DOx_(_7){this.inherited(arguments);if(this._beingDestroyed){return;}if(this._started){dojo.publish(this.id+"-removeChild",[_7]);this.layout();}if(this.selectedChildWidget===_7){this.selectedChildWidget=undefined;if(this._started){var _8=this.getChildren();if(_8.length){this.selectChild(_8[0]);}}}},selectChild:function $DOy_(_9){_9=dijit.byId(_9);if(this.selectedChildWidget!=_9){this._transition(_9,this.selectedChildWidget);this.selectedChildWidget=_9;dojo.publish(this.id+"-selectChild",[_9]);if(this.persist){dojo.cookie(this.id+"_selectedChild",this.selectedChildWidget.id);}}},_transition:function $DOz_(_a,_b){if(_b){this._hideChild(_b);}this._showChild(_a);if(this.doLayout&&_a.resize){_a.resize(this._containerContentBox||this._contentBox);}},_adjacent:function $DO0_(_c){var _d=this.getChildren();var _e=dojo.indexOf(_d,this.selectedChildWidget);_e+=_c?1:_d.length-1;return _d[_e%_d.length];},forward:function $DO1_(){this.selectChild(this._adjacent(true));},back:function $DO2_(){this.selectChild(this._adjacent(false));},_onKeyPress:function $DO3_(e){dojo.publish(this.id+"-containerKeyPress",[{e:e,page:this}]);},layout:function $DO4_(){if(this.doLayout&&this.selectedChildWidget&&this.selectedChildWidget.resize){this.selectedChildWidget.resize(this._contentBox);}},_showChild:function $DO5_(_10){var _11=this.getChildren();_10.isFirstChild=(_10==_11[0]);_10.isLastChild=(_10==_11[_11.length-1]);_10.selected=true;dojo.removeClass(_10.domNode,"dijitHidden");dojo.addClass(_10.domNode,"dijitVisible");if(_10._onShow){_10._onShow();}else{if(_10.onShow){_10.onShow();}}},_hideChild:function $DO6_(_12){_12.selected=false;dojo.removeClass(_12.domNode,"dijitVisible");dojo.addClass(_12.domNode,"dijitHidden");if(_12.onHide){_12.onHide();}},closeChild:function $DO7_(_13){var _14=_13.onClose(this,_13);if(_14){this.removeChild(_13);_13.destroyRecursive();}},destroy:function $DO8_(){this._beingDestroyed=true;this.inherited(arguments);}});dojo.require("dijit.layout.StackController");dojo.extend(dijit._Widget,{title:"",selected:false,closable:false,onClose:function $DO9_(){return true;}});}