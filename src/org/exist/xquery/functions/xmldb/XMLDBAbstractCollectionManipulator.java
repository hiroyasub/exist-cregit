begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  Modifications Copyright (C) 2004 Luigi P. Bai  *  finder@users.sf.net  *  Licensed as below under the LGPL.  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    */
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
name|util
operator|.
name|StringTokenizer
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
name|xmldb
operator|.
name|LocalCollection
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
name|ErrorCodes
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|XMLDBAbstractCollectionManipulator
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
name|boolean
name|errorIfAbsent
decl_stmt|;
specifier|private
name|int
name|paramNumber
init|=
literal|0
decl_stmt|;
comment|//collecton will be passed as parameter number 0 by default
specifier|protected
name|void
name|setCollectionParameterNunmer
parameter_list|(
name|int
name|paramNumber
parameter_list|)
block|{
name|this
operator|.
name|paramNumber
operator|=
name|paramNumber
expr_stmt|;
block|}
specifier|public
name|XMLDBAbstractCollectionManipulator
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|signature
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XMLDBAbstractCollectionManipulator
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|,
name|boolean
name|errorIfAbsent
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorIfAbsent
operator|=
name|errorIfAbsent
expr_stmt|;
block|}
specifier|protected
name|LocalCollection
name|createLocalCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
operator|new
name|LocalCollection
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
name|name
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
argument_list|,
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
literal|0
operator|==
name|args
operator|.
name|length
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Expected a collection as the argument "
operator|+
operator|(
name|paramNumber
operator|+
literal|1
operator|)
operator|+
literal|"."
argument_list|)
throw|;
name|boolean
name|collectionNeedsClose
init|=
literal|false
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|Item
name|item
init|=
name|args
index|[
name|paramNumber
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found node"
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|internalCol
init|=
operator|(
operator|(
name|NodeProxy
operator|)
name|node
operator|)
operator|.
name|getDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found node"
argument_list|)
expr_stmt|;
try|try
block|{
comment|//TODO: use xmldbURI
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|internalCol
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded collection "
operator|+
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
literal|"Failed to access collection: "
operator|+
name|internalCol
operator|.
name|getURI
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
comment|//Otherwise, just extract the name as a string:
name|String
name|collectionURI
init|=
name|args
index|[
name|paramNumber
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectionURI
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|collectionURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:"
argument_list|)
condition|)
block|{
comment|// Must be a LOCAL collection
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|collectionURI
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|collectionURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist:///"
argument_list|)
condition|)
block|{
comment|// Must be a LOCAL collection
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|collectionURI
operator|.
name|replaceFirst
argument_list|(
literal|"xmldb:exist://"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|collectionURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://localhost"
argument_list|)
condition|)
block|{
comment|// Must be a LOCAL collection
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|collectionURI
operator|.
name|replaceFirst
argument_list|(
literal|"xmldb:exist://localhost"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|collectionURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://127.0.0.1"
argument_list|)
condition|)
block|{
comment|// Must be a LOCAL collection
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|collectionURI
operator|.
name|replaceFirst
argument_list|(
literal|"xmldb:exist://127.0.0.1"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Right now, the collection is retrieved as GUEST. Need to figure out how to
comment|// get user information into the URL?
name|collection
operator|=
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionURI
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
if|if
condition|(
name|errorIfAbsent
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Could not locate collection: "
operator|+
name|collectionURI
argument_list|,
name|xe
argument_list|)
throw|;
name|collection
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|collection
operator|==
literal|null
operator|&&
name|errorIfAbsent
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Unable to find collection: "
operator|+
name|collectionURI
argument_list|)
throw|;
block|}
block|}
name|Sequence
name|s
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
try|try
block|{
name|s
operator|=
name|evalWithCollection
argument_list|(
name|collection
argument_list|,
name|args
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|collectionNeedsClose
operator|&&
name|collection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|collection
operator|.
name|close
argument_list|()
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
name|getASTNode
argument_list|()
argument_list|,
literal|"Unable to close collection"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|s
return|;
block|}
specifier|abstract
specifier|protected
name|Sequence
name|evalWithCollection
parameter_list|(
name|Collection
name|c
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
function_decl|;
specifier|protected
specifier|final
name|Collection
name|createCollection
parameter_list|(
name|Collection
name|parentColl
parameter_list|,
name|String
name|relPath
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|XPathException
block|{
name|CollectionManagementService
name|mgtService
decl_stmt|;
name|Collection
name|current
init|=
name|parentColl
decl_stmt|,
name|c
decl_stmt|;
name|String
name|token
decl_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
operator|new
name|AnyURIValue
argument_list|(
name|relPath
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|c
operator|=
name|current
operator|.
name|getChildCollection
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|mgtService
operator|=
operator|(
name|CollectionManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|current
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
name|current
operator|=
name|c
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
block|}
end_class

end_unit

