begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|ExpressionDumper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Implements a dynamic document constructor. Creates a new  * document node with its own node identity.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DocumentConstructor
extends|extends
name|NodeConstructor
block|{
specifier|private
specifier|final
name|Expression
name|content
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|DocumentConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|contentExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|contentExpr
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newContextInfo
operator|.
name|addFlag
argument_list|(
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|content
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|contentSeq
init|=
name|content
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|contentSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|buf
init|=
literal|null
decl_stmt|;
name|SequenceIterator
name|i
init|=
name|contentSeq
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NAMESPACE
comment|/*|| 		               next.getType() == Type.DOCUMENT*/
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Found a node of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" inside a document constructor"
argument_list|)
throw|;
comment|// if item is an atomic value, collect the string values of all
comment|// following atomic values and seperate them by a space.
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
if|else if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
comment|// if item is a node, flush any collected character data and
comment|//	copy the node to the target doc.
block|}
if|else if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
comment|//TODO : design like below ? -pb
comment|/* 		         		         		        //TODO : wondering whether we shouldn't iterate over a nodeset as the specs would tend to say. -pb	         		         		        SequenceIterator i = contentSeq.iterate(); 		        Item next = i.nextItem(); 		        while(next != null) { 		            context.proceed(this, builder); 		             					if (Type.subTypeOf(next.getType(), Type.NODE)) { 						//flush any collected character data 						if (buf != null&& buf.length()> 0) { 							receiver.characters(buf); 							buf.setLength(0); 						}					 						// copy the node to the target doc 						if(next.getType() == Type.ATTRIBUTE) { 							throw new XPathException(getASTNode(), "XPTY0004 : Found a node of type " +  								Type.getTypeName(next.getType()) +  " inside a document constructor");							 						} else if (next.getType() == Type.DOCUMENT) {		 							//TODO : definitely broken, but that's the way to do 							for (int j = 0 ; j< ((DocumentImpl)next).getChildCount(); j++) {								 								((DocumentImpl)next).getNode(j).copyTo(context.getBroker(), receiver); 							}							 						} else if (Type.subTypeOf(next.getType(), Type.TEXT)) { 							//TODO 							buf.append("#text"); 						} else { 							next.copyTo(context.getBroker(), receiver); 						} 					} else {					 					    if(buf == null) 					        buf = new StringBuffer(); 						//else if (buf.length()> 0) 						//	buf.append(' '); 						buf.append(next.getStringValue());						 					} 					next = i.nextItem(); 					*/
block|}
comment|// flush remaining character data
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Encountered SAX exception while processing document constructor: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|this
argument_list|)
argument_list|)
throw|;
block|}
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|NodeImpl
name|node
init|=
name|builder
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"document {"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
comment|//TODO : is this the required syntax ?
name|content
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"document {"
argument_list|)
expr_stmt|;
comment|//TODO : is this the required syntax ?
name|result
operator|.
name|append
argument_list|(
name|content
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|content
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

