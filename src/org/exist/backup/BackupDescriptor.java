begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|ContentHandler
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
name|exist
operator|.
name|util
operator|.
name|EXistInputSource
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
name|IOException
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
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|BackupDescriptor
block|{
name|String
name|COLLECTION_DESCRIPTOR
init|=
literal|"__contents__.xml"
decl_stmt|;
name|String
name|BACKUP_PROPERTIES
init|=
literal|"backup.properties"
decl_stmt|;
name|String
name|PREVIOUS_PROP_NAME
init|=
literal|"previous"
decl_stmt|;
name|String
name|NUMBER_IN_SEQUENCE_PROP_NAME
init|=
literal|"nr-in-sequence"
decl_stmt|;
name|String
name|INCREMENTAL_PROP_NAME
init|=
literal|"incremental"
decl_stmt|;
name|String
name|DATE_PROP_NAME
init|=
literal|"date"
decl_stmt|;
name|EXistInputSource
name|getInputSource
parameter_list|()
function_decl|;
name|EXistInputSource
name|getInputSource
parameter_list|(
name|String
name|describedItem
parameter_list|)
function_decl|;
name|BackupDescriptor
name|getChildBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
function_decl|;
name|BackupDescriptor
name|getBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
function_decl|;
name|String
name|getName
parameter_list|()
function_decl|;
name|String
name|getSymbolicPath
parameter_list|()
function_decl|;
name|String
name|getSymbolicPath
parameter_list|(
name|String
name|describedItem
parameter_list|,
name|boolean
name|isChildDescriptor
parameter_list|)
function_decl|;
comment|/**      * Returns general properties of the backup, normally including the creation date or if it is an incremental backup.      *      * @return  a Properties object or null if no properties were found      *      * @throws  IOException  if there was an error in the properties file      */
name|Properties
name|getProperties
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|File
name|getParentDir
parameter_list|()
function_decl|;
name|Date
name|getDate
parameter_list|()
function_decl|;
name|boolean
name|before
parameter_list|(
name|long
name|timestamp
parameter_list|)
function_decl|;
name|void
name|parse
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
function_decl|;
block|}
end_interface

end_unit

