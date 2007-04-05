begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
package|;
end_package

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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|Function
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
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ModuleImpl
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|""
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|FunAbs
operator|.
name|signature
argument_list|,
name|FunAbs
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAvg
operator|.
name|signature
argument_list|,
name|FunAvg
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunBaseURI
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunBaseURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunBaseURI
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunBaseURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunBaseURI
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FunBaseURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunBoolean
operator|.
name|signature
argument_list|,
name|FunBoolean
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCeiling
operator|.
name|signature
argument_list|,
name|FunCeiling
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCodepointEqual
operator|.
name|signature
argument_list|,
name|FunCodepointEqual
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCodepointsToString
operator|.
name|signature
argument_list|,
name|FunCodepointsToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCompare
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunCompare
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCompare
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunCompare
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunConcat
operator|.
name|signature
argument_list|,
name|FunConcat
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunContains
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunContains
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunContains
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunContains
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCount
operator|.
name|signature
argument_list|,
name|FunCount
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCurrentDateTime
operator|.
name|fnCurrentDate
argument_list|,
name|FunCurrentDateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCurrentDateTime
operator|.
name|fnCurrentDateTime
argument_list|,
name|FunCurrentDateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCurrentDateTime
operator|.
name|fnCurrentTime
argument_list|,
name|FunCurrentDateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunData
operator|.
name|signature
argument_list|,
name|FunData
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDateTime
operator|.
name|signature
argument_list|,
name|FunDateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDeepEqual
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunDeepEqual
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDeepEqual
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunDeepEqual
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDistinctValues
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunDistinctValues
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDistinctValues
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunDistinctValues
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDoc
operator|.
name|signature
argument_list|,
name|FunDoc
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDocAvailable
operator|.
name|signature
argument_list|,
name|FunDocAvailable
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDocumentURI
operator|.
name|signature
argument_list|,
name|FunDocumentURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEmpty
operator|.
name|signature
argument_list|,
name|FunEmpty
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEncodeForURI
operator|.
name|signature
argument_list|,
name|FunEncodeForURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEndsWith
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunEndsWith
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEndsWith
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunEndsWith
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunError
operator|.
name|signature
index|[
literal|0
index|]
argument_list|,
name|FunError
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunError
operator|.
name|signature
index|[
literal|1
index|]
argument_list|,
name|FunError
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunError
operator|.
name|signature
index|[
literal|2
index|]
argument_list|,
name|FunError
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunError
operator|.
name|signature
index|[
literal|3
index|]
argument_list|,
name|FunError
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEscapeHTMLURI
operator|.
name|signature
argument_list|,
name|FunEscapeHTMLURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunEscapeURI
operator|.
name|signature
argument_list|,
name|FunEscapeURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunExactlyOne
operator|.
name|signature
argument_list|,
name|FunExactlyOne
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunExists
operator|.
name|signature
argument_list|,
name|FunExists
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunFloor
operator|.
name|signature
argument_list|,
name|FunFloor
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnDayFromDate
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnMonthFromDate
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnYearFromDate
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnTimezoneFromDate
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnHoursFromTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnMinutesFromTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnSecondsFromTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnTimezoneFromTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnDayFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnMonthFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnYearFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnHoursFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnMinutesFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnSecondsFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDateComponent
operator|.
name|fnTimezoneFromDateTime
argument_list|,
name|FunGetDateComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnYearsFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnMonthsFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnDaysFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnHoursFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnMinutesFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDurationComponent
operator|.
name|fnSecondsFromDuration
argument_list|,
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustDateToTimezone
index|[
literal|0
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustDateToTimezone
index|[
literal|1
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustTimeToTimezone
index|[
literal|0
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustTimeToTimezone
index|[
literal|1
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustDateTimeToTimezone
index|[
literal|0
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunAdjustTimezone
operator|.
name|fnAdjustDateTimeToTimezone
index|[
literal|1
index|]
argument_list|,
name|FunAdjustTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunId
operator|.
name|signature
index|[
literal|0
index|]
argument_list|,
name|FunId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunId
operator|.
name|signature
index|[
literal|1
index|]
argument_list|,
name|FunId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunImplicitTimezone
operator|.
name|signature
argument_list|,
name|FunImplicitTimezone
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunIndexOf
operator|.
name|fnIndexOf
index|[
literal|0
index|]
argument_list|,
name|FunIndexOf
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunIndexOf
operator|.
name|fnIndexOf
index|[
literal|1
index|]
argument_list|,
name|FunIndexOf
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunIRIToURI
operator|.
name|signature
argument_list|,
name|FunIRIToURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunItemAt
operator|.
name|signature
argument_list|,
name|FunItemAt
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunInScopePrefixes
operator|.
name|signature
argument_list|,
name|FunInScopePrefixes
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunInsertBefore
operator|.
name|signature
argument_list|,
name|FunInsertBefore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunLang
operator|.
name|signature
argument_list|,
name|FunLang
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunLast
operator|.
name|signature
argument_list|,
name|FunLast
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunLocalName
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunLocalName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunLocalName
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunLocalName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMatches
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunMatches
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMatches
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunMatches
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMax
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunMax
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMax
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunMax
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMin
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunMin
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunMin
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunMin
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNodeName
operator|.
name|signature
argument_list|,
name|FunNodeName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunName
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunName
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNamespaceURI
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunNamespaceURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNamespaceURI
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunNamespaceURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNamespaceURIForPrefix
operator|.
name|signature
argument_list|,
name|FunNamespaceURIForPrefix
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNormalizeSpace
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunNormalizeSpace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNormalizeSpace
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunNormalizeSpace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNormalizeUnicode
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunNormalizeUnicode
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNormalizeUnicode
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunNormalizeUnicode
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNot
operator|.
name|signature
argument_list|,
name|FunNot
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNumber
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunNumber
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunNumber
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunNumber
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunOneOrMore
operator|.
name|signature
argument_list|,
name|FunOneOrMore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunPosition
operator|.
name|signature
argument_list|,
name|FunPosition
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunQName
operator|.
name|signature
argument_list|,
name|FunQName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRemove
operator|.
name|signature
argument_list|,
name|FunRemove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunReplace
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunReplace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunReplace
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunReplace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunReverse
operator|.
name|signature
argument_list|,
name|FunReverse
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunResolveURI
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunResolveURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunResolveURI
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunResolveURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRoot
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunRoot
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRoot
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunRoot
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRound
operator|.
name|signature
argument_list|,
name|FunRound
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRoundHalfToEven
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunRoundHalfToEven
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunRoundHalfToEven
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunRoundHalfToEven
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStartsWith
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunStartsWith
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStartsWith
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunStartsWith
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunString
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunString
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStringJoin
operator|.
name|signature
argument_list|,
name|FunStringJoin
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStringPad
operator|.
name|signature
argument_list|,
name|FunStringPad
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStringToCodepoints
operator|.
name|signature
argument_list|,
name|FunStringToCodepoints
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStrLength
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunStrLength
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunStrLength
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunStrLength
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubSequence
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSubSequence
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubSequence
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSubSequence
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstring
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSubstring
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstring
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSubstring
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstringAfter
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSubstringAfter
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstringAfter
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSubstringAfter
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstringBefore
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSubstringBefore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSubstringBefore
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSubstringBefore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSum
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSum
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSum
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSum
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTokenize
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunTokenize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTokenize
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunTokenize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTrace
operator|.
name|signature
argument_list|,
name|FunTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTranslate
operator|.
name|signature
argument_list|,
name|FunTranslate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTrueOrFalse
operator|.
name|fnTrue
argument_list|,
name|FunTrueOrFalse
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunTrueOrFalse
operator|.
name|fnFalse
argument_list|,
name|FunTrueOrFalse
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunUpperOrLowerCase
operator|.
name|fnLowerCase
argument_list|,
name|FunUpperOrLowerCase
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunUpperOrLowerCase
operator|.
name|fnUpperCase
argument_list|,
name|FunUpperOrLowerCase
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunZeroOrOne
operator|.
name|signature
argument_list|,
name|FunZeroOrOne
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunUnordered
operator|.
name|signature
argument_list|,
name|FunUnordered
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtCollection
operator|.
name|signature
argument_list|,
name|ExtCollection
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtXCollection
operator|.
name|signature
argument_list|,
name|ExtXCollection
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtDoctype
operator|.
name|signature
argument_list|,
name|ExtDoctype
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtDocument
operator|.
name|signature
argument_list|,
name|ExtDocument
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtRegexp
operator|.
name|signature
argument_list|,
name|ExtRegexp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExtRegexpOr
operator|.
name|signature
argument_list|,
name|ExtRegexpOr
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QNameFunctions
operator|.
name|localNameFromQName
argument_list|,
name|QNameFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QNameFunctions
operator|.
name|prefixFromQName
argument_list|,
name|QNameFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QNameFunctions
operator|.
name|namespaceURIFromQName
argument_list|,
name|QNameFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunResolveQName
operator|.
name|signature
argument_list|,
name|FunResolveQName
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|functions
argument_list|,
operator|new
name|FunctionComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ModuleImpl
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDescription() 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"XQuery/XPath Core Library Functions"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
block|}
end_class

end_unit

