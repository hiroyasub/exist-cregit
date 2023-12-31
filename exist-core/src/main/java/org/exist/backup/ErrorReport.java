begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
package|;
end_package

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

begin_class
specifier|public
class|class
name|ErrorReport
block|{
specifier|public
specifier|final
specifier|static
name|int
name|INCORRECT_NODE_ID
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INCORRECT_NODE_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NODE_HIERARCHY
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS_FAILED
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CHILD_COLLECTION
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|RESOURCE_ACCESS_FAILED
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DOM_INDEX
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CONFIGURATION_FAILD
init|=
literal|7
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|ERRCODES
init|=
block|{
literal|"ERR_NODE_ID"
block|,
literal|"ERR_NODE_TYPE"
block|,
literal|"ERR_NODE_HIERARCHY"
block|,
literal|"ERR_ACCESS"
block|,
literal|"ERR_CHILD_COLLECTION"
block|,
literal|"RESOURCE_ACCESS_FAILED"
block|,
literal|"ERR_DOM_INDEX"
block|}
decl_stmt|;
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
specifier|private
name|String
name|message
init|=
literal|null
decl_stmt|;
specifier|private
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
specifier|public
name|ErrorReport
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|ErrorReport
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|exception
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
specifier|public
name|int
name|getErrcode
parameter_list|()
block|{
return|return
operator|(
name|code
operator|)
return|;
block|}
specifier|public
name|String
name|getErrcodeString
parameter_list|()
block|{
return|return
operator|(
name|ERRCODES
index|[
name|code
index|]
operator|)
return|;
block|}
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
operator|(
name|message
operator|)
return|;
block|}
specifier|public
name|void
name|setMessage
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|Throwable
name|getException
parameter_list|()
block|{
return|return
operator|(
name|exception
operator|)
return|;
block|}
specifier|public
name|void
name|setException
parameter_list|(
specifier|final
name|Throwable
name|exception
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ERRCODES
index|[
name|code
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|":\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|sb
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|static
class|class
name|ResourceError
extends|extends
name|ErrorReport
block|{
specifier|private
name|int
name|documentId
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|ResourceError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ResourceError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|exception
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getDocumentId
parameter_list|()
block|{
return|return
operator|(
name|documentId
operator|)
return|;
block|}
specifier|public
name|void
name|setDocumentId
parameter_list|(
specifier|final
name|int
name|documentId
parameter_list|)
block|{
name|this
operator|.
name|documentId
operator|=
name|documentId
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"\nDocument ID: "
operator|+
name|documentId
operator|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|CollectionError
extends|extends
name|ErrorReport
block|{
specifier|private
name|int
name|collectionId
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|XmldbURI
name|collectionURI
init|=
literal|null
decl_stmt|;
specifier|public
name|CollectionError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CollectionError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|exception
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getCollectionId
parameter_list|()
block|{
return|return
operator|(
name|collectionId
operator|)
return|;
block|}
specifier|public
name|void
name|setCollectionId
parameter_list|(
specifier|final
name|int
name|collectionId
parameter_list|)
block|{
name|this
operator|.
name|collectionId
operator|=
name|collectionId
expr_stmt|;
block|}
specifier|public
name|XmldbURI
name|getCollectionURI
parameter_list|()
block|{
return|return
operator|(
name|collectionURI
operator|)
return|;
block|}
specifier|public
name|void
name|setCollectionURI
parameter_list|(
specifier|final
name|XmldbURI
name|collectionURI
parameter_list|)
block|{
name|this
operator|.
name|collectionURI
operator|=
name|collectionURI
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"\nCollection ID: "
operator|+
name|collectionId
operator|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|IndexError
extends|extends
name|ErrorReport
block|{
specifier|private
name|int
name|documentId
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|IndexError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|int
name|documentId
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentId
operator|=
name|documentId
expr_stmt|;
block|}
specifier|public
name|IndexError
parameter_list|(
specifier|final
name|int
name|code
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|exception
parameter_list|,
specifier|final
name|int
name|documentId
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentId
operator|=
name|documentId
expr_stmt|;
block|}
specifier|public
name|int
name|getDocumentId
parameter_list|()
block|{
return|return
operator|(
name|documentId
operator|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"\nDocument ID: "
operator|+
name|documentId
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

