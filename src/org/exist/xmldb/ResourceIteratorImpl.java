begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001 Wolfgang M. Meier  * meier@ifs.tu-darmstadt.de  * http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
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
name|*
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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ResourceIteratorImpl
implements|implements
name|ResourceIterator
block|{
specifier|protected
name|XmlRpcClient
name|rpcClient
decl_stmt|;
specifier|protected
name|CollectionImpl
name|collection
decl_stmt|;
specifier|protected
name|Vector
name|resources
decl_stmt|;
specifier|protected
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|indentXML
decl_stmt|;
specifier|protected
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
name|ResourceIteratorImpl
parameter_list|(
name|CollectionImpl
name|col
parameter_list|,
name|Vector
name|resources
parameter_list|,
name|int
name|indentXML
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|resources
operator|=
name|resources
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|col
expr_stmt|;
name|this
operator|.
name|indentXML
operator|=
name|indentXML
expr_stmt|;
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
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
specifier|public
name|void
name|setNext
parameter_list|(
name|int
name|next
parameter_list|)
block|{
name|pos
operator|=
name|next
expr_stmt|;
block|}
specifier|public
name|Resource
name|nextResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|pos
operator|>=
name|resources
operator|.
name|size
argument_list|()
condition|)
return|return
literal|null
return|;
comment|// node or value?
if|if
condition|(
name|resources
operator|.
name|elementAt
argument_list|(
name|pos
argument_list|)
operator|instanceof
name|Vector
condition|)
block|{
comment|// node
name|Vector
name|v
init|=
operator|(
name|Vector
operator|)
name|resources
operator|.
name|elementAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
name|String
name|doc
init|=
operator|(
name|String
operator|)
name|v
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|s_id
init|=
operator|(
name|String
operator|)
name|v
operator|.
name|elementAt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|s_id
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|indentXML
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|collection
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"retrieve"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|XMLResource
name|res
init|=
operator|new
name|XMLResourceImpl
argument_list|(
name|collection
argument_list|,
name|doc
argument_list|,
name|doc
operator|+
literal|"_"
operator|+
name|s_id
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
operator|(
name|Object
operator|)
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// value
name|XMLResource
name|res
init|=
operator|new
name|XMLResourceImpl
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|pos
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|resources
operator|.
name|elementAt
argument_list|(
name|pos
operator|++
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
block|}
end_class

end_unit

