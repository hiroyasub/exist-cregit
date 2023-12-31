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
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
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
name|util
operator|.
name|XMLNames
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
name|*
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
name|Error
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_comment
comment|/**  * XQuery 3.0 computed namespace constructor.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NamespaceConstructor
extends|extends
name|NodeConstructor
block|{
specifier|private
name|Expression
name|qnameExpr
decl_stmt|;
specifier|private
name|Expression
name|content
init|=
literal|null
decl_stmt|;
specifier|public
name|NamespaceConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContentExpr
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|path
operator|.
name|setUseStaticContext
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Expression
name|expr
init|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|path
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|content
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|void
name|setNameExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|expr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|expr
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|expr
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|qnameExpr
operator|=
name|expr
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
specifier|final
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
name|qnameExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
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
block|{
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
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
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
block|}
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|prefixSeq
init|=
name|qnameExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|prefixSeq
operator|.
name|isEmpty
argument_list|()
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|prefixSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
operator|||
name|prefixSeq
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Prefix needs to be xs:string or xs:untypedAtomic"
argument_list|)
throw|;
block|}
name|String
name|prefix
init|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
decl_stmt|;
if|if
condition|(
operator|!
name|prefixSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|prefix
operator|=
name|prefixSeq
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|XMLNames
operator|.
name|isNCName
argument_list|(
name|prefix
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0074
argument_list|,
literal|"Prefix cannot be cast to xs:NCName"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|Sequence
name|uriSeq
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
specifier|final
name|String
name|value
init|=
name|uriSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|inscopeNsUri
init|=
name|context
operator|.
name|getInScopeNamespace
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|inscopeNsUri
operator|!=
literal|null
operator|&&
operator|!
name|inscopeNsUri
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0102
argument_list|,
literal|"Cannot override already defined ns"
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0101
argument_list|,
literal|"Cannot bind xmlns prefix"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
operator|&&
operator|!
name|value
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XML_NS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0101
argument_list|,
literal|"Cannot bind xml prefix to another namespace"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XML_NS
argument_list|)
operator|&&
operator|!
name|prefix
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0101
argument_list|,
literal|"Cannot bind prefix to XML namespace"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XMLNS_NS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0101
argument_list|,
literal|"Cannot bind prefix to xmlns namespace"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0101
argument_list|,
literal|"Cannot bind prefix to empty or zero-length namespace"
argument_list|)
throw|;
block|}
comment|//context.declareInScopeNamespace(prefix, value);
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|namespaceNode
argument_list|(
name|prefix
argument_list|,
name|value
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNamespaceNode
argument_list|(
name|nodeNr
argument_list|)
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
block|{
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
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
literal|"namespace "
argument_list|)
expr_stmt|;
comment|//TODO : remove curly braces if Qname
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|qnameExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|endIndent
argument_list|()
operator|.
name|nl
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"namespace "
argument_list|)
expr_stmt|;
comment|//TODO : remove curly braces if Qname
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|qnameExpr
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
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
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
block|}
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
name|qnameExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

