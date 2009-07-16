begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
operator|.
name|util
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
name|Cardinality
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
name|FunctionSignature
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
name|LocalVariable
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
name|XQueryContext
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
name|FunctionParameterSequenceType
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
name|Item
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
name|SequenceIterator
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
name|SequenceType
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
name|StringValue
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
name|Type
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CatchFunction
extends|extends
name|Function
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|CatchFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"catch"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function corresponds to a try-catch statement in Java. The code block "
operator|+
literal|"in $b will be put inside a try-catch statement. If an exception is thrown while executing "
operator|+
literal|"$b, the function checks the name of the exception and calls $c if it matches one of "
operator|+
literal|"the fully qualified Java class names specified in $a.  A value of \"*\" in $a will catch all java exceptions"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"java-classnames"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"list of one or more fully qualified Java class names.  An entry of '*' will catch all java exceptions."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"try-code-blocks"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"These code blocks will be put inside of a the try part of the try-catch statement."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"catch-code-blocks"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"These code blocks will called if the catch matches one of the $java-classnames"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"results"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the results from the try-catch"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|CatchFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|Sequence
name|exceptionClasses
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Caught exception in util:catch: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|XPathException
operator|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//            context.popDocumentContext();
name|context
operator|.
name|getWatchDog
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|exceptionClasses
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|exClassName
init|=
name|next
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Class
name|exClass
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|exClassName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|exClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|exClassName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
operator|||
name|exClass
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|exClass
operator|.
name|isInstance
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Calling exception handler to process "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|UtilModule
name|myModule
init|=
operator|(
name|UtilModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
name|myModule
operator|.
name|declareVariable
argument_list|(
name|UtilModule
operator|.
name|EXCEPTION_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|myModule
operator|.
name|declareVariable
argument_list|(
name|UtilModule
operator|.
name|EXCEPTION_MESSAGE_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
if|if
condition|(
name|e2
operator|instanceof
name|XPathException
condition|)
block|{
throw|throw
operator|(
name|XPathException
operator|)
name|e2
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Error in exception handler: "
operator|+
name|e2
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// this type of exception is not caught: throw again
if|if
condition|(
name|e
operator|instanceof
name|XPathException
condition|)
throw|throw
operator|(
name|XPathException
operator|)
name|e
throw|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|returnsType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#getCardinality()      */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|getCardinality
argument_list|()
return|;
block|}
block|}
end_class

end_unit

