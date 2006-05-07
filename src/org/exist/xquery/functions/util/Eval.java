begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|DocumentSet
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
name|QName
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
name|ReferenceNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|XQueryPool
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
name|BasicFunction
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
name|Cardinality
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
name|CompiledXQuery
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
name|FunctionSignature
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
name|XPathException
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
name|XQuery
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
name|XQueryContext
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
name|BooleanValue
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
name|EmptySequence
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
name|NodeValue
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
name|SequenceType
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
name|StringValue
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Eval
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eval"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Dynamically evaluates its string argument as an XPath/XQuery expression. "
operator|+
literal|"The argument expression will inherit the current execution context, i.e. all "
operator|+
literal|"namespace declarations and variable declarations are visible from within the "
operator|+
literal|"inner expression. It will return an empty sequence if you pass a whitespace string."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|, 				}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eval"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Dynamically evaluates its string argument as an XPath/XQuery expression. "
operator|+
literal|"The argument expression will inherit the current execution context, i.e. all "
operator|+
literal|"namespace declarations and variable declarations are visible from within the "
operator|+
literal|"inner expression. It will return an empty sequence if you pass a whitespace string."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eval-with-context"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Dynamically evaluates its string argument as an XPath/XQuery expression. "
operator|+
literal|"A new execution context will be created before the expression is evaluated. Static "
operator|+
literal|"context properties can be set via the XML fragment in the second parameter. The "
operator|+
literal|"XML fragment should have the format:<static-context><variable name=\"qname\">"
operator|+
literal|"variable value</variable></static-context>."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eval-inline"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Dynamically evaluates the XPath/XQuery expression specified in $b within "
operator|+
literal|"the current instance of the query engine. The evaluation context is taken from "
operator|+
literal|"argument $a."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eval-inline"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Dynamically evaluates the XPath/XQuery expression specified in $b within "
operator|+
literal|"the current instance of the query engine. The evaluation context is taken from "
operator|+
literal|"argument $a. The third argument, $c, specifies if the compiled query expression "
operator|+
literal|"should be cached. The cached query will be globally available within the db instance."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|Eval
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|argCount
init|=
literal|0
decl_stmt|;
name|Sequence
name|exprContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"eval-inline"
argument_list|)
condition|)
block|{
comment|// the current expression context
name|exprContext
operator|=
name|args
index|[
name|argCount
operator|++
index|]
expr_stmt|;
block|}
comment|// get the query expression
name|String
name|expr
init|=
name|StringValue
operator|.
name|expand
argument_list|(
name|args
index|[
name|argCount
operator|++
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|expr
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
return|return
operator|new
name|EmptySequence
argument_list|()
return|;
name|NodeValue
name|contextInit
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"eval-with-context"
argument_list|)
condition|)
block|{
comment|// set the context initialization param for later use
name|contextInit
operator|=
operator|(
name|NodeValue
operator|)
name|args
index|[
name|argCount
operator|++
index|]
expr_stmt|;
block|}
comment|// should the compiled query be cached?
name|boolean
name|cache
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|argCount
operator|<
name|getArgumentCount
argument_list|()
condition|)
name|cache
operator|=
operator|(
operator|(
name|BooleanValue
operator|)
name|args
index|[
name|argCount
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
comment|// save some context properties
name|context
operator|.
name|pushNamespaceContext
argument_list|()
expr_stmt|;
name|DocumentSet
name|oldDocs
init|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
name|exprContext
operator|!=
literal|null
condition|)
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|exprContext
operator|.
name|getDocumentSet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
argument_list|(
literal|2
argument_list|)
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|,
literal|"eval: "
operator|+
name|expr
argument_list|)
expr_stmt|;
name|Sequence
name|sequence
init|=
literal|null
decl_stmt|;
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|expr
argument_list|)
decl_stmt|;
name|XQuery
name|xquery
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|cache
condition|?
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|source
argument_list|)
else|:
literal|null
decl_stmt|;
name|XQueryContext
name|innerContext
decl_stmt|;
if|if
condition|(
name|contextInit
operator|!=
literal|null
condition|)
block|{
comment|// eval-with-context: initialize a new context
name|innerContext
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
name|initContext
argument_list|(
name|contextInit
operator|.
name|getNode
argument_list|()
argument_list|,
name|innerContext
argument_list|)
expr_stmt|;
block|}
else|else
comment|// use the existing outer context
name|innerContext
operator|=
name|context
expr_stmt|;
try|try
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|innerContext
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compiled
operator|.
name|setContext
argument_list|(
name|innerContext
argument_list|)
expr_stmt|;
block|}
name|sequence
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
name|exprContext
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerContext
operator|!=
name|this
operator|.
name|context
condition|)
name|innerContext
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|sequence
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|cache
operator|&&
name|compiled
operator|!=
literal|null
condition|)
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldDocs
operator|!=
literal|null
condition|)
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|oldDocs
argument_list|)
expr_stmt|;
name|context
operator|.
name|popNamespaceContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
argument_list|(
literal|2
argument_list|)
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
literal|"eval: "
operator|+
name|expr
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Read to optional static-context fragment to initialize 	 * the context. 	 *  	 * @param root 	 * @param innerContext 	 * @throws XPathException 	 */
specifier|private
name|void
name|initContext
parameter_list|(
name|Node
name|root
parameter_list|,
name|XQueryContext
name|innerContext
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeList
name|cl
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|cl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
literal|"variable"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|String
name|qname
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|NodeValue
name|value
init|=
operator|(
name|NodeValue
operator|)
name|elem
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|ReferenceNode
condition|)
name|value
operator|=
operator|(
operator|(
name|ReferenceNode
operator|)
name|value
operator|)
operator|.
name|getReference
argument_list|()
expr_stmt|;
name|innerContext
operator|.
name|declareVariable
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

