begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
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
name|DocumentAtExist
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
name|com
operator|.
name|eaio
operator|.
name|uuid
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|EntityCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|model
operator|.
name|DeleteAction
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|model
operator|.
name|Entity
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|model
operator|.
name|PrimaryKey
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|model
operator|.
name|SecondaryKey
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|sleepycat
operator|.
name|persist
operator|.
name|model
operator|.
name|Relationship
operator|.
name|ONE_TO_ONE
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|Entity
comment|//(version=1) //http://docs.oracle.com/cd/E17076_02/html/java/com/sleepycat/persist/evolve/Conversion.html
specifier|public
class|class
name|MetasImpl
implements|implements
name|Metas
block|{
annotation|@
name|PrimaryKey
specifier|private
name|String
name|uuid
decl_stmt|;
annotation|@
name|SecondaryKey
argument_list|(
name|relate
operator|=
name|ONE_TO_ONE
argument_list|,
name|onRelatedEntityDelete
operator|=
name|DeleteAction
operator|.
name|CASCADE
argument_list|)
specifier|protected
name|String
name|uri
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|MetasImpl
parameter_list|()
block|{
block|}
specifier|protected
name|MetasImpl
parameter_list|(
name|DocumentAtExist
name|doc
parameter_list|)
block|{
name|update
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|getUUID
argument_list|()
operator|==
literal|null
condition|)
name|uuid
operator|=
operator|(
operator|new
name|UUID
argument_list|()
operator|)
operator|.
name|toString
argument_list|()
expr_stmt|;
else|else
name|uuid
operator|=
name|doc
operator|.
name|getUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MetasImpl
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MetasImpl
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|uuid
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
block|}
specifier|public
name|String
name|getUUID
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
specifier|public
name|Meta
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|MetaImpl
name|m
init|=
operator|(
name|MetaImpl
operator|)
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
return|return
name|MetaDataImpl
operator|.
name|_
operator|.
name|addMeta
argument_list|(
name|this
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
return|;
else|else
block|{
name|m
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|MetaDataImpl
operator|.
name|_
operator|.
name|addMeta
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
specifier|public
name|Meta
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|MetaDataImpl
operator|.
name|_
operator|.
name|getMeta
argument_list|(
name|this
argument_list|,
name|key
argument_list|)
return|;
block|}
specifier|protected
name|void
name|update
parameter_list|(
name|DocumentAtExist
name|doc
parameter_list|)
block|{
name|uri
operator|=
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|EntityCursor
argument_list|<
name|MetaImpl
argument_list|>
name|keys
parameter_list|()
block|{
return|return
name|MetaDataImpl
operator|.
name|_
operator|.
name|getMetaKeys
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|void
name|restore
parameter_list|(
name|String
name|uuid
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|MetaDataImpl
operator|.
name|_
operator|.
name|_addMeta
argument_list|(
name|this
argument_list|,
name|uuid
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

