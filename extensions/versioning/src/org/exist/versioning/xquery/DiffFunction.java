begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
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
name|DateTimeValue
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
name|NodeProxy
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
name|DocumentBuilderReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|VersioningTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
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
name|versioning
operator|.
name|StandardDiff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|DiffException
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_class
specifier|public
class|class
name|DiffFunction
extends|extends
name|BasicFunction
block|{
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
literal|"diff"
argument_list|,
name|VersioningModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|VersioningModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a diff between two documents (which normally means two "
operator|+
literal|"versions of the same document). Both documents should be stored in the "
operator|+
literal|"database. The function will not work with in-memory documents. The returned "
operator|+
literal|"diff uses the same format as generated by the VersioningTrigger."
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
name|NODE
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
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|DiffFunction
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
name|NodeValue
name|nv1
init|=
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
decl_stmt|;
name|NodeValue
name|nv2
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|nv1
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
operator|||
name|nv2
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"diff function only works on persistent documents stored in the db"
argument_list|)
throw|;
name|DocumentImpl
name|doc1
init|=
operator|(
operator|(
name|NodeProxy
operator|)
name|nv1
operator|)
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc2
init|=
operator|(
operator|(
name|NodeProxy
operator|)
name|nv2
operator|)
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
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
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"document"
argument_list|,
name|doc1
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"revision"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"date"
argument_list|,
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"user"
argument_list|,
name|context
operator|.
name|getUser
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|VersioningTrigger
operator|.
name|ELEMENT_VERSION
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|VersioningTrigger
operator|.
name|writeProperties
argument_list|(
name|receiver
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|StandardDiff
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
name|diff
operator|.
name|diff
argument_list|(
name|doc1
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|diff
operator|.
name|diff2XML
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
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
name|this
argument_list|,
literal|"Caugt error while generating diff: "
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
catch|catch
parameter_list|(
name|DiffException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Caugt error while generating diff: "
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
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

