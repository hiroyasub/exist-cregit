begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCommand
implements|implements
name|Command
block|{
specifier|private
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
specifier|private
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
specifier|protected
name|String
index|[]
name|names
init|=
literal|null
decl_stmt|;
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
block|{
return|return
name|names
return|;
block|}
specifier|public
name|PrintStream
name|out
parameter_list|()
block|{
return|return
name|out
return|;
block|}
specifier|public
name|PrintStream
name|err
parameter_list|()
block|{
return|return
name|err
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.plugin.command.Command#process(java.lang.String[]) 	 */
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|String
index|[]
name|params
parameter_list|)
throws|throws
name|CommandException
block|{
name|process
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.plugin.command.Command#process(org.exist.xmldb.XmldbURI, java.lang.String[]) 	 */
annotation|@
name|Override
specifier|public
specifier|abstract
name|void
name|process
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
index|[]
name|commandData
parameter_list|)
throws|throws
name|CommandException
function_decl|;
block|}
end_class

end_unit
