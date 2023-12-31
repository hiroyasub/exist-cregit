begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|fn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Dependency
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
name|ErrorCodes
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
name|Function
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
name|Profiler
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
name|AnyURIValue
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
name|FunctionReturnSequenceType
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
name|FunctionParameterSequenceType
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
name|Node
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunBaseURI
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FunBaseURI
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"base-uri"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the value of the base URI property for the context item."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The base URI from the context item"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"base-uri"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the value of the base URI property for $uri. "
operator|+
literal|"If $uri is the empty sequence, the empty sequence is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"uri"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The URI"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the base URI from $uri"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"static-base-uri"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the value of the base URI property from the static context. "
operator|+
literal|"If the base-uri property is undefined, the empty sequence is returned."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The base URI from the static context"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunBaseURI
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[],      * org.exist.xquery.value.Sequence)      */
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
block|}
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
name|NodeValue
name|node
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"static-base-uri"
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isBaseURIDeclared
argument_list|()
condition|)
block|{
name|result
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|AnyURIValue
operator|)
name|result
operator|)
operator|.
name|toURI
argument_list|()
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
comment|//                    throw new XPathException(this, ErrorCodes.XPST0001, "");
name|LOG
operator|.
name|debug
argument_list|(
literal|"URI is not absolute"
argument_list|)
expr_stmt|;
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|isEmpty
argument_list|()
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
name|XPDY0002
argument_list|,
literal|"Context sequence is empty and no argument specified"
argument_list|)
throw|;
block|}
specifier|final
name|Item
name|item
init|=
name|contextSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
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
literal|"Context item is not a node"
argument_list|)
throw|;
block|}
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|item
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|&&
name|node
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
comment|// This is implemented to be a recursive ascent according to
comment|// section 2.5 in www.w3.org/TR/xpath-functions
comment|// see memtree/ElementImpl and dom/ElementImpl. /ljo
specifier|final
name|Node
name|domNode
init|=
name|node
operator|.
name|getNode
argument_list|()
decl_stmt|;
specifier|final
name|short
name|type
init|=
name|domNode
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
comment|//A direct processing instruction constructor creates a processing instruction node
comment|//whose target property is PITarget and whose content property is DirPIContents.
comment|//The base-uri property of the node is empty.
comment|//The parent property of the node is empty.
if|if
condition|(
name|type
operator|!=
name|Node
operator|.
name|DOCUMENT_NODE
operator|&&
name|type
operator|!=
name|Node
operator|.
name|ATTRIBUTE_NODE
operator|&&
name|domNode
operator|.
name|getParentNode
argument_list|()
operator|==
literal|null
condition|)
block|{
block|}
if|else if
condition|(
operator|(
name|type
operator|==
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|COMMENT_NODE
operator|)
operator|&&
operator|(
name|domNode
operator|.
name|getParentNode
argument_list|()
operator|!=
literal|null
operator|&&
name|domNode
operator|.
name|getParentNode
argument_list|()
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
operator|)
condition|)
block|{
comment|//Nothing to do
block|}
if|else if
condition|(
name|type
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|COMMENT_NODE
condition|)
block|{
name|URI
name|relativeURI
init|=
literal|null
decl_stmt|;
name|URI
name|baseURI
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|uri
init|=
name|domNode
operator|.
name|getBaseURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|relativeURI
operator|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|baseURI
operator|=
operator|new
name|URI
argument_list|(
name|context
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|relativeURI
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|relativeURI
operator|.
name|toString
argument_list|()
argument_list|)
operator|||
operator|(
name|type
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
operator|&&
literal|"/db"
operator|.
name|equals
argument_list|(
name|relativeURI
operator|.
name|toString
argument_list|()
argument_list|)
operator|)
operator|)
operator|)
condition|)
block|{
if|if
condition|(
name|relativeURI
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|relativeURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|baseURI
operator|.
name|resolve
argument_list|(
name|relativeURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|baseURI
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
block|}
end_class

end_unit

