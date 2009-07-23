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
name|xmldb
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
name|java
operator|.
name|util
operator|.
name|Date
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
name|xmldb
operator|.
name|CollectionImpl
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
name|DateTimeValue
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  *  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBCreated
extends|extends
name|XMLDBAbstractCollectionManipulator
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XMLDBCreated
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|createdSignatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"created"
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
literal|"Returns the creation date of $resource located in $collection-uri. "
operator|+
literal|"The collection can be passed as a simple collection "
operator|+
literal|"path or an XMLDB URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the collection"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the resuource"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"created"
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
literal|"Returns the creation date of $collection-uri. The collection can be passed as a simple collection "
operator|+
literal|"path or an XMLDB URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the collection"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|lastModifiedSignature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"last-modified"
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
literal|"Returns the last-modification date of $resource, located in "
operator|+
literal|"$collection-uri. The collection "
operator|+
literal|"can be passed as a simple collection path or an XMLDB URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the collection"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the resource"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|XMLDBCreated
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|XMLDBModule
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
try|try
block|{
name|Date
name|date
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|date
operator|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|collection
operator|)
operator|.
name|getCreationTime
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Resource
name|resource
init|=
name|collection
operator|.
name|getResource
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
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|XMLDBModule
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
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"last-modified"
argument_list|)
condition|)
name|date
operator|=
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|getLastModificationTime
argument_list|()
expr_stmt|;
else|else
name|date
operator|=
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|getCreationTime
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|XMLDBModule
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
operator|new
name|DateTimeValue
argument_list|(
name|date
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to retrieve creation date or modification time of specified resource or creation date of collection"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|XMLDBModule
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to retrieve creation date: "
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
block|}
block|}
end_class

end_unit

