/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.av.FLVideo"]){dojo._hasResource["dojox.av.FLVideo"]=true;dojo.provide("dojox.av.FLVideo");dojo.experimental("dojox.av.FLVideo");dojo.require("dijit._Widget");dojo.require("dojox.embed.Flash");dojo.require("dojox.av._Media");dojo.declare("dojox.av.FLVideo",[dijit._Widget,dojox.av._Media],{_swfPath:dojo.moduleUrl("dojox.av","resources/video.swf"),constructor:function $DWy_(_1){dojo.global.swfIsInHTML=function $DXM_(){return true;};},postCreate:function $DWz_(){this._subs=[];this._cons=[];this.mediaUrl=this._normalizeUrl(this.mediaUrl);this.initialVolume=this._normalizeVolume(this.initialVolume);var _2={path:this._swfPath.uri,width:"100%",height:"100%",minimumVersion:9,expressInstall:true,params:{allowFullScreen:true,wmode:"transparent"},vars:{videoUrl:this.mediaUrl,id:this.id,autoPlay:this.autoPlay,volume:this.initialVolume,isDebug:this.isDebug}};this._sub("stageClick","onClick");this._sub("stageSized","onSwfSized");this._sub("mediaStatus","onPlayerStatus");this._sub("mediaMeta","onMetaData");this._sub("mediaError","onError");this._sub("mediaStart","onStart");this._sub("mediaEnd","onEnd");this._flashObject=new dojox.embed.Flash(_2,this.domNode);this._flashObject.onError=function $DXN_(_3){console.warn("Flash Error:",_3);alert(_3);};this._flashObject.onLoad=dojo.hitch(this,function(_4){this.flashMedia=_4;this.isPlaying=this.autoPlay;this.isStopped=!this.autoPlay;this.onLoad(this.flashMedia);this._initStatus();this._update();});},play:function $DW0_(_5){this.isPlaying=true;this.isStopped=false;this.flashMedia.doPlay(this._normalizeUrl(_5));},pause:function $DW1_(){this.isPlaying=false;this.isStopped=false;this.flashMedia.pause();},seek:function $DW2_(_6){this.flashMedia.seek(_6);},volume:function $DW3_(_7){if(_7){if(!this.flashMedia){this.initialVolume=_7;}this.flashMedia.setVolume(this._normalizeVolume(_7));}if(!this.flashMedia||!this.flashMedia.doGetVolume){return this.initialVolume;}return this.flashMedia.getVolume();},_checkBuffer:function $DXK_(_8,_9){if(this.percentDownloaded==100){if(this.isBuffering){this.onBuffer(false);this.flashMedia.doPlay();}return;}if(!this.isBuffering&&_9<0.1){this.onBuffer(true);this.flashMedia.pause();return;}var _a=this.percentDownloaded*0.01*this.duration;if(!this.isBuffering&&_8+this.minBufferTime*0.001>_a){this.onBuffer(true);this.flashMedia.pause();}else{if(this.isBuffering&&_8+this.bufferTime*0.001<=_a){this.onBuffer(false);this.flashMedia.doPlay();}}},_update:function $DXL_(){var _b=Math.min(this.getTime()||0,this.duration);var _c=this.flashMedia.getLoaded();this.percentDownloaded=Math.ceil(_c.bytesLoaded/_c.bytesTotal*100);this.onDownloaded(this.percentDownloaded);this.onPosition(_b);if(this.duration){this._checkBuffer(_b,_c.buffer);}setTimeout(dojo.hitch(this,"_update"),this.updateTime);}});}