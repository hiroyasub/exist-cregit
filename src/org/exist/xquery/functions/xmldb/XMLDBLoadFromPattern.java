begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|DirectoryScanner
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
name|EXistResource
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBLoadFromPattern
extends|extends
name|XMLDBAbstractCollectionManipulator
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
literal|"store-files-from-pattern"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Store new resources into the database. Resources are read from the server's "
operator|+
literal|"file system, using the file pattern specified in the second argument. File pattern matching "
operator|+
literal|"is based on code from Apache's Ant, thus following the same conventions. For example: "
operator|+
literal|"*.xml matches any file ending with .xml in the current directory, **/*.xml matches files "
operator|+
literal|"in any directory below the current one. "
operator|+
literal|"The first argument denotes the collection where resources should be stored. "
operator|+
literal|"The collection can be either specified as a simple collection path, "
operator|+
literal|"an XMLDB URI, or a collection object as returned by the collection or "
operator|+
literal|"create-collection functions. The function returns a sequence of all document paths added "
operator|+
literal|"to the db. These can be directly passed to fn:doc() to retrieve the document."
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
name|EXACTLY_ONE
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
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
literal|"store-files-from-pattern"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Store new resources into the database. Resources are read from the server's "
operator|+
literal|"file system, using the file pattern specified in the second argument. File pattern matching "
operator|+
literal|"is based on code from Apache's Ant, thus following the same conventions. For example: "
operator|+
literal|"*.xml matches any file ending with .xml in the current directory, **/*.xml matches files "
operator|+
literal|"in any directory below the current one. "
operator|+
literal|"The first argument denotes the collection where resources should be stored. "
operator|+
literal|"The collection can be either specified as a simple collection path, "
operator|+
literal|"an XMLDB URI, or a collection object as returned by the collection or "
operator|+
literal|"create-collection functions. The function returns a sequence of all document paths added "
operator|+
literal|"to the db. These can be directly passed to fn:doc() to retrieve the document. The final argument $d is used to specify a mime-type.  If the mime-type "
operator|+
literal|"is something other than 'text/xml' or 'application/xml', the resource will be stored as "
operator|+
literal|"a binary resource."
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
name|EXACTLY_ONE
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XMLDBLoadFromPattern
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.xmldb.XMLDBAbstractCollectionManipulator#evalWithCollection(org.xmldb.api.base.Collection, org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|protected
name|Sequence
name|evalWithCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
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
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading files from directory: "
operator|+
name|baseDir
argument_list|)
expr_stmt|;
name|Sequence
name|patterns
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|String
name|resourceType
init|=
literal|"XMLResource"
decl_stmt|;
name|String
name|mimeType
init|=
literal|"text/xml"
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|4
condition|)
block|{
name|mimeType
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
literal|"text/xml"
operator|.
name|equals
argument_list|(
name|mimeType
argument_list|)
operator|||
literal|"application/xml"
operator|.
name|equals
argument_list|(
name|mimeType
argument_list|)
operator|)
condition|)
name|resourceType
operator|=
literal|"BinaryResource"
expr_stmt|;
block|}
name|ValueSequence
name|stored
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|patterns
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
name|pattern
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|File
index|[]
name|files
init|=
name|DirectoryScanner
operator|.
name|scanDir
argument_list|(
name|baseDir
argument_list|,
name|pattern
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
try|try
block|{
comment|//TODO: these probably need to be encoded
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|resourceType
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|files
index|[
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"BinaryResource"
operator|.
name|equals
argument_list|(
name|resourceType
argument_list|)
condition|)
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|setMimeType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
comment|//TODO : use dedicated function in XmldbURI
name|stored
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not store file "
operator|+
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|stored
return|;
block|}
block|}
end_class

end_unit

