begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|BackingStoreException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|InvalidPreferencesFormatException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|Preferences
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

begin_class
specifier|public
class|class
name|FavouriteConnections
block|{
comment|/** Name of Preference node containing favourites */
specifier|private
specifier|static
specifier|final
name|String
name|FAVOURITES_NODE
init|=
name|Messages
operator|.
name|getString
argument_list|(
literal|"LoginPanel.1"
argument_list|)
decl_stmt|;
comment|//$NON-NLS-1$
specifier|public
specifier|static
name|void
name|store
parameter_list|(
specifier|final
name|List
argument_list|<
name|FavouriteConnection
argument_list|>
name|favourites
parameter_list|)
block|{
specifier|final
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|FavouriteConnections
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Clear connection node
name|Preferences
name|favouritesNode
init|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
decl_stmt|;
try|try
block|{
name|favouritesNode
operator|.
name|removeNode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BackingStoreException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Recreate connection node
name|favouritesNode
operator|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
expr_stmt|;
comment|// Write all favourites
for|for
control|(
specifier|final
name|FavouriteConnection
name|favourite
range|:
name|favourites
control|)
block|{
if|if
condition|(
name|favourites
operator|!=
literal|null
condition|)
block|{
comment|// Create node
specifier|final
name|Preferences
name|favouriteNode
init|=
name|favouritesNode
operator|.
name|node
argument_list|(
name|favourite
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Fill node
name|favouriteNode
operator|.
name|put
argument_list|(
name|FavouriteConnection
operator|.
name|USERNAME
argument_list|,
name|favourite
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
comment|//do NOT store passwords in plain-text in users preferences
comment|//favouriteNode.put(FavouriteConnection.PASSWORD, favourite.getPassword());
name|favouriteNode
operator|.
name|put
argument_list|(
name|FavouriteConnection
operator|.
name|PASSWORD
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|//TODO hash passwords before storing - need to implement server-side login with hashes
name|favouriteNode
operator|.
name|put
argument_list|(
name|FavouriteConnection
operator|.
name|URI
argument_list|,
name|favourite
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|FavouriteConnection
operator|.
name|CONFIGURATION
argument_list|,
name|favourite
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|FavouriteConnection
operator|.
name|SSL
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|favourite
operator|.
name|isSsl
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|FavouriteConnection
argument_list|>
name|load
parameter_list|()
block|{
specifier|final
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|FavouriteConnections
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Preferences
name|favouritesNode
init|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
decl_stmt|;
comment|// Get all favourites
name|String
name|favouriteNodeNames
index|[]
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|favouriteNodeNames
operator|=
name|favouritesNode
operator|.
name|childrenNames
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BackingStoreException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Copy for each connection data into Favourite array
specifier|final
name|List
argument_list|<
name|FavouriteConnection
argument_list|>
name|favourites
init|=
operator|new
name|ArrayList
argument_list|<
name|FavouriteConnection
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|favouriteNodeName
range|:
name|favouriteNodeNames
control|)
block|{
specifier|final
name|Preferences
name|node
init|=
name|favouritesNode
operator|.
name|node
argument_list|(
name|favouriteNodeName
argument_list|)
decl_stmt|;
specifier|final
name|FavouriteConnection
name|favourite
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|URI
argument_list|,
literal|""
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
operator|&&
operator|(
operator|!
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|CONFIGURATION
argument_list|,
literal|""
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
condition|)
block|{
comment|//backwards compatibility with old login favourites
if|if
condition|(
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|URI
argument_list|,
literal|""
argument_list|)
operator|.
name|equals
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
comment|//embedded
name|favourite
operator|=
name|getEmbeddedFavourite
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//remote
name|favourite
operator|=
name|getRemoteFavourite
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|URI
argument_list|,
literal|""
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
comment|//embedded
name|favourite
operator|=
name|getEmbeddedFavourite
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//remote
name|favourite
operator|=
name|getRemoteFavourite
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|favourites
operator|.
name|add
argument_list|(
name|favourite
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|favourites
argument_list|)
expr_stmt|;
return|return
name|favourites
return|;
block|}
specifier|private
specifier|static
name|FavouriteConnection
name|getRemoteFavourite
parameter_list|(
specifier|final
name|String
name|favouriteNodeName
parameter_list|,
specifier|final
name|Preferences
name|node
parameter_list|)
block|{
comment|//do NOT store passwords in plain-text in users preferences
comment|/* return new FavouriteConnection(             favouriteNodeName,             node.get(FavouriteConnection.USERNAME, ""),             node.get(FavouriteConnection.PASSWORD, ""),             node.get(FavouriteConnection.URI, ""),             Boolean.parseBoolean(node.get(FavouriteConnection.SSL, "FALSE"))         );*/
return|return
operator|new
name|FavouriteConnection
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|USERNAME
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|""
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|URI
argument_list|,
literal|""
argument_list|)
argument_list|,
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|SSL
argument_list|,
literal|"FALSE"
argument_list|)
argument_list|)
argument_list|)
return|;
comment|//TODO hash passwords before storing - need to implement server-side login with hashes
block|}
specifier|private
specifier|static
name|FavouriteConnection
name|getEmbeddedFavourite
parameter_list|(
specifier|final
name|String
name|favouriteNodeName
parameter_list|,
specifier|final
name|Preferences
name|node
parameter_list|)
block|{
comment|//do NOT store passwords in plain-text in users preferences
comment|/* return new FavouriteConnection(             favouriteNodeName,             node.get(FavouriteConnection.USERNAME, ""),             node.get(FavouriteConnection.PASSWORD, ""),             node.get(FavouriteConnection.CONFIGURATION, "")         ); */
return|return
operator|new
name|FavouriteConnection
argument_list|(
name|favouriteNodeName
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|USERNAME
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|""
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|FavouriteConnection
operator|.
name|CONFIGURATION
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
comment|//TODO hash passwords before storing - need to implement server-side login with hashes
block|}
specifier|public
specifier|static
name|void
name|importFromFile
parameter_list|(
specifier|final
name|File
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidPreferencesFormatException
block|{
specifier|final
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|FavouriteConnections
operator|.
name|class
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|prefs
operator|.
name|importPreferences
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|exportToFile
parameter_list|(
specifier|final
name|File
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|BackingStoreException
block|{
specifier|final
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|FavouriteConnections
operator|.
name|class
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|prefs
operator|.
name|exportSubtree
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

