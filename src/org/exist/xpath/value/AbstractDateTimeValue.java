begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractDateTimeValue
extends|extends
name|ComputableValue
block|{
specifier|protected
name|GregorianCalendar
name|calendar
decl_stmt|;
specifier|protected
name|int
name|tzOffset
init|=
literal|0
decl_stmt|;
specifier|protected
name|boolean
name|explicitTimeZone
init|=
literal|false
decl_stmt|;
specifier|protected
name|Date
name|date
decl_stmt|;
specifier|public
name|AbstractDateTimeValue
parameter_list|()
block|{
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|()
expr_stmt|;
name|tzOffset
operator|=
operator|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|)
operator|+
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DST_OFFSET
argument_list|)
operator|)
operator|/
literal|60000
expr_stmt|;
name|date
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|compareTo
parameter_list|(
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|protected
name|void
name|formatString
parameter_list|(
name|StringBuffer
name|buf
parameter_list|,
name|int
name|value
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|String
name|s
init|=
literal|"000"
operator|+
name|value
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

