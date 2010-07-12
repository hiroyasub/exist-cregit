begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
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
name|Document
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SourceImpl
implements|implements
name|Source
block|{
specifier|public
specifier|static
specifier|final
name|int
name|NOT_DEFAINED
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DOM
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EXIST_Sequence
init|=
literal|3
decl_stmt|;
name|String
name|systemId
init|=
literal|null
decl_stmt|;
name|Object
name|source
decl_stmt|;
name|int
name|type
init|=
name|NOT_DEFAINED
decl_stmt|;
specifier|public
name|SourceImpl
parameter_list|(
name|Document
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|type
operator|=
name|DOM
expr_stmt|;
block|}
specifier|public
name|SourceImpl
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|type
operator|=
name|EXIST_Sequence
expr_stmt|;
block|}
specifier|public
name|SourceImpl
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|type
operator|=
name|STRING
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Source#getSystemId() 	 */
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
name|systemId
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Source#setSystemId(java.lang.String) 	 */
specifier|public
name|void
name|setSystemId
parameter_list|(
name|String
name|systemId
parameter_list|)
block|{
name|this
operator|.
name|systemId
operator|=
name|systemId
expr_stmt|;
block|}
specifier|public
name|Object
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|type
operator|==
name|STRING
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|source
return|;
block|}
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

