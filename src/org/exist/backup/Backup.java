begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id:  */
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
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
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
name|Permission
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
name|UserManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_class
specifier|public
class|class
name|Backup
block|{
specifier|private
name|String
name|backupDir
decl_stmt|;
specifier|private
name|String
name|rootCollection
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|String
name|pass
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NS
init|=
literal|"http://exist.sourceforge.net/NS/exist"
decl_stmt|;
specifier|public
name|Backup
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pass
parameter_list|,
name|String
name|backupDir
parameter_list|,
name|String
name|rootCollection
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|pass
operator|=
name|pass
expr_stmt|;
name|this
operator|.
name|backupDir
operator|=
name|backupDir
expr_stmt|;
name|this
operator|.
name|rootCollection
operator|=
name|rootCollection
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"using root collection: "
operator|+
name|rootCollection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Backup
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pass
parameter_list|,
name|String
name|backupDir
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|pass
argument_list|,
name|backupDir
argument_list|,
literal|"xmldb:exist:///db"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|backup
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|Collection
name|current
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|rootCollection
argument_list|,
name|user
argument_list|,
name|pass
argument_list|)
decl_stmt|;
name|backup
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|backup
parameter_list|(
name|Collection
name|current
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|SAXException
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
return|return;
name|current
operator|.
name|setProperty
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// get resources and permissions
name|String
index|[]
name|resources
init|=
name|current
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|backupDir
operator|+
name|current
operator|.
name|getName
argument_list|()
decl_stmt|;
name|UserManagementService
name|mgtService
init|=
operator|(
name|UserManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Permission
name|perms
index|[]
init|=
name|mgtService
operator|.
name|listResourcePermissions
argument_list|()
decl_stmt|;
name|Permission
name|currentPerms
init|=
name|mgtService
operator|.
name|getPermissions
argument_list|(
name|current
argument_list|)
decl_stmt|;
comment|// create directory and open __contents__.xml
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|BufferedWriter
name|contents
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|path
operator|+
literal|'/'
operator|+
literal|"__contents__.xml"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|(
literal|"xml"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// serializer writes to __contents__.xml
name|XMLSerializer
name|serializer
init|=
operator|new
name|XMLSerializer
argument_list|(
name|contents
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|startPrefixMapping
argument_list|(
literal|""
argument_list|,
name|NS
argument_list|)
expr_stmt|;
comment|// write<collection> element
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|current
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"owner"
argument_list|,
literal|"owner"
argument_list|,
literal|"CDATA"
argument_list|,
name|currentPerms
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"group"
argument_list|,
literal|"group"
argument_list|,
literal|"CDATA"
argument_list|,
name|currentPerms
operator|.
name|getOwnerGroup
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"mode"
argument_list|,
literal|"mode"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toOctalString
argument_list|(
name|currentPerms
operator|.
name|getPermissions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|NS
argument_list|,
literal|"collection"
argument_list|,
literal|"collection"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
comment|// scan through resources
name|XMLResource
name|resource
decl_stmt|;
name|FileOutputStream
name|os
decl_stmt|;
name|BufferedWriter
name|writer
decl_stmt|;
name|XMLSerializer
name|contentSerializer
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|current
operator|.
name|getResource
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"writing "
operator|+
name|path
operator|+
literal|'/'
operator|+
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|resources
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
comment|// write resource to contentSerializer
name|contentSerializer
operator|=
operator|new
name|XMLSerializer
argument_list|(
name|writer
argument_list|,
name|format
argument_list|)
expr_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|contentSerializer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// store permissions
name|attr
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"owner"
argument_list|,
literal|"owner"
argument_list|,
literal|"CDATA"
argument_list|,
name|perms
index|[
name|i
index|]
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"group"
argument_list|,
literal|"group"
argument_list|,
literal|"CDATA"
argument_list|,
name|perms
index|[
name|i
index|]
operator|.
name|getOwnerGroup
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"mode"
argument_list|,
literal|"mode"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toOctalString
argument_list|(
name|perms
index|[
name|i
index|]
operator|.
name|getPermissions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|NS
argument_list|,
literal|"resource"
argument_list|,
literal|"resource"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endElement
argument_list|(
name|NS
argument_list|,
literal|"resource"
argument_list|,
literal|"resource"
argument_list|)
expr_stmt|;
block|}
comment|// write subcollections
name|String
index|[]
name|collections
init|=
name|current
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|current
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"db"
argument_list|)
operator|&&
name|collections
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"system"
argument_list|)
condition|)
continue|continue;
name|attr
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|collections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|NS
argument_list|,
literal|"subcollection"
argument_list|,
literal|"subcollection"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endElement
argument_list|(
name|NS
argument_list|,
literal|"subcollection"
argument_list|,
literal|"subcollection"
argument_list|)
expr_stmt|;
block|}
comment|// close<collection>
name|serializer
operator|.
name|endElement
argument_list|(
name|NS
argument_list|,
literal|"collection"
argument_list|,
literal|"collection"
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endPrefixMapping
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|contents
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// descend into subcollections
name|Collection
name|child
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|child
operator|=
name|current
operator|.
name|getChildCollection
argument_list|(
name|collections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|backup
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
try|try
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
literal|"admin"
argument_list|,
literal|null
argument_list|,
literal|"backup"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|backup
operator|.
name|backup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

