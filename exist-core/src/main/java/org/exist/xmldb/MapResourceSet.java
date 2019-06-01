begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|ArrayList
import|;
end_import

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
name|Iterator
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
name|Map
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
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * Implementation of ResourceSet (a container of Resource objects), using  * internally both a Map and a Vector. The Map is keyed by the Id of each  * resource.  *  * @author Jean-Marc Vanel (2 April 2003)  */
end_comment

begin_class
specifier|public
class|class
name|MapResourceSet
implements|implements
name|ResourceSet
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resources
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Resource
argument_list|>
name|resourcesVector
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|MapResourceSet
parameter_list|()
block|{
name|this
operator|.
name|resources
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MapResourceSet
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resources
parameter_list|)
block|{
name|this
operator|.
name|resources
operator|=
name|resources
expr_stmt|;
for|for
control|(
name|Resource
name|res
range|:
name|resources
operator|.
name|values
argument_list|()
control|)
block|{
name|resourcesVector
operator|.
name|add
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|MapResourceSet
parameter_list|(
name|ResourceSet
name|rs
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|resources
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rs
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Resource
name|res
init|=
name|rs
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|resources
operator|.
name|put
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|resourcesVector
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getResourcesMap
parameter_list|()
block|{
return|return
name|resources
return|;
block|}
comment|/**      * Adds a resource to the container      *      * @param resource The resource to be added to the object      * @throws org.xmldb.api.base.XMLDBException      */
annotation|@
name|Override
specifier|public
name|void
name|addResource
parameter_list|(
specifier|final
name|Resource
name|resource
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|put
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|resourcesVector
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addAll
parameter_list|(
specifier|final
name|ResourceSet
name|resourceSet
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resourceSet
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|addResource
argument_list|(
name|resourceSet
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Make the container empty      *      * @throws XMLDBException      */
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Gets the iterator property      *      * @return The iterator value      * @throws XMLDBException      */
annotation|@
name|Override
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|()
return|;
block|}
comment|/**      * Gets the iterator property, starting from a given position      *      * @param start starting position>0 for the iterator      * @return The iterator value      * @throws XMLDBException thrown if pos is out of range      */
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|(
specifier|final
name|long
name|start
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|(
name|start
argument_list|)
return|;
block|}
comment|/**      * Gets the membersAsResource property of the object      *      * @return The membersAsResource value      * @exception XMLDBException Description of the Exception      */
annotation|@
name|Override
specifier|public
name|Resource
name|getMembersAsResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NOT_IMPLEMENTED
argument_list|)
throw|;
block|}
comment|/**      * Gets the resource at a given position.      *      * @param pos position> 0      * @return The resource value      * @exception XMLDBException thrown if pos is out of range      */
annotation|@
name|Override
specifier|public
name|Resource
name|getResource
parameter_list|(
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|resources
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Object
name|r
init|=
name|resourcesVector
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|instanceof
name|Resource
condition|)
block|{
return|return
operator|(
name|Resource
operator|)
name|r
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Gets the size property      *      * @return The size value      * @exception XMLDBException      */
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|long
operator|)
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Removes the resource at a given position.      *      * @param pos position> 0      * @exception XMLDBException thrown if pos is out of range      */
annotation|@
name|Override
specifier|public
name|void
name|removeResource
parameter_list|(
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Resource
name|r
init|=
name|resourcesVector
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
decl_stmt|;
name|resourcesVector
operator|.
name|remove
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
name|resources
operator|.
name|remove
argument_list|(
name|r
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Inner resource Iterator Class      *      */
class|class
name|NewResourceIterator
implements|implements
name|ResourceIterator
block|{
name|long
name|pos
init|=
literal|0
decl_stmt|;
comment|/**          * Constructor for the NewResourceIterator object          */
specifier|public
name|NewResourceIterator
parameter_list|()
block|{
block|}
comment|/**          * Constructor for the NewResourceIterator object          *          * @param start starting position>0 for the iterator          */
specifier|public
name|NewResourceIterator
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|pos
operator|=
name|start
expr_stmt|;
block|}
comment|/**          * Classical loop test.          *          * @return Description of the Return Value          * @exception XMLDBException Description of the Exception          */
annotation|@
name|Override
specifier|public
name|boolean
name|hasMoreResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|pos
operator|<
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**          * Classical accessor to next Resource          *          * @return the next Resource          * @exception XMLDBException          */
annotation|@
name|Override
specifier|public
name|Resource
name|nextResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getResource
argument_list|(
name|pos
operator|++
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
