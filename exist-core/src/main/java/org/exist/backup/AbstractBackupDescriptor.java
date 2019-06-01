begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|XMLReaderPool
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
name|XPathException
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
name|xml
operator|.
name|sax
operator|.
name|XMLReader
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractBackupDescriptor
implements|implements
name|BackupDescriptor
block|{
specifier|protected
name|long
name|numberOfFiles
init|=
literal|0
decl_stmt|;
specifier|protected
name|Date
name|date
decl_stmt|;
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Properties
name|properties
init|=
name|getProperties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|dateStr
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateStr
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DateTimeValue
name|dtv
init|=
operator|new
name|DateTimeValue
argument_list|(
name|dateStr
argument_list|)
decl_stmt|;
name|date
operator|=
name|dtv
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XPathException
name|e
parameter_list|)
block|{
block|}
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
comment|// catch unexpected issues by setting the backup time as early as possible
name|date
operator|=
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|date
operator|)
return|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
specifier|final
name|long
name|timestamp
parameter_list|)
block|{
return|return
operator|(
name|timestamp
operator|>
name|getDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
specifier|final
name|XMLReaderPool
name|parserPool
parameter_list|,
specifier|final
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|parserPool
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|getInputSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|parserPool
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNumberOfFiles
parameter_list|()
block|{
return|return
name|numberOfFiles
return|;
block|}
block|}
end_class

end_unit
