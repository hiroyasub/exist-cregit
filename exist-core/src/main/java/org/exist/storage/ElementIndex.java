begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database   * Copyright (C) 2001-06 The eXist Project  *   * This library is free software; you can redistribute it and/or modify it under  * the terms of the GNU Library General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This library is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more  * details.  *   * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|persistent
operator|.
name|DocumentImpl
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
name|persistent
operator|.
name|DocumentSet
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
name|persistent
operator|.
name|ExtNodeSet
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|NodeSet
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
name|security
operator|.
name|PermissionDeniedException
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
name|Occurrences
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
name|NodeSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/** base class for {@link org.exist.storage.structural.NativeStructuralIndex} */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ElementIndex
extends|extends
name|Observable
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ElementIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/** The broker that is using this value index */
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
specifier|protected
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|ArrayList
argument_list|<
name|NodeProxy
argument_list|>
argument_list|>
name|pending
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** The current document */
specifier|protected
name|DocumentImpl
name|doc
decl_stmt|;
specifier|protected
name|boolean
name|inUpdateMode
init|=
literal|false
decl_stmt|;
specifier|public
name|ElementIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
if|if
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|this
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|!=
name|doc
operator|.
name|getDocId
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Document changed but pending had "
operator|+
name|pending
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
name|pending
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
specifier|public
name|void
name|setInUpdateMode
parameter_list|(
name|boolean
name|update
parameter_list|)
block|{
name|inUpdateMode
operator|=
name|update
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|NodeSet
name|findElementsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|,
name|NodeSelector
name|selector
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|NodeSet
name|findDescendantsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|axis
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|ExtNodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|Occurrences
index|[]
name|scanIndexedElements
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|matchElementsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|,
name|NodeSelector
name|selector
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|matchDescendantsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|axis
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|ExtNodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
function_decl|;
block|}
end_class

end_unit

