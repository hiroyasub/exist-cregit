begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
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
name|collections
operator|.
name|Collection
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
name|NodeProxy
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
name|ExtArrayNodeSet
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|MetadataFunction
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MetadataFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"metadata"
argument_list|,
name|MetadataModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MetadataModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Retrieves metadata for the dynamic context."
operator|+
literal|"If the context item is undefined an error is raised."
argument_list|,
literal|null
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
name|ZERO_OR_ONE
argument_list|,
literal|"the metadata documents"
argument_list|)
argument_list|,
literal|"an orphaned experiment.  This will be removed in the next release after 1.4."
argument_list|)
decl_stmt|;
specifier|public
name|MetadataFunction
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|MetadataModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
comment|//must be a context to act on
if|if
condition|(
name|contextSequence
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
literal|"FONC0001: undefined context item"
argument_list|)
throw|;
block|}
comment|//iterate through the source documents
name|DocumentSet
name|sourceDocuments
init|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|Iterator
name|itSourceDocuments
init|=
name|sourceDocuments
operator|.
name|getDocumentIterator
argument_list|()
decl_stmt|;
name|NodeSet
name|metadataDocuments
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
name|sourceDocuments
operator|.
name|getDocumentCount
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Collection
name|metadataCollection
init|=
literal|null
decl_stmt|;
name|XmldbURI
name|lastMetadataCollectionURI
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|itSourceDocuments
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|//get the source document
name|DocumentImpl
name|sourceDoc
init|=
operator|(
name|DocumentImpl
operator|)
name|itSourceDocuments
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//get the uri for the corresponding metadata document
name|XmldbURI
name|metadataDocURI
init|=
name|XmldbURI
operator|.
name|METADATA_COLLECTION_URI
operator|.
name|append
argument_list|(
name|sourceDoc
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
comment|//get the uri for the corresponding metadata collection
name|String
name|tmpMetadataCollectionURI
init|=
name|metadataDocURI
operator|.
name|getCollectionPath
argument_list|()
decl_stmt|;
name|tmpMetadataCollectionURI
operator|=
name|tmpMetadataCollectionURI
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tmpMetadataCollectionURI
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|XmldbURI
name|metadataCollectionURI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|tmpMetadataCollectionURI
argument_list|)
decl_stmt|;
comment|//get the metadata document corresponding to the source document
name|DocumentImpl
name|metadataDoc
init|=
literal|null
decl_stmt|;
comment|//TODO: not sure that this collection fetch avoidance code is working correctly?
comment|//only refetch the collection if different uri
if|if
condition|(
operator|!
name|metadataCollectionURI
operator|.
name|equals
argument_list|(
name|lastMetadataCollectionURI
argument_list|)
condition|)
block|{
name|metadataCollection
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getCollection
argument_list|(
name|metadataCollectionURI
argument_list|)
expr_stmt|;
comment|//remeber the metadata collection uri
name|lastMetadataCollectionURI
operator|=
name|metadataCollectionURI
expr_stmt|;
block|}
comment|//is there a corresponding metadata collection?
if|if
condition|(
name|metadataCollection
operator|!=
literal|null
condition|)
block|{
comment|//is there a corresponding metadata document?
if|if
condition|(
name|metadataCollection
operator|.
name|hasDocument
argument_list|(
name|metadataDocURI
operator|.
name|lastSegment
argument_list|()
argument_list|)
condition|)
block|{
comment|//get the metadata document
name|metadataDoc
operator|=
name|metadataCollection
operator|.
name|getDocument
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|metadataDocURI
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//if we find a metadata document, add it to the result set
if|if
condition|(
name|metadataDoc
operator|!=
literal|null
condition|)
block|{
name|metadataDocuments
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|metadataDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|MetadataModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|metadataDocuments
return|;
block|}
block|}
end_class

end_unit

