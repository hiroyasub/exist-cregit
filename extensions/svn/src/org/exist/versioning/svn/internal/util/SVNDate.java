begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html.  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|util
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|FieldPosition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNErrorManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|ISVNOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNErrorMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|util
operator|.
name|SVNDebugLog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|util
operator|.
name|SVNLogType
import|;
end_import

begin_comment
comment|/**  * @author TMate Software Ltd.  * @version 1.3  */
end_comment

begin_class
specifier|public
class|class
name|SVNDate
extends|extends
name|Date
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|4845L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SVNDate
name|NULL
init|=
operator|new
name|SVNDate
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Calendar
name|CALENDAR
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"en"
argument_list|,
literal|"US"
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|DateFormat
name|SVN_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|ISO8601_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS'000Z'"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|RFC1123_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, d MMM yyyy HH:mm:ss z"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|CUSTOM_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss Z (EE, d MMM yyyy)"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|HUMAN_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd' 'HH:mm:ss' 'ZZZZ' ('E', 'dd' 'MMM' 'yyyy')'"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|SHORT_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd' 'HH:mm:ss'Z'"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|CONSOLE_DIFF_DATE_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE' 'MMM' 'dd' 'HH:mm:ss' 'yyyy"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|CONSOLE_LONG_DATE_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MM' 'dd'  'yyyy"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|CONSOLE_SHORT_DATE_FORMAT
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MM' 'dd'  'HH:mm"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|DATE_SEPARATORS
init|=
block|{
literal|'-'
block|,
literal|'-'
block|,
literal|'T'
block|,
literal|':'
block|,
literal|':'
block|,
literal|'.'
block|,
literal|'Z'
block|}
decl_stmt|;
static|static
block|{
name|SVN_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|ISO8601_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|RFC1123_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|HUMAN_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|SHORT_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|CUSTOM_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|myMicroSeconds
decl_stmt|;
specifier|private
name|SVNDate
parameter_list|(
name|long
name|time
parameter_list|,
name|int
name|micro
parameter_list|)
block|{
name|super
argument_list|(
operator|(
literal|1000
operator|*
name|time
operator|+
name|micro
operator|)
operator|/
literal|1000
argument_list|)
expr_stmt|;
name|myMicroSeconds
operator|=
name|micro
operator|>=
literal|0
condition|?
name|micro
operator|%
literal|1000
else|:
literal|1000
operator|+
operator|(
name|micro
operator|%
literal|1000
operator|)
expr_stmt|;
block|}
specifier|public
name|String
name|format
parameter_list|()
block|{
name|StringBuffer
name|formatted
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|SVN_FORMAT
init|)
block|{
name|SVN_FORMAT
operator|.
name|format
argument_list|(
name|this
argument_list|,
name|formatted
argument_list|,
operator|new
name|FieldPosition
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|m1
init|=
name|myMicroSeconds
operator|%
literal|10
decl_stmt|;
name|int
name|m2
init|=
operator|(
name|myMicroSeconds
operator|/
literal|10
operator|)
operator|%
literal|10
decl_stmt|;
name|int
name|m3
init|=
operator|(
name|myMicroSeconds
operator|)
operator|/
literal|100
decl_stmt|;
name|formatted
operator|.
name|append
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|formatted
operator|.
name|append
argument_list|(
name|m2
argument_list|)
expr_stmt|;
name|formatted
operator|.
name|append
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|formatted
operator|.
name|append
argument_list|(
literal|'Z'
argument_list|)
expr_stmt|;
return|return
name|formatted
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|formatDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
return|return
name|formatDate
argument_list|(
name|date
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|formatDate
parameter_list|(
name|Date
name|date
parameter_list|,
name|boolean
name|formatZeroDate
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
operator|!
name|formatZeroDate
operator|&&
name|date
operator|.
name|getTime
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|date
operator|instanceof
name|SVNDate
condition|)
block|{
name|SVNDate
name|extendedDate
init|=
operator|(
name|SVNDate
operator|)
name|date
decl_stmt|;
return|return
name|extendedDate
operator|.
name|format
argument_list|()
return|;
block|}
synchronized|synchronized
init|(
name|ISO8601_FORMAT
init|)
block|{
return|return
name|ISO8601_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatRFC1123Date
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|RFC1123_FORMAT
init|)
block|{
return|return
name|RFC1123_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatHumanDate
parameter_list|(
name|Date
name|date
parameter_list|,
name|ISVNOptions
name|options
parameter_list|)
block|{
name|DateFormat
name|df
init|=
name|HUMAN_FORMAT
decl_stmt|;
if|if
condition|(
name|options
operator|!=
literal|null
operator|&&
name|options
operator|.
name|getKeywordDateFormat
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|df
operator|=
name|options
operator|.
name|getKeywordDateFormat
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|df
init|)
block|{
return|return
name|df
operator|.
name|format
argument_list|(
name|date
operator|!=
literal|null
condition|?
name|date
else|:
name|NULL
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatShortDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
synchronized|synchronized
init|(
name|SHORT_FORMAT
init|)
block|{
return|return
name|SHORT_FORMAT
operator|.
name|format
argument_list|(
name|date
operator|!=
literal|null
condition|?
name|date
else|:
name|NULL
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatCustomDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|CUSTOM_FORMAT
init|)
block|{
return|return
name|CUSTOM_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatConsoleDiffDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|CONSOLE_DIFF_DATE_FORMAT
init|)
block|{
return|return
name|CONSOLE_DIFF_DATE_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatConsoleLongDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|CONSOLE_LONG_DATE_FORMAT
init|)
block|{
return|return
name|CONSOLE_LONG_DATE_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|formatConsoleShortDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|CONSOLE_SHORT_DATE_FORMAT
init|)
block|{
return|return
name|CONSOLE_SHORT_DATE_FORMAT
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|SVNDate
name|parseDate
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
name|NULL
return|;
block|}
try|try
block|{
return|return
name|parseDatestamp
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|SVNDebugLog
operator|.
name|getDefaultLog
argument_list|()
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|DEFAULT
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
return|return
name|NULL
return|;
block|}
specifier|public
specifier|static
name|Date
name|parseDateString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|SVNException
block|{
try|try
block|{
return|return
name|parseDatestamp
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|svne
parameter_list|)
block|{
throw|throw
name|svne
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|BAD_DATE
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|th
argument_list|,
name|SVNLogType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
return|return
name|NULL
return|;
block|}
specifier|private
specifier|static
name|SVNDate
name|parseDatestamp
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|BAD_DATE
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|SVNLogType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
name|int
name|charIndex
init|=
literal|0
decl_stmt|;
name|int
name|startIndex
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|7
index|]
decl_stmt|;
name|int
name|microseconds
init|=
literal|0
decl_stmt|;
name|int
name|timeZoneInd
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|DATE_SEPARATORS
operator|.
name|length
operator|&&
name|charIndex
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|index
operator|>
literal|1
condition|)
block|{
name|timeZoneInd
operator|=
name|charIndex
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
literal|'+'
condition|)
block|{
name|timeZoneInd
operator|=
name|charIndex
expr_stmt|;
block|}
if|if
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
name|DATE_SEPARATORS
index|[
name|index
index|]
operator|||
operator|(
name|index
operator|==
literal|5
operator|&&
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
name|DATE_SEPARATORS
index|[
name|index
operator|+
literal|1
index|]
operator|)
condition|)
block|{
if|if
condition|(
name|index
operator|==
literal|5
operator|&&
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
name|DATE_SEPARATORS
index|[
name|index
operator|+
literal|1
index|]
condition|)
block|{
name|index
operator|++
expr_stmt|;
block|}
name|String
name|segment
init|=
name|str
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|charIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
index|[
name|index
index|]
operator|=
literal|0
expr_stmt|;
block|}
if|else if
condition|(
name|index
operator|+
literal|1
operator|<
name|DATE_SEPARATORS
operator|.
name|length
condition|)
block|{
name|result
index|[
name|index
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
index|[
name|index
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|3
argument_list|,
name|segment
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|segment
operator|.
name|length
argument_list|()
operator|>
literal|3
condition|)
block|{
name|microseconds
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|startIndex
operator|=
name|charIndex
operator|+
literal|1
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|charIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|<
name|DATE_SEPARATORS
operator|.
name|length
condition|)
block|{
name|String
name|segment
init|=
name|str
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
index|[
name|index
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|result
index|[
name|index
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|year
init|=
name|result
index|[
literal|0
index|]
decl_stmt|;
name|int
name|month
init|=
name|result
index|[
literal|1
index|]
decl_stmt|;
name|int
name|date
init|=
name|result
index|[
literal|2
index|]
decl_stmt|;
name|int
name|hour
init|=
name|result
index|[
literal|3
index|]
decl_stmt|;
name|int
name|min
init|=
name|result
index|[
literal|4
index|]
decl_stmt|;
name|int
name|sec
init|=
name|result
index|[
literal|5
index|]
decl_stmt|;
name|int
name|ms
init|=
name|result
index|[
literal|6
index|]
decl_stmt|;
name|String
name|timeZoneId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|timeZoneInd
operator|!=
operator|-
literal|1
operator|&&
name|timeZoneInd
operator|<
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|&&
name|str
operator|.
name|indexOf
argument_list|(
literal|'Z'
argument_list|)
operator|==
operator|-
literal|1
operator|&&
name|str
operator|.
name|indexOf
argument_list|(
literal|'z'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|timeZoneId
operator|=
literal|"GMT"
operator|+
name|str
operator|.
name|substring
argument_list|(
name|timeZoneInd
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|CALENDAR
init|)
block|{
name|CALENDAR
operator|.
name|clear
argument_list|()
expr_stmt|;
name|TimeZone
name|oldTimeZone
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|timeZoneId
operator|!=
literal|null
condition|)
block|{
name|oldTimeZone
operator|=
name|CALENDAR
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|CALENDAR
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|timeZoneId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|str
operator|.
name|indexOf
argument_list|(
literal|'Z'
argument_list|)
operator|==
operator|-
literal|1
operator|&&
name|str
operator|.
name|indexOf
argument_list|(
literal|'z'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|oldTimeZone
operator|=
name|CALENDAR
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|CALENDAR
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CALENDAR
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|date
argument_list|,
name|hour
argument_list|,
name|min
argument_list|,
name|sec
argument_list|)
expr_stmt|;
name|CALENDAR
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
name|ms
argument_list|)
expr_stmt|;
name|SVNDate
name|resultDate
init|=
operator|new
name|SVNDate
argument_list|(
name|CALENDAR
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|microseconds
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldTimeZone
operator|!=
literal|null
condition|)
block|{
name|CALENDAR
operator|.
name|setTimeZone
argument_list|(
name|oldTimeZone
argument_list|)
expr_stmt|;
block|}
return|return
name|resultDate
return|;
block|}
block|}
specifier|public
specifier|static
name|long
name|parseDateAsMilliseconds
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
name|int
name|charIndex
init|=
literal|0
decl_stmt|;
name|int
name|startIndex
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|7
index|]
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|DATE_SEPARATORS
operator|.
name|length
operator|&&
name|charIndex
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|charIndex
argument_list|)
operator|==
name|DATE_SEPARATORS
index|[
name|index
index|]
condition|)
block|{
name|String
name|segment
init|=
name|str
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|charIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
index|[
name|index
index|]
operator|=
literal|0
expr_stmt|;
block|}
if|else if
condition|(
name|index
operator|+
literal|1
operator|<
name|DATE_SEPARATORS
operator|.
name|length
condition|)
block|{
try|try
block|{
name|result
index|[
name|index
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
try|try
block|{
name|result
index|[
name|index
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|segment
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|3
argument_list|,
name|segment
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
name|startIndex
operator|=
name|charIndex
operator|+
literal|1
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|charIndex
operator|++
expr_stmt|;
block|}
name|int
name|year
init|=
name|result
index|[
literal|0
index|]
decl_stmt|;
name|int
name|month
init|=
name|result
index|[
literal|1
index|]
decl_stmt|;
name|int
name|date
init|=
name|result
index|[
literal|2
index|]
decl_stmt|;
name|int
name|hour
init|=
name|result
index|[
literal|3
index|]
decl_stmt|;
name|int
name|min
init|=
name|result
index|[
literal|4
index|]
decl_stmt|;
name|int
name|sec
init|=
name|result
index|[
literal|5
index|]
decl_stmt|;
name|int
name|ms
init|=
name|result
index|[
literal|6
index|]
decl_stmt|;
synchronized|synchronized
init|(
name|CALENDAR
init|)
block|{
name|CALENDAR
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CALENDAR
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|date
argument_list|,
name|hour
argument_list|,
name|min
argument_list|,
name|sec
argument_list|)
expr_stmt|;
name|CALENDAR
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
name|ms
argument_list|)
expr_stmt|;
return|return
name|CALENDAR
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|myMicroSeconds
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|SVNDate
condition|)
block|{
name|SVNDate
name|date
init|=
operator|(
name|SVNDate
operator|)
name|obj
decl_stmt|;
return|return
name|getTime
argument_list|()
operator|==
name|date
operator|.
name|getTime
argument_list|()
operator|&&
name|myMicroSeconds
operator|==
name|date
operator|.
name|myMicroSeconds
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
name|Date
name|when
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|when
argument_list|)
operator|&&
name|when
operator|instanceof
name|SVNDate
condition|)
block|{
return|return
name|myMicroSeconds
operator|<
operator|(
operator|(
name|SVNDate
operator|)
name|when
operator|)
operator|.
name|myMicroSeconds
return|;
block|}
return|return
name|super
operator|.
name|before
argument_list|(
name|when
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|after
parameter_list|(
name|Date
name|when
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|when
argument_list|)
operator|&&
name|when
operator|instanceof
name|SVNDate
condition|)
block|{
return|return
name|myMicroSeconds
operator|>
operator|(
operator|(
name|SVNDate
operator|)
name|when
operator|)
operator|.
name|myMicroSeconds
return|;
block|}
return|return
name|super
operator|.
name|after
argument_list|(
name|when
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Date
name|anotherDate
parameter_list|)
block|{
name|int
name|result
init|=
name|super
operator|.
name|compareTo
argument_list|(
name|anotherDate
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
operator|&&
name|anotherDate
operator|instanceof
name|SVNDate
condition|)
block|{
name|SVNDate
name|date
init|=
operator|(
name|SVNDate
operator|)
name|anotherDate
decl_stmt|;
return|return
operator|(
name|myMicroSeconds
operator|<
name|date
operator|.
name|myMicroSeconds
condition|?
operator|-
literal|1
else|:
operator|(
name|myMicroSeconds
operator|==
name|date
operator|.
name|myMicroSeconds
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|long
name|getTimeInMicros
parameter_list|()
block|{
return|return
literal|1000
operator|*
name|getTime
argument_list|()
operator|+
name|myMicroSeconds
return|;
block|}
block|}
end_class

end_unit

