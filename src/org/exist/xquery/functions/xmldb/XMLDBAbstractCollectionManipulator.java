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
name|JavaObjectValue
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
name|XMLDBException
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
name|name
argument_list|,
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
return|;
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
literal|"Expected a collection as the first argument."
argument_list|)
throw|;
name|boolean
name|collectionNeedsClose
init|=
literal|true
decl_stmt|;
comment|// If the incoming is a collection object, use it:
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
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|JAVA_OBJECT
condition|)
block|{
name|Object
name|o
init|=
operator|(
operator|(
name|JavaObjectValue
operator|)
name|item
operator|)
operator|.
name|getObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
name|collection
operator|=
operator|(
name|Collection
operator|)
name|o
expr_stmt|;
block|}
if|else if
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
name|collection
operator|=
name|createLocalCollection
argument_list|(
name|internalCol
operator|.
name|getName
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
name|getName
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
literal|null
operator|==
name|collection
condition|)
block|{
comment|// Otherwise, just extract the name as a string:
name|String
name|collectionURI
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|collectionURI
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
literal|null
operator|==
name|collection
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
else|else
block|{
comment|// Don't close incoming JavaObjects:
name|collectionNeedsClose
operator|=
literal|false
expr_stmt|;
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
block|}
end_class

end_unit

