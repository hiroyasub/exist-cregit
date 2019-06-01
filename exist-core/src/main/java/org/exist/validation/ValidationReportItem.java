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

begin_class
specifier|public
class|class
name|ValidationReportItem
block|{
specifier|public
specifier|static
specifier|final
name|int
name|WARNING
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ERROR
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|FATAL
init|=
literal|4
decl_stmt|;
specifier|private
name|int
name|type
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|lineNumber
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|columnNumber
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|publicId
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|systemId
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|message
init|=
literal|""
decl_stmt|;
specifier|private
name|int
name|repeat
init|=
literal|1
decl_stmt|;
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setLineNumber
parameter_list|(
name|int
name|nr
parameter_list|)
block|{
name|this
operator|.
name|lineNumber
operator|=
name|nr
expr_stmt|;
block|}
specifier|public
name|int
name|getLineNumber
parameter_list|()
block|{
return|return
name|this
operator|.
name|lineNumber
return|;
block|}
specifier|public
name|void
name|setColumnNumber
parameter_list|(
name|int
name|nr
parameter_list|)
block|{
name|this
operator|.
name|columnNumber
operator|=
name|nr
expr_stmt|;
block|}
specifier|public
name|int
name|getColumnNumber
parameter_list|()
block|{
return|return
name|this
operator|.
name|columnNumber
return|;
block|}
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
specifier|public
name|void
name|setPublicId
parameter_list|(
name|String
name|publicId
parameter_list|)
block|{
name|this
operator|.
name|publicId
operator|=
name|publicId
expr_stmt|;
block|}
specifier|public
name|String
name|getPublicId
parameter_list|()
block|{
return|return
name|this
operator|.
name|publicId
return|;
block|}
specifier|public
name|void
name|setSystemId
parameter_list|(
name|String
name|systemId
parameter_list|)
block|{
name|this
operator|.
name|systemId
operator|=
name|systemId
expr_stmt|;
block|}
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
name|this
operator|.
name|systemId
return|;
block|}
specifier|public
name|String
name|getTypeText
parameter_list|()
block|{
name|String
name|reportType
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|WARNING
case|:
name|reportType
operator|=
literal|"Warning"
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|reportType
operator|=
literal|"Error"
expr_stmt|;
break|break;
case|case
name|FATAL
case|:
name|reportType
operator|=
literal|"Fatal"
expr_stmt|;
break|break;
default|default:
name|reportType
operator|=
literal|"Unknown Error type"
expr_stmt|;
break|break;
block|}
return|return
name|reportType
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|String
name|reportType
init|=
name|getTypeText
argument_list|()
decl_stmt|;
return|return
operator|(
name|reportType
operator|+
literal|" ("
operator|+
name|lineNumber
operator|+
literal|","
operator|+
name|columnNumber
operator|+
literal|") : "
operator|+
name|message
operator|)
return|;
block|}
specifier|public
name|void
name|increaseRepeat
parameter_list|()
block|{
name|repeat
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|getRepeat
parameter_list|()
block|{
return|return
name|repeat
return|;
block|}
block|}
end_class

end_unit
