begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * A simple source object wrapping around a single string value.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|StringSource
extends|extends
name|AbstractSource
block|{
specifier|private
name|String
name|data
decl_stmt|;
comment|/**      *       */
specifier|public
name|StringSource
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|content
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getKey()      */
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|data
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#isValid()      */
specifier|public
name|int
name|isValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|Source
operator|.
name|VALID
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#isValid(org.exist.source.Source)      */
specifier|public
name|int
name|isValid
parameter_list|(
name|Source
name|other
parameter_list|)
block|{
return|return
name|Source
operator|.
name|VALID
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getReader()      */
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getContent()      */
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

