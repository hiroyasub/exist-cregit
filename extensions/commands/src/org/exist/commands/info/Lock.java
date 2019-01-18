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
name|commands
operator|.
name|info
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

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
name|plugin
operator|.
name|command
operator|.
name|AbstractCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|command
operator|.
name|CommandException
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
name|BrokerPool
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
class|class
name|Lock
extends|extends
name|AbstractCommand
block|{
specifier|public
name|Lock
parameter_list|()
block|{
name|names
operator|=
operator|new
name|String
index|[]
block|{
literal|"lock"
block|,
literal|"l"
block|}
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.plugin.command.AbstractCommand#process(org.exist.xmldb.XmldbURI, java.lang.String[]) 	 */
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|XmldbURI
name|collectionURI
parameter_list|,
name|String
index|[]
name|commandData
parameter_list|)
throws|throws
name|CommandException
block|{
try|try
block|{
specifier|final
name|Database
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|getBroker
argument_list|()
init|)
block|{
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|collectionURI
argument_list|)
decl_stmt|;
name|out
argument_list|()
operator|.
name|println
argument_list|(
literal|"Collection lock:"
argument_list|)
expr_stmt|;
comment|//TODO:check where that method is
comment|//collection.getLock().debug(out());
if|if
condition|(
name|commandData
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
name|DocumentImpl
name|doc
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|commandData
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|err
argument_list|()
operator|.
name|println
argument_list|(
literal|"Resource '"
operator|+
name|commandData
index|[
literal|0
index|]
operator|+
literal|"' not found."
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
argument_list|()
operator|.
name|println
argument_list|(
literal|"Locked by "
operator|+
name|doc
operator|.
name|getUserLock
argument_list|()
argument_list|)
expr_stmt|;
name|out
argument_list|()
operator|.
name|println
argument_list|(
literal|"Lock token: "
operator|+
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLockToken
argument_list|()
argument_list|)
expr_stmt|;
name|out
argument_list|()
operator|.
name|println
argument_list|(
literal|"Update lock: "
argument_list|)
expr_stmt|;
comment|//TODO:check where that method is
comment|//doc.getUpdateLock().debug(out());
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommandException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

