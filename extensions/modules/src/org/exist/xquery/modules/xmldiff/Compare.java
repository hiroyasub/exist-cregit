begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2006-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xmldiff
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
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|Diff
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
name|storage
operator|.
name|serializers
operator|.
name|Serializer
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
name|value
operator|.
name|*
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|Compare
extends|extends
name|Function
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Compare
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|OUTPUT_PROPERTIES
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"compare"
argument_list|,
name|XmlDiffModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XmlDiffModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns true() if the two node sets $node-set-1 and $node-set-2 are equal, otherwise false()"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set-1"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the first node set"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set-2"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the second node set"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"true() if the two node sets $node-set-1 and $node-set-2 are equal, otherwise false()"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Compare
parameter_list|(
name|XQueryContext
name|context
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
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[],      *      org.exist.xquery.value.Sequence)      */
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
name|Expression
name|arg1
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Sequence
name|s1
init|=
name|arg1
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Expression
name|arg2
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|Sequence
name|s2
init|=
name|arg2
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
name|popDocumentContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|s1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|s2
operator|.
name|isEmpty
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|s2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|s1
operator|.
name|isEmpty
argument_list|()
argument_list|)
return|;
block|}
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|v1
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|v2
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|s1
operator|.
name|hasMany
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s1
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|v1
operator|.
name|append
argument_list|(
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|s1
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|v1
operator|.
name|append
argument_list|(
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|s1
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s2
operator|.
name|hasMany
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s2
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|v2
operator|.
name|append
argument_list|(
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|s2
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|v2
operator|.
name|append
argument_list|(
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|s2
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Diff
name|d
init|=
operator|new
name|Diff
argument_list|(
name|v1
operator|.
name|toString
argument_list|()
argument_list|,
name|v2
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|identical
init|=
name|d
operator|.
name|identical
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|identical
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Diff result: "
operator|+
name|d
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|BooleanValue
argument_list|(
name|identical
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An exception occurred while serializing node "
operator|+
literal|"for comparison: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
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
return|return
name|result
return|;
block|}
specifier|private
name|String
name|serialize
parameter_list|(
name|NodeValue
name|node
parameter_list|)
throws|throws
name|SAXException
block|{
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|OUTPUT_PROPERTIES
argument_list|)
expr_stmt|;
return|return
name|serializer
operator|.
name|serialize
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

