/*
	Copyright (c) 2004-2009, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.widget.Roller"]){dojo._hasResource["dojox.widget.Roller"]=true;dojo.provide("dojox.widget.Roller");dojo.require("dijit._Widget");dojo.declare("dojox.widget.Roller",dijit._Widget,{delay:2000,autoStart:true,itemSelector:"> li",durationIn:400,durationOut:275,_idx:-1,postCreate:function $DAtq_(){if(!this["items"]){this.items=[];}dojo.addClass(this.domNode,"dojoxRoller");dojo.query(this.itemSelector,this.domNode).forEach(function(_1,i){this.items.push(_1.innerHTML);if(i==0){this._roller=_1;this._idx=0;}else{dojo.destroy(_1);}},this);if(!this._roller){this._roller=dojo.create("li",null,this.domNode);}this.makeAnims();if(this.autoStart){this.start();}},makeAnims:function $DAtr_(){var n=this.domNode;dojo.mixin(this,{_anim:{"in":dojo.fadeIn({node:n,duration:this.durationIn}),"out":dojo.fadeOut({node:n,duration:this.durationOut})}});this._setupConnects();},_setupConnects:function $DAts_(){var _4=this._anim;this.connect(_4["out"],"onEnd",function(){this._set(this._idx+1);_4["in"].play(15);});this.connect(_4["in"],"onEnd",function(){this._timeout=setTimeout(dojo.hitch(this,"_run"),this.delay);});},start:function $DAtt_(){if(!this.rolling){this.rolling=true;this._run();}},_run:function $DAtu_(){this._anim["out"].gotoPercent(0,true);},stop:function $DAtv_(){this.rolling=false;var m=this._anim,t=this._timeout;if(t){clearTimeout(t);}m["in"].stop();m["out"].stop();},_set:function $DAtw_(i){var l=this.items.length-1;if(i<0){i=l;}if(i>l){i=0;}this._roller.innerHTML=this.items[i]||"error!";this._idx=i;}});dojo.declare("dojox.widget.RollerSlide",dojox.widget.Roller,{durationOut:175,makeAnims:function $DAtx_(){var n=this.domNode,_a="position",_b={top:{end:0,start:25},opacity:1};dojo.style(n,_a,"relative");dojo.style(this._roller,_a,"absolute");dojo.mixin(this,{_anim:{"in":dojo.animateProperty({node:n,duration:this.durationIn,properties:_b}),"out":dojo.fadeOut({node:n,duration:this.durationOut})}});this._setupConnects();}});dojo.declare("dojox.widget._RollerHover",null,{postCreate:function $DAty_(){this.inherited(arguments);this.connect(this.domNode,"onmouseenter","stop");this.connect(this.domNode,"onmouseleave","start");}});}