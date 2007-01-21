begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
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
name|AttrList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|performance
operator|.
name|actions
operator|.
name|Action
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
name|QName
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|Writer
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
name|BufferedOutputStream
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|TestResultWriter
block|{
specifier|private
specifier|static
specifier|final
name|QName
name|ROOT_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"test-result"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|ACTION_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"action"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|THREAD_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"thread"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|NAME_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|THREAD_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"thread"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|ELAPSED_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"elapsed"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|DESCRIPTION_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"description"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|ID_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|PARENT_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"parent"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|RESULT_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"result"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|GROUP_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"group"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
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
name|defaultProperties
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
block|}
specifier|private
name|SAXSerializer
name|serializer
decl_stmt|;
specifier|private
name|Writer
name|writer
decl_stmt|;
specifier|public
name|TestResultWriter
parameter_list|(
name|String
name|outFile
parameter_list|)
throws|throws
name|EXistException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|serializer
operator|=
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
name|defaultProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|AttrList
name|attribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|startElement
argument_list|(
name|ROOT_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"error while configuring test output file: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|report
parameter_list|(
name|Action
name|action
parameter_list|,
name|String
name|message
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
name|AttrList
name|attribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|THREAD_ATTRIB
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|NAME_ATTRIB
argument_list|,
name|action
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|ELAPSED_ATTRIB
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|elapsed
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|ID_ATTRIB
argument_list|,
name|action
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|action
operator|.
name|getParent
argument_list|()
operator|instanceof
name|ActionThread
operator|)
condition|)
name|attribs
operator|.
name|addAttribute
argument_list|(
name|PARENT_ATTRIB
argument_list|,
name|action
operator|.
name|getParent
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|)
name|attribs
operator|.
name|addAttribute
argument_list|(
name|DESCRIPTION_ATTRIB
argument_list|,
name|action
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|getLastResult
argument_list|()
operator|!=
literal|null
condition|)
name|attribs
operator|.
name|addAttribute
argument_list|(
name|RESULT_ATTRIB
argument_list|,
name|action
operator|.
name|getLastResult
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|startElement
argument_list|(
name|ACTION_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
name|serializer
operator|.
name|characters
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endElement
argument_list|(
name|ACTION_ELEMENT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
specifier|public
specifier|synchronized
name|void
name|threadStarted
parameter_list|(
name|ActionThread
name|thread
parameter_list|)
block|{
name|AttrList
name|attribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|NAME_ATTRIB
argument_list|,
name|thread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|startElement
argument_list|(
name|THREAD_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endElement
argument_list|(
name|ACTION_ELEMENT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
specifier|public
specifier|synchronized
name|void
name|groupStart
parameter_list|(
name|Group
name|group
parameter_list|)
block|{
name|AttrList
name|attribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|NAME_ATTRIB
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|startElement
argument_list|(
name|GROUP_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
specifier|public
specifier|synchronized
name|void
name|groupEnd
parameter_list|(
name|Group
name|group
parameter_list|)
block|{
try|try
block|{
name|serializer
operator|.
name|endElement
argument_list|(
name|GROUP_ELEMENT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|serializer
operator|.
name|endElement
argument_list|(
name|ROOT_ELEMENT
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

