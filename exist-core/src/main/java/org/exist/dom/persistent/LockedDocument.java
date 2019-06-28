begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|ManagedDocumentLock
import|;
end_import

begin_comment
comment|/**  * Just a wrapper around a  {@link DocumentImpl} which allows us to also hold a lock  * lease which is released when {@link #close()} is called. This  * allows us to use ARM (Automatic Resource Management) e.g. try-with-resources  * with eXist Document objects  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|LockedDocument
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|final
name|ManagedDocumentLock
name|managedDocumentLock
decl_stmt|;
specifier|private
specifier|final
name|DocumentImpl
name|document
decl_stmt|;
specifier|public
name|LockedDocument
parameter_list|(
specifier|final
name|ManagedDocumentLock
name|managedDocumentLock
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
block|{
name|this
operator|.
name|managedDocumentLock
operator|=
name|managedDocumentLock
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
block|}
comment|/**      * Get the document      *      * @return the locked document      */
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
comment|/**      * Unlocks the Document      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|managedDocumentLock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

