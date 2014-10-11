begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|conversion
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LongField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|RangeIndexConfigElement
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
name|DateValue
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
name|TimeUtils
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Simple normalization of dates: if there is only a year, transform it into a date: yyy-01-01.  * If full date is given, but with missing digits: fill them in.  */
end_comment

begin_class
specifier|public
class|class
name|DateConverter
implements|implements
name|TypeConverter
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DateConverter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Pattern
name|DATE_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+)-(\\d+)-(\\d+)"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Field
name|toField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|content
parameter_list|)
block|{
try|try
block|{
name|DateValue
name|dv
decl_stmt|;
if|if
condition|(
name|content
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// just year
name|int
name|year
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|XMLGregorianCalendar
name|calendar
init|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|setYear
argument_list|(
name|year
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setDay
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setMonth
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dv
operator|=
operator|new
name|DateValue
argument_list|(
name|calendar
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// try to handle missing digits as in 1980-8-4
name|Matcher
name|matcher
init|=
name|DATE_REGEX
operator|.
name|matcher
argument_list|(
name|content
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
block|{
name|content
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%04d-%02d-%02d"
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// invalid content: ignore
block|}
block|}
name|dv
operator|=
operator|new
name|DateValue
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|dl
init|=
name|RangeIndexConfigElement
operator|.
name|dateToLong
argument_list|(
name|dv
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongField
argument_list|(
name|fieldName
argument_list|,
name|dl
argument_list|,
name|LongField
operator|.
name|TYPE_NOT_STORED
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// wrong type: ignore
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid date format: "
operator|+
name|content
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// wrong type: ignore
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid date format: "
operator|+
name|content
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid date format: "
operator|+
name|content
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
