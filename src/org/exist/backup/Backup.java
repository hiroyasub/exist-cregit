begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|Resource
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|storage
operator|.
name|NativeBroker
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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
name|CollectionImpl
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
name|EXistResource
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
name|ExtendedResource
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|util
operator|.
name|URIUtils
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
name|DateTimeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|*
import|;
end_import

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
name|IOException
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_class
specifier|public
class|class
name|Backup
block|{
specifier|private
specifier|static
specifier|final
name|int
name|currVersion
init|=
literal|1
decl_stmt|;
specifier|private
name|String
name|target
decl_stmt|;
specifier|private
name|XmldbURI
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
name|Properties
name|defaultOutputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|public
name|Properties
name|contentsOutputProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
block|{
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
block|{
name|contentsOutputProps
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
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
name|target
parameter_list|,
name|XmldbURI
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
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|rootCollection
operator|=
name|rootCollection
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
name|target
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|pass
argument_list|,
name|target
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
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
name|target
parameter_list|,
name|XmldbURI
name|rootCollection
parameter_list|,
name|Properties
name|property
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|pass
argument_list|,
name|target
argument_list|,
name|rootCollection
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultOutputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
name|property
operator|.
name|getProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"no"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|encode
parameter_list|(
name|String
name|enco
parameter_list|)
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
name|t
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|enco
operator|.
name|length
argument_list|()
condition|;
name|y
operator|++
control|)
block|{
name|t
operator|=
name|enco
operator|.
name|charAt
argument_list|(
name|y
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|==
literal|'"'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&22;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'&'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&26;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'*'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&2A;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|':'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&3A;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'<'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&3C;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'>'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&3E;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'?'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&3F;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'\\'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&5C;"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|==
literal|'|'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"&7C;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|out
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|static
name|String
name|decode
parameter_list|(
name|String
name|enco
parameter_list|)
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|temp
init|=
literal|""
decl_stmt|;
name|char
name|t
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|enco
operator|.
name|length
argument_list|()
condition|;
name|y
operator|++
control|)
block|{
name|t
operator|=
name|enco
operator|.
name|charAt
argument_list|(
name|y
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|!=
literal|'&'
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temp
operator|=
name|enco
operator|.
name|substring
argument_list|(
name|y
argument_list|,
name|y
operator|+
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&22;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&26;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&2A;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&3A;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&3C;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&3E;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&3F;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&5C;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"&7C;"
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
block|}
name|y
operator|=
name|y
operator|+
literal|3
expr_stmt|;
block|}
block|}
return|return
operator|(
name|out
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
specifier|public
name|void
name|backup
parameter_list|(
name|boolean
name|guiMode
parameter_list|,
name|JFrame
name|parent
parameter_list|)
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
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|,
name|pass
argument_list|)
decl_stmt|;
if|if
condition|(
name|guiMode
condition|)
block|{
name|BackupDialog
name|dialog
init|=
operator|new
name|BackupDialog
argument_list|(
name|parent
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|dialog
operator|.
name|setSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|350
argument_list|,
literal|150
argument_list|)
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BackupThread
name|thread
init|=
operator|new
name|BackupThread
argument_list|(
name|current
argument_list|,
name|dialog
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
comment|// if backup runs as a single dialog, wait for it (or app will terminate)
while|while
condition|(
name|thread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|backup
argument_list|(
name|current
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|backup
parameter_list|(
name|Collection
name|current
parameter_list|,
name|BackupDialog
name|dialog
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|String
name|cname
init|=
name|current
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
name|cname
operator|=
literal|"/"
operator|+
name|cname
expr_stmt|;
block|}
name|String
name|path
init|=
name|target
operator|+
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|cname
argument_list|)
argument_list|)
decl_stmt|;
name|BackupWriter
name|output
decl_stmt|;
if|if
condition|(
name|target
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|output
operator|=
operator|new
name|ZipWriter
argument_list|(
name|target
argument_list|,
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|cname
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|=
operator|new
name|FileSystemWriter
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|backup
argument_list|(
name|current
argument_list|,
name|output
argument_list|,
name|dialog
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|backup
parameter_list|(
name|Collection
name|current
parameter_list|,
name|BackupWriter
name|output
parameter_list|,
name|BackupDialog
name|dialog
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
block|{
return|return;
block|}
name|current
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
name|defaultOutputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
name|defaultOutputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
name|defaultOutputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
name|defaultOutputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|)
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
name|Arrays
operator|.
name|sort
argument_list|(
name|resources
argument_list|)
expr_stmt|;
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
index|[]
name|perms
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
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
block|{
name|dialog
operator|.
name|setCollection
argument_list|(
name|current
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|setResourceCount
argument_list|(
name|resources
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|Writer
name|contents
init|=
name|output
operator|.
name|newContents
argument_list|()
decl_stmt|;
comment|// serializer writes to __contents__.xml
name|SAXSerializer
name|serializer
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
name|contents
argument_list|,
name|contentsOutputProps
argument_list|)
expr_stmt|;
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|)
expr_stmt|;
comment|// write<collection> element
name|CollectionImpl
name|cur
init|=
operator|(
name|CollectionImpl
operator|)
name|current
decl_stmt|;
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
comment|//The name should have come from an XmldbURI.toString() call
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|Namespaces
operator|.
name|EXIST_NS
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
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"created"
argument_list|,
literal|"created"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
operator|new
name|DateTimeValue
argument_list|(
name|cur
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"version"
argument_list|,
literal|"version"
argument_list|,
literal|"CDATA"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|currVersion
argument_list|)
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"collection"
argument_list|,
literal|"collection"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
comment|// scan through resources
name|Resource
name|resource
decl_stmt|;
name|OutputStream
name|os
decl_stmt|;
name|BufferedWriter
name|writer
decl_stmt|;
name|SAXSerializer
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
try|try
block|{
if|if
condition|(
name|resources
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"__contents__.xml"
argument_list|)
condition|)
block|{
comment|//Skipping resources[i]
continue|continue;
block|}
name|resource
operator|=
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
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
block|{
name|dialog
operator|.
name|setResource
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|setProgress
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|os
operator|=
name|output
operator|.
name|newEntry
argument_list|(
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|instanceof
name|ExtendedResource
condition|)
block|{
operator|(
operator|(
name|ExtendedResource
operator|)
name|resource
operator|)
operator|.
name|getContentIntoAStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|writer
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
comment|// write resource to contentSerializer
name|contentSerializer
operator|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
expr_stmt|;
name|contentSerializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|defaultOutputProperties
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|setLexicalHandler
argument_list|(
name|contentSerializer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|XMLResource
operator|)
name|resource
operator|)
operator|.
name|getContentAsSAX
argument_list|(
name|contentSerializer
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|contentSerializer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An exception occurred while writing the resource: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
name|output
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|EXistResource
name|ris
init|=
operator|(
name|EXistResource
operator|)
name|resource
decl_stmt|;
comment|//store permissions
name|attr
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
name|resource
operator|.
name|getResourceType
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|Namespaces
operator|.
name|EXIST_NS
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
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|Date
name|date
init|=
name|ris
operator|.
name|getCreationTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"created"
argument_list|,
literal|"created"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
operator|new
name|DateTimeValue
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|date
operator|=
name|ris
operator|.
name|getLastModificationTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"modified"
argument_list|,
literal|"modified"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
operator|new
name|DateTimeValue
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"filename"
argument_list|,
literal|"filename"
argument_list|,
literal|"CDATA"
argument_list|,
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"mimetype"
argument_list|,
literal|"mimetype"
argument_list|,
literal|"CDATA"
argument_list|,
name|encode
argument_list|(
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|getMimeType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|resource
operator|.
name|getResourceType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"BinaryResource"
argument_list|)
condition|)
block|{
if|if
condition|(
name|ris
operator|.
name|getDocType
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"namedoctype"
argument_list|,
literal|"namedoctype"
argument_list|,
literal|"CDATA"
argument_list|,
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"publicid"
argument_list|,
literal|"publicid"
argument_list|,
literal|"CDATA"
argument_list|,
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getSystemId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"systemid"
argument_list|,
literal|"systemid"
argument_list|,
literal|"CDATA"
argument_list|,
name|ris
operator|.
name|getDocType
argument_list|()
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|serializer
operator|.
name|startElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"resource"
argument_list|,
literal|"resource"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to backup resource "
operator|+
name|resources
index|[
name|i
index|]
operator|+
literal|" from collection "
operator|+
name|current
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|NativeBroker
operator|.
name|SYSTEM_COLLECTION
argument_list|)
operator|&&
name|collections
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"temp"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|attr
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|attr
operator|.
name|addAttribute
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"filename"
argument_list|,
literal|"filename"
argument_list|,
literal|"CDATA"
argument_list|,
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|collections
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
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
name|Namespaces
operator|.
name|EXIST_NS
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
name|Namespaces
operator|.
name|EXIST_NS
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
name|output
operator|.
name|closeContents
argument_list|()
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|serializer
argument_list|)
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
if|if
condition|(
name|child
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|NativeBroker
operator|.
name|TEMP_COLLECTION
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|output
operator|.
name|newCollection
argument_list|(
name|encode
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|collections
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|backup
argument_list|(
name|child
argument_list|,
name|output
argument_list|,
name|dialog
argument_list|)
expr_stmt|;
name|output
operator|.
name|closeCollection
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
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
name|URIUtils
operator|.
name|encodeXmldbUriFor
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|backup
operator|.
name|backup
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
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
class|class
name|BackupThread
extends|extends
name|Thread
block|{
name|Collection
name|collection_
decl_stmt|;
name|BackupDialog
name|dialog_
decl_stmt|;
specifier|public
name|BackupThread
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|BackupDialog
name|dialog
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|collection_
operator|=
name|collection
expr_stmt|;
name|dialog_
operator|=
name|dialog
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|backup
argument_list|(
name|collection_
argument_list|,
name|dialog_
argument_list|)
expr_stmt|;
name|dialog_
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
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
block|}
end_class

end_unit

