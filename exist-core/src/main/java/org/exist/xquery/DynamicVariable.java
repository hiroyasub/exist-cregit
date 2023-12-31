begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|Immutable
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_class
annotation|@
name|Immutable
specifier|public
class|class
name|DynamicVariable
implements|implements
name|Variable
block|{
specifier|private
specifier|final
name|QName
name|name
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Sequence
argument_list|>
name|valueSupplier
decl_stmt|;
specifier|public
name|DynamicVariable
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|Supplier
argument_list|<
name|Sequence
argument_list|>
name|valueSupplier
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|valueSupplier
operator|=
name|valueSupplier
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|Sequence
name|val
parameter_list|)
block|{
name|throwImmutable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getValue
parameter_list|()
block|{
return|return
name|valueSupplier
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|valueSupplier
operator|.
name|get
argument_list|()
operator|.
name|getItemType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSequenceType
parameter_list|(
specifier|final
name|SequenceType
name|type
parameter_list|)
block|{
name|throwImmutable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SequenceType
name|getSequenceType
parameter_list|()
block|{
specifier|final
name|Sequence
name|value
init|=
name|getValue
argument_list|()
decl_stmt|;
return|return
operator|new
name|SequenceType
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|value
operator|.
name|getCardinality
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setStaticType
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
name|throwImmutable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getStaticType
parameter_list|()
block|{
return|return
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInitialized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setIsInitialized
parameter_list|(
specifier|final
name|boolean
name|initialized
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cardinality
name|getCardinality
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getCardinality
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setStackPosition
parameter_list|(
specifier|final
name|int
name|position
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|DocumentSet
name|getContextDocs
parameter_list|()
block|{
return|return
name|DocumentSet
operator|.
name|EMPTY_DOCUMENT_SET
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContextDocs
parameter_list|(
specifier|final
name|DocumentSet
name|docs
parameter_list|)
block|{
name|throwImmutable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkType
parameter_list|()
block|{
block|}
specifier|private
specifier|static
name|void
name|throwImmutable
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Changing a dynamic variable is not permitted"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

