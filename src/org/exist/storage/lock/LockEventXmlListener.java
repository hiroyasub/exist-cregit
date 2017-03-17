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
name|storage
operator|.
name|lock
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLOutputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamWriter
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * A lock event listener which formats events as XML and writes them to a file  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|LockEventXmlListener
implements|implements
name|LockTable
operator|.
name|LockEventListener
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|LockEventXmlListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|Path
name|xmlFile
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|prettyPrint
decl_stmt|;
specifier|private
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
specifier|private
name|XMLStreamWriter
name|xmlStreamWriter
init|=
literal|null
decl_stmt|;
specifier|public
name|LockEventXmlListener
parameter_list|(
specifier|final
name|Path
name|xmlFile
parameter_list|)
block|{
name|this
argument_list|(
name|xmlFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LockEventXmlListener
parameter_list|(
specifier|final
name|Path
name|xmlFile
parameter_list|,
specifier|final
name|boolean
name|prettyPrint
parameter_list|)
block|{
name|this
operator|.
name|xmlFile
operator|=
name|xmlFile
expr_stmt|;
name|this
operator|.
name|prettyPrint
operator|=
name|prettyPrint
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registered
parameter_list|()
block|{
name|this
operator|.
name|registered
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|this
operator|.
name|os
operator|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|xmlFile
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|APPEND
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|)
expr_stmt|;
specifier|final
name|XMLOutputFactory
name|xmlOutputFactory
init|=
name|XMLOutputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|=
name|xmlOutputFactory
operator|.
name|createXMLStreamWriter
argument_list|(
name|os
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|.
name|writeStartDocument
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
literal|"lockEvents"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregistered
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|xmlStreamWriter
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|xmlStreamWriter
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|os
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|registered
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRegistered
parameter_list|()
block|{
return|return
name|registered
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|LockTable
operator|.
name|LockAction
name|lockAction
parameter_list|)
block|{
if|if
condition|(
operator|!
name|registered
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|xmlStreamWriter
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
literal|"lockEvent"
argument_list|)
expr_stmt|;
name|writeLongElement
argument_list|(
literal|"timestamp"
argument_list|,
name|lockAction
operator|.
name|timestamp
argument_list|)
expr_stmt|;
name|writeStringElement
argument_list|(
literal|"action"
argument_list|,
name|lockAction
operator|.
name|action
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|writeLongElement
argument_list|(
literal|"groupId"
argument_list|,
name|lockAction
operator|.
name|groupId
argument_list|)
expr_stmt|;
name|writeStringElement
argument_list|(
literal|"id"
argument_list|,
name|lockAction
operator|.
name|id
argument_list|)
expr_stmt|;
name|writeStringElement
argument_list|(
literal|"thread"
argument_list|,
name|lockAction
operator|.
name|threadName
argument_list|)
expr_stmt|;
name|stackTraceToJson
argument_list|(
name|lockAction
operator|.
name|stackTrace
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
literal|"lock"
argument_list|)
expr_stmt|;
name|writeStringElement
argument_list|(
literal|"type"
argument_list|,
name|lockAction
operator|.
name|lockType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|writeStringElement
argument_list|(
literal|"mode"
argument_list|,
name|lockAction
operator|.
name|mode
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|writeIntElement
argument_list|(
literal|"holdCount"
argument_list|,
name|lockAction
operator|.
name|count
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|writeStringElement
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeCharacters
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|writeLongElement
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeCharacters
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|writeIntElement
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|value
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeCharacters
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stackTraceToJson
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|StackTraceElement
index|[]
name|stackTrace
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
literal|"trace"
argument_list|)
expr_stmt|;
if|if
condition|(
name|stackTrace
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|StackTraceElement
name|stackTraceElement
range|:
name|stackTrace
control|)
block|{
name|xmlStreamWriter
operator|.
name|writeStartElement
argument_list|(
literal|"frame"
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeAttribute
argument_list|(
literal|"methodName"
argument_list|,
name|stackTraceElement
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeAttribute
argument_list|(
literal|"className"
argument_list|,
name|stackTraceElement
operator|.
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeAttribute
argument_list|(
literal|"lineNumber"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|stackTraceElement
operator|.
name|getLineNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
block|}
name|xmlStreamWriter
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

