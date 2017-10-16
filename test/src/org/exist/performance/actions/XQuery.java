begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|actions
package|;
end_package

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
name|exist
operator|.
name|performance
operator|.
name|AbstractAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|EXistXQueryService
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
name|ResourceSet
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
name|ResourceIterator
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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

begin_class
specifier|public
class|class
name|XQuery
extends|extends
name|AbstractAction
block|{
specifier|private
specifier|final
specifier|static
name|String
name|OPTIMIZE
init|=
literal|"declare option exist:optimize 'enable=yes';\n"
decl_stmt|;
specifier|private
name|String
name|query
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|collectionPath
decl_stmt|;
specifier|private
name|boolean
name|retrieve
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|forceOptimize
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|lastResult
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Runner
name|runner
parameter_list|,
name|Action
name|parent
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|configure
argument_list|(
name|runner
argument_list|,
name|parent
argument_list|,
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"optimize"
argument_list|)
condition|)
name|forceOptimize
operator|=
name|getBooleanValue
argument_list|(
name|config
argument_list|,
literal|"optimize"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
else|else
name|forceOptimize
operator|=
name|getBooleanValue
argument_list|(
operator|(
name|Element
operator|)
name|config
operator|.
name|getParentNode
argument_list|()
argument_list|,
literal|"optimize"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"query"
argument_list|)
condition|)
name|query
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
else|else
block|{
name|Node
name|child
init|=
name|config
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|child
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
name|query
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"collection"
argument_list|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
name|StoreFromFile
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" requires an attribute 'collection'"
argument_list|)
throw|;
name|collectionPath
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"retrieve-results"
argument_list|)
condition|)
block|{
name|String
name|option
init|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"retrieve-results"
argument_list|)
decl_stmt|;
name|retrieve
operator|=
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
operator|||
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|EXistException
block|{
name|Collection
name|collection
init|=
name|connection
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"collection "
operator|+
name|collectionPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|EXistXQueryService
name|service
init|=
operator|(
name|EXistXQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getParent
argument_list|()
operator|.
name|getNamespaces
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|getParent
argument_list|()
operator|.
name|getNamespaces
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|service
operator|.
name|setNamespace
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|forceOptimize
condition|?
name|OPTIMIZE
operator|+
name|query
else|:
name|query
argument_list|)
decl_stmt|;
name|lastResult
operator|=
operator|(
name|int
operator|)
name|result
operator|.
name|getSize
argument_list|()
expr_stmt|;
if|if
condition|(
name|retrieve
condition|)
block|{
for|for
control|(
name|ResourceIterator
name|i
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|r
init|=
name|i
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|r
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|getLastResult
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|lastResult
argument_list|)
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
name|description
operator|==
literal|null
condition|?
name|query
else|:
name|description
operator|)
return|;
block|}
block|}
end_class

end_unit

