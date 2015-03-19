begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2006-2009 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: QNameIndexLookup.java 3063 2006-04-05 20:49:44Z brihaye $  */
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|persistent
operator|.
name|NodeSet
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
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|OrderedValuesIndex
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
name|Indexable
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
name|Occurrences
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
name|ValueOccurrences
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
name|IntegerValue
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

begin_comment
comment|/**  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|IndexKeyDocuments
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
name|IndexKeyDocuments
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|nodeParam
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The nodes whose content is indexed"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|valueParam
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The indexed value to search for"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|indexParam
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"index"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The index in which the search is made"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|result
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the number of documents for the indexed value"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-key-documents"
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
literal|"Return the number of documents for an indexed value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|nodeParam
block|,
name|valueParam
block|}
argument_list|,
name|result
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-key-documents"
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
literal|"Return the number of documents for an indexed value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|nodeParam
block|,
name|valueParam
block|,
name|indexParam
block|}
argument_list|,
name|result
argument_list|)
block|}
decl_stmt|;
specifier|public
name|IndexKeyDocuments
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
decl_stmt|;
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
specifier|final
name|NodeSet
name|nodes
init|=
name|args
index|[
literal|0
index|]
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
specifier|final
name|DocumentSet
name|docs
init|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
specifier|final
name|IndexWorker
name|indexWorker
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexName
argument_list|(
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
comment|//Alternate design
comment|//IndexWorker indexWorker = context.getBroker().getBrokerPool().getIndexManager().getIndexByName(args[2].itemAt(0).getStringValue()).getWorker();
if|if
condition|(
name|indexWorker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown index: "
operator|+
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|hints
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexWorker
operator|instanceof
name|OrderedValuesIndex
condition|)
block|{
name|hints
operator|.
name|put
argument_list|(
name|OrderedValuesIndex
operator|.
name|START_VALUE
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
name|indexWorker
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" isn't an instance of org.exist.indexing.OrderedIndexWorker. Start value '"
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|"' ignored."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Occurrences
index|[]
name|occur
init|=
name|indexWorker
operator|.
name|scanIndex
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|nodes
argument_list|,
name|hints
argument_list|)
decl_stmt|;
if|if
condition|(
name|occur
operator|.
name|length
operator|==
literal|0
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
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
literal|0
index|]
operator|.
name|getDocuments
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|ValueOccurrences
name|occur
index|[]
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
operator|(
name|Indexable
operator|)
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|occur
operator|.
name|length
operator|==
literal|0
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
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
literal|0
index|]
operator|.
name|getDocuments
argument_list|()
argument_list|)
expr_stmt|;
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

