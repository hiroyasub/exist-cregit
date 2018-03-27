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
name|validation
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|ErrorHandler
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
name|SAXParseException
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
comment|/**  * Report containing all validation info (errors, warnings).  *  * @author Dannes Wessels (dizzzz@exist-db.org)  *  * @see org.xml.sax.ErrorHandler  */
end_comment

begin_class
specifier|public
class|class
name|ValidationReport
implements|implements
name|ErrorHandler
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|ValidationReportItem
argument_list|>
name|validationReport
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|ValidationReportItem
name|lastItem
decl_stmt|;
specifier|private
name|long
name|duration
init|=
operator|-
literal|1L
decl_stmt|;
specifier|private
name|long
name|start
init|=
operator|-
literal|1L
decl_stmt|;
specifier|private
name|Throwable
name|throwed
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespaceUri
init|=
literal|null
decl_stmt|;
specifier|private
name|ValidationReportItem
name|createValidationReportItem
parameter_list|(
name|int
name|type
parameter_list|,
name|SAXParseException
name|exception
parameter_list|)
block|{
specifier|final
name|ValidationReportItem
name|vri
init|=
operator|new
name|ValidationReportItem
argument_list|()
decl_stmt|;
name|vri
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|vri
operator|.
name|setLineNumber
argument_list|(
name|exception
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
name|vri
operator|.
name|setColumnNumber
argument_list|(
name|exception
operator|.
name|getColumnNumber
argument_list|()
argument_list|)
expr_stmt|;
name|vri
operator|.
name|setMessage
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|vri
operator|.
name|setPublicId
argument_list|(
name|exception
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|vri
operator|.
name|setSystemId
argument_list|(
name|exception
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|vri
return|;
block|}
specifier|private
name|void
name|addItem
parameter_list|(
name|ValidationReportItem
name|newItem
parameter_list|)
block|{
if|if
condition|(
name|lastItem
operator|==
literal|null
condition|)
block|{
comment|// First reported item
name|validationReport
operator|.
name|add
argument_list|(
name|newItem
argument_list|)
expr_stmt|;
name|lastItem
operator|=
name|newItem
expr_stmt|;
block|}
if|else if
condition|(
name|lastItem
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|newItem
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
comment|// Message is repeated
name|lastItem
operator|.
name|increaseRepeat
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Received new message
name|validationReport
operator|.
name|add
argument_list|(
name|newItem
argument_list|)
expr_stmt|;
comment|// Swap reported item
name|lastItem
operator|=
name|newItem
expr_stmt|;
block|}
block|}
comment|/**      *  Receive notification of a recoverable error.      * @param exception The warning information encapsulated in a      *                      SAX parse exception.      * @throws SAXException Any SAX exception, possibly wrapping another      *                      exception.      */
specifier|public
name|void
name|error
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|addItem
argument_list|(
name|createValidationReportItem
argument_list|(
name|ValidationReportItem
operator|.
name|ERROR
argument_list|,
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Receive notification of a non-recoverable error.      *      * @param exception     The warning information encapsulated in a      *                      SAX parse exception.      * @throws SAXException Any SAX exception, possibly wrapping another      *                      exception.      */
specifier|public
name|void
name|fatalError
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|addItem
argument_list|(
name|createValidationReportItem
argument_list|(
name|ValidationReportItem
operator|.
name|FATAL
argument_list|,
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Receive notification of a warning.      *      * @param exception     The warning information encapsulated in a      *                      SAX parse exception.      * @throws SAXException Any SAX exception, possibly wrapping another      *                      exception.      */
specifier|public
name|void
name|warning
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|addItem
argument_list|(
name|createValidationReportItem
argument_list|(
name|ValidationReportItem
operator|.
name|WARNING
argument_list|,
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|this
operator|.
name|throwed
operator|=
name|ex
expr_stmt|;
block|}
comment|/**      *  Give validation information of the XML document.      *      * @return FALSE if no errors and warnings occurred.      */
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
operator|(
name|validationReport
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|throwed
operator|==
literal|null
operator|)
operator|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|ValidationReportItem
argument_list|>
name|getValidationReportItemList
parameter_list|()
block|{
return|return
name|validationReport
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getTextValidationReport
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|textReport
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|isValid
argument_list|()
condition|)
block|{
name|textReport
operator|.
name|add
argument_list|(
literal|"Document is valid."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|textReport
operator|.
name|add
argument_list|(
literal|"Document is not valid."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|throwed
operator|!=
literal|null
condition|)
block|{
name|textReport
operator|.
name|add
argument_list|(
literal|"Exception: "
operator|+
name|throwed
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|textReport
operator|.
name|addAll
argument_list|(
name|validationReport
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ValidationReportItem
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|textReport
operator|.
name|add
argument_list|(
literal|"Validated in "
operator|+
name|duration
operator|+
literal|" millisec."
argument_list|)
expr_stmt|;
return|return
name|textReport
return|;
block|}
specifier|public
name|String
index|[]
name|getValidationReportArray
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|vr
init|=
name|getTextValidationReport
argument_list|()
decl_stmt|;
specifier|final
name|String
name|report
index|[]
init|=
operator|new
name|String
index|[
name|vr
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|vr
operator|.
name|toArray
argument_list|(
name|report
argument_list|)
return|;
block|}
specifier|public
name|void
name|setValidationDuration
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|duration
operator|=
name|time
expr_stmt|;
block|}
specifier|public
name|long
name|getValidationDuration
parameter_list|()
block|{
return|return
name|duration
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|line
range|:
name|getTextValidationReport
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|getValidationDuration
argument_list|()
operator|==
operator|-
literal|1L
condition|)
block|{
comment|// not already stopped
name|long
name|stop
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|setValidationDuration
argument_list|(
name|stop
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setThrowable
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|throwed
operator|=
name|throwable
expr_stmt|;
block|}
specifier|public
name|Throwable
name|getThrowable
parameter_list|()
block|{
return|return
name|throwed
return|;
block|}
specifier|public
name|void
name|setNamespaceUri
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|namespaceUri
operator|=
name|namespace
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceUri
parameter_list|()
block|{
return|return
name|namespaceUri
return|;
block|}
specifier|public
name|String
name|getStackTrace
parameter_list|()
block|{
if|if
condition|(
name|throwed
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|PrintStream
name|ps
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|throwed
operator|.
name|printStackTrace
argument_list|(
name|ps
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toString
argument_list|(
name|UTF_8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

