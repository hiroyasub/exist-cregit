/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dijit.layout.ContentPane"]){dojo._hasResource["dijit.layout.ContentPane"]=true;dojo.provide("dijit.layout.ContentPane");dojo.require("dijit._Widget");dojo.require("dijit._Contained");dojo.require("dijit.layout._LayoutWidget");dojo.require("dojo.parser");dojo.require("dojo.string");dojo.require("dojo.html");dojo.requireLocalization("dijit","loading",null,"ROOT,ar,ca,cs,da,de,el,es,fi,fr,he,hu,it,ja,ko,nb,nl,pl,pt,pt-pt,ru,sk,sl,sv,th,tr,zh,zh-tw");dojo.declare("dijit.layout.ContentPane",dijit._Widget,{href:"",extractContent:false,parseOnLoad:true,preventCache:false,preload:false,refreshOnShow:false,loadingMessage:"<span class='dijitContentPaneLoading'>${loadingState}</span>",errorMessage:"<span class='dijitContentPaneError'>${errorState}</span>",isLoaded:false,baseClass:"dijitContentPane",doLayout:true,ioArgs:{},isContainer:true,postMixInProperties:function $DNr_(){this.inherited(arguments);var _1=dojo.i18n.getLocalization("dijit","loading",this.lang);this.loadingMessage=dojo.string.substitute(this.loadingMessage,_1);this.errorMessage=dojo.string.substitute(this.errorMessage,_1);if(!this.href&&this.srcNodeRef&&this.srcNodeRef.innerHTML){this.isLoaded=true;}},buildRendering:function $DNs_(){this.inherited(arguments);if(!this.containerNode){this.containerNode=this.domNode;}},postCreate:function $DNt_(){this.domNode.title="";if(!dojo.attr(this.domNode,"role")){dijit.setWaiRole(this.domNode,"group");}dojo.addClass(this.domNode,this.baseClass);},startup:function $DNu_(){if(this._started){return;}if(this.isLoaded){dojo.forEach(this.getChildren(),function(_2){_2.startup();});if(this.doLayout){this._checkIfSingleChild();}if(!this._singleChild||!dijit._Contained.prototype.getParent.call(this)){this._scheduleLayout();}}this._loadCheck();this.inherited(arguments);},_checkIfSingleChild:function $DNv_(){var _3=dojo.query(">",this.containerNode),_4=_3.filter(function(_5){return dojo.hasAttr(_5,"dojoType")||dojo.hasAttr(_5,"widgetId");}),_6=dojo.filter(_4.map(dijit.byNode),function(_7){return _7&&_7.domNode&&_7.resize;});if(_3.length==_4.length&&_6.length==1){this._singleChild=_6[0];}else{delete this._singleChild;}},setHref:function $DNw_(_8){dojo.deprecated("dijit.layout.ContentPane.setHref() is deprecated. Use attr('href', ...) instead.","","2.0");return this.attr("href",_8);},_setHrefAttr:function $DNx_(_9){this.cancel();this.href=_9;if(this._created&&(this.preload||this._isShown())){return this.refresh();}else{this._hrefChanged=true;}},setContent:function $DNy_(_a){dojo.deprecated("dijit.layout.ContentPane.setContent() is deprecated.  Use attr('content', ...) instead.","","2.0");this.attr("content",_a);},_setContentAttr:function $DNz_(_b){this.href="";this.cancel();this._setContent(_b||"");this._isDownloaded=false;},_getContentAttr:function $DN0_(){return this.containerNode.innerHTML;},cancel:function $DN1_(){if(this._xhrDfd&&(this._xhrDfd.fired==-1)){this._xhrDfd.cancel();}delete this._xhrDfd;},uninitialize:function $DN2_(){if(this._beingDestroyed){this.cancel();}},destroyRecursive:function $DN3_(_c){if(this._beingDestroyed){return;}this._beingDestroyed=true;this.inherited(arguments);},resize:function $DN4_(_d){dojo.marginBox(this.domNode,_d);var _e=this.containerNode,mb=dojo.mixin(dojo.marginBox(_e),_d||{});var cb=(this._contentBox=dijit.layout.marginBox2contentBox(_e,mb));if(this._singleChild&&this._singleChild.resize){this._singleChild.resize({w:cb.w,h:cb.h});}},_isShown:function $DN5_(){if("open" in this){return this.open;}else{var _11=this.domNode;return (_11.style.display!="none")&&(_11.style.visibility!="hidden")&&!dojo.hasClass(_11,"dijitHidden");}},_onShow:function $DN6_(){if(this._needLayout){this._layoutChildren();}this._loadCheck();if(this.onShow){this.onShow();}},_loadCheck:function $DN7_(){if((this.href&&!this._xhrDfd)&&(!this.isLoaded||this._hrefChanged||this.refreshOnShow)&&(this.preload||this._isShown())){delete this._hrefChanged;this.refresh();}},refresh:function $DN8_(){this.cancel();this._setContent(this.onDownloadStart(),true);var _12=this;var _13={preventCache:(this.preventCache||this.refreshOnShow),url:this.href,handleAs:"text"};if(dojo.isObject(this.ioArgs)){dojo.mixin(_13,this.ioArgs);}var _14=(this._xhrDfd=(this.ioMethod||dojo.xhrGet)(_13));_14.addCallback(function(_15){try{_12._isDownloaded=true;_12._setContent(_15,false);_12.onDownloadEnd();}catch(err){_12._onError("Content",err);}delete _12._xhrDfd;return _15;});_14.addErrback(function(err){if(!_14.canceled){_12._onError("Download",err);}delete _12._xhrDfd;return err;});},_onLoadHandler:function $DN9_(_17){this.isLoaded=true;try{this.onLoad(_17);}catch(e){console.error("Error "+this.widgetId+" running custom onLoad code: "+e.message);}},_onUnloadHandler:function $DOA_(){this.isLoaded=false;try{this.onUnload();}catch(e){console.error("Error "+this.widgetId+" running custom onUnload code: "+e.message);}},destroyDescendants:function $DOB_(){if(this.isLoaded){this._onUnloadHandler();}var _18=this._contentSetter;dojo.forEach(this.getChildren(),function(_19){if(_19.destroyRecursive){_19.destroyRecursive();}});if(_18){dojo.forEach(_18.parseResults,function(_1a){if(_1a.destroyRecursive&&_1a.domNode&&_1a.domNode.parentNode==dojo.body()){_1a.destroyRecursive();}});delete _18.parseResults;}dojo.html._emptyNode(this.containerNode);},_setContent:function $DOC_(_1b,_1c){this.destroyDescendants();delete this._singleChild;var _1d=this._contentSetter;if(!(_1d&&_1d instanceof dojo.html._ContentSetter)){_1d=this._contentSetter=new dojo.html._ContentSetter({node:this.containerNode,_onError:dojo.hitch(this,this._onError),onContentError:dojo.hitch(this,function(e){var _1f=this.onContentError(e);try{this.containerNode.innerHTML=_1f;}catch(e){console.error("Fatal "+this.id+" could not change content due to "+e.message,e);}})});}var _20=dojo.mixin({cleanContent:this.cleanContent,extractContent:this.extractContent,parseContent:this.parseOnLoad},this._contentSetterParams||{});dojo.mixin(_1d,_20);_1d.set((dojo.isObject(_1b)&&_1b.domNode)?_1b.domNode:_1b);delete this._contentSetterParams;if(!_1c){dojo.forEach(this.getChildren(),function(_21){_21.startup();});if(this.doLayout){this._checkIfSingleChild();}this._scheduleLayout();this._onLoadHandler(_1b);}},_onError:function $DOD_(_22,err,_24){var _25=this["on"+_22+"Error"].call(this,err);if(_24){console.error(_24,err);}else{if(_25){this._setContent(_25,true);}}},_scheduleLayout:function $DOE_(){if(this._isShown()){this._layoutChildren();}else{this._needLayout=true;}},_layoutChildren:function $DOF_(){if(this._singleChild&&this._singleChild.resize){var cb=this._contentBox||dojo.contentBox(this.containerNode);this._singleChild.resize({w:cb.w,h:cb.h});}else{dojo.forEach(this.getChildren(),function(_27){if(_27.resize){_27.resize();}});}delete this._needLayout;},onLoad:function $DOG_(_28){},onUnload:function $DOH_(){},onDownloadStart:function $DOI_(){return this.loadingMessage;},onContentError:function $DOJ_(_29){},onDownloadError:function $DOK_(_2a){return this.errorMessage;},onDownloadEnd:function $DOL_(){}});}