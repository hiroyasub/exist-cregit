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
name|xquery
operator|.
name|functions
package|;
end_package

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
name|NAMESPACE_URI
init|=
literal|"http://www.w3.org/2003/05/xpath-functions"
decl_stmt|;
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
name|signature
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
name|signature
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
name|FunCurrentDate
operator|.
name|signature
argument_list|,
name|FunCurrentDate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCurrentDateTime
operator|.
name|signature
argument_list|,
name|FunCurrentDateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunCurrentTime
operator|.
name|signature
argument_list|,
name|FunCurrentTime
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
name|FunDeepEqual
operator|.
name|signature
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
name|signature
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
name|FunEndsWith
operator|.
name|signature
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
argument_list|,
name|FunError
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
name|FunFalse
operator|.
name|signature
argument_list|,
name|FunFalse
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
name|FunGetDayFromDate
operator|.
name|signature
argument_list|,
name|FunGetDayFromDate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetDaysFromDayTimeDuration
operator|.
name|signature
argument_list|,
name|FunGetDaysFromDayTimeDuration
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetHoursFromDayTimeDuration
operator|.
name|signature
argument_list|,
name|FunGetHoursFromDayTimeDuration
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetMinutesFromDayTimeDuration
operator|.
name|signature
argument_list|,
name|FunGetMinutesFromDayTimeDuration
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetMonthFromDate
operator|.
name|signature
argument_list|,
name|FunGetMonthFromDate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetSecondsFromDayTimeDuration
operator|.
name|signature
argument_list|,
name|FunGetSecondsFromDayTimeDuration
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGetYearFromDate
operator|.
name|signature
argument_list|,
name|FunGetYearFromDate
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
argument_list|,
name|FunId
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
name|FunLowerCase
operator|.
name|signature
argument_list|,
name|FunLowerCase
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
name|FunStartsWith
operator|.
name|signature
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
name|signature
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
name|signature
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
name|FunTrue
operator|.
name|signature
argument_list|,
name|FunTrue
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunUpperCase
operator|.
name|signature
argument_list|,
name|FunUpperCase
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
block|}
decl_stmt|;
specifier|public
name|ModuleImpl
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
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

