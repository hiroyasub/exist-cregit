begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|lucene
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
name|dom
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
name|dom
operator|.
name|persistent
operator|.
name|NodeProxy
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
name|lucene
operator|.
name|LuceneIndex
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
name|lucene
operator|.
name|LuceneIndexWorker
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Class implementing the ft:search() method  * @author<a href="mailto:dannes@exist-db.org">Dannes Wessels</a>  */
end_comment

begin_class
specifier|public
class|class
name|Search
extends|extends
name|BasicFunction
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
name|Search
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Function signatures      */
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
literal|"search"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Search for (non-XML) data with lucene"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"URI paths of documents or collections in database. Collection URIs should end on a '/'."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"query string"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"fields"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Fields to return in search results"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"An XML fragment containing options to be passed to Lucene's query parser. The following "
operator|+
literal|"options are supported (a description can be found in the docs):\n"
operator|+
literal|"<options>\n"
operator|+
literal|"<default-operator>and|or</default-operator>\n"
operator|+
literal|"<phrase-slop>number</phrase-slop>\n"
operator|+
literal|"<leading-wildcard>yes|no</leading-wildcard>\n"
operator|+
literal|"<filter-rewrite>yes|no</filter-rewrite>\n"
operator|+
literal|"</options>"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"All documents that are match by the query"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"search"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Search for (non-XML) data with lucene"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"URI paths of documents or collections in database. Collection URIs should end on a '/'."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"query string"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"fields"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Fields to return in search results"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"All documents that are match by the query"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"search"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Search for (non-XML) data with lucene"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"URI paths of documents or collections in database. Collection URIs should end on a '/'."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"query string"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"All documents that are match by the query"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"search"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Search for (non-XML) data with lucene"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"query string"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"All documents that are match by the query"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Search
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
annotation|@
name|Override
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
name|NodeImpl
name|report
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Only match documents that match these URLs
name|List
argument_list|<
name|String
argument_list|>
name|toBeMatchedURIs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Sequence
name|pathSeq
init|=
name|getArgumentCount
argument_list|()
operator|>
literal|1
condition|?
name|args
index|[
literal|0
index|]
else|:
name|contextSequence
decl_stmt|;
if|if
condition|(
name|pathSeq
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|// Get first agument, these are the documents / collections to search in
for|for
control|(
name|SequenceIterator
name|i
init|=
name|pathSeq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|path
decl_stmt|;
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
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
if|if
condition|(
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
name|path
operator|=
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|item
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|path
operator|=
name|item
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
name|toBeMatchedURIs
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|// Get second argument, this is the query
name|String
name|query
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
name|query
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
else|else
name|query
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|String
index|[]
name|fields
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
name|fields
operator|=
operator|new
name|String
index|[
name|args
index|[
literal|2
index|]
operator|.
name|getItemCount
argument_list|()
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|2
index|]
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|fields
index|[
name|j
operator|++
index|]
operator|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Get the lucene worker
name|LuceneIndexWorker
name|index
init|=
operator|(
name|LuceneIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|QueryOptions
name|options
init|=
name|Query
operator|.
name|parseOptions
argument_list|(
name|this
argument_list|,
name|contextSequence
argument_list|,
literal|null
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// Perform search
name|report
operator|=
name|index
operator|.
name|search
argument_list|(
name|context
argument_list|,
name|toBeMatchedURIs
argument_list|,
name|query
argument_list|,
name|fields
argument_list|,
name|options
argument_list|)
expr_stmt|;
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
name|this
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
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
comment|// Log and rethrow
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
comment|// Return list of matching files.
return|return
name|report
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
block|}
end_class

end_unit

