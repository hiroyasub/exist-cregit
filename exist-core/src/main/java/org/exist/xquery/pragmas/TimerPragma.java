begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|pragmas
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
name|*
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
name|dom
operator|.
name|QName
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
name|util
operator|.
name|ExpressionDumper
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
name|Sequence
import|;
end_import

begin_class
specifier|public
class|class
name|TimerPragma
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|TIMER_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"timer"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
name|Logger
name|log
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|start
decl_stmt|;
specifier|private
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
specifier|public
name|TimerPragma
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|qname
argument_list|,
name|contents
argument_list|)
expr_stmt|;
if|if
condition|(
name|contents
operator|!=
literal|null
operator|&&
name|contents
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|String
name|options
index|[]
init|=
name|Option
operator|.
name|tokenize
argument_list|(
name|contents
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|option
range|:
name|options
control|)
block|{
specifier|final
name|String
name|param
index|[]
init|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid content found for pragma "
operator|+
name|TIMER_PRAGMA
operator|.
name|getStringValue
argument_list|()
operator|+
literal|": "
operator|+
name|contents
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"verbose"
operator|.
name|equals
argument_list|(
name|param
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|verbose
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|param
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"logger"
operator|.
name|equals
argument_list|(
name|param
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|log
operator|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|param
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|log
operator|==
literal|null
condition|)
block|{
name|log
operator|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|TimerPragma
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Elapsed: "
operator|+
name|elapsed
operator|+
literal|"ms. for expression:\n"
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Elapsed: "
operator|+
name|elapsed
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

