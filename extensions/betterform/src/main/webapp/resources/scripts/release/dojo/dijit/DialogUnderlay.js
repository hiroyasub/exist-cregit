/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dijit.DialogUnderlay"]){dojo._hasResource["dijit.DialogUnderlay"]=true;dojo.provide("dijit.DialogUnderlay");dojo.require("dijit._Widget");dojo.require("dijit._Templated");dojo.declare("dijit.DialogUnderlay",[dijit._Widget,dijit._Templated],{templateString:"<div class='dijitDialogUnderlayWrapper'><div class='dijitDialogUnderlay' dojoAttachPoint='node'></div></div>",dialogId:"","class":"",attributeMap:{id:"domNode"},_setDialogIdAttr:function $DGv_(id){dojo.attr(this.node,"id",id+"_underlay");},_setClassAttr:function $DGw_(_2){this.node.className="dijitDialogUnderlay "+_2;},postCreate:function $DGx_(){dojo.body().appendChild(this.domNode);this.bgIframe=new dijit.BackgroundIframe(this.domNode);},layout:function $DGy_(){var is=this.node.style,os=this.domNode.style;os.display="none";var _5=dijit.getViewport();os.top=_5.t+"px";os.left=_5.l+"px";is.width=_5.w+"px";is.height=_5.h+"px";os.display="block";},show:function $DGz_(){this.domNode.style.display="block";this.layout();if(this.bgIframe.iframe){this.bgIframe.iframe.style.display="block";}},hide:function $DG0_(){this.domNode.style.display="none";if(this.bgIframe.iframe){this.bgIframe.iframe.style.display="none";}},uninitialize:function $DG1_(){if(this.bgIframe){this.bgIframe.destroy();}}});}