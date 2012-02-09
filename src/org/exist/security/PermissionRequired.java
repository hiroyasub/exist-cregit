begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|value
operator|=
block|{
name|ElementType
operator|.
name|METHOD
block|,
name|ElementType
operator|.
name|PARAMETER
block|}
argument_list|)
specifier|public
annotation_defn|@interface
name|PermissionRequired
block|{
comment|//int mode() default UNDEFINED;
name|int
name|user
parameter_list|()
default|default
name|UNDEFINED
function_decl|;
name|int
name|group
parameter_list|()
default|default
name|UNDEFINED
function_decl|;
name|int
name|mode
parameter_list|()
default|default
name|UNDEFINED
function_decl|;
specifier|public
specifier|final
specifier|static
name|int
name|UNDEFINED
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IS_DBA
init|=
literal|04
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IS_OWNER
init|=
literal|02
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IS_MEMBER
init|=
literal|40
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ACL_WRITE
init|=
literal|04
decl_stmt|;
block|}
end_annotation_defn

end_unit

