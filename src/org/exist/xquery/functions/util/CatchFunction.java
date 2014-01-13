begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|FunctionReturnSequenceType
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
literal|"in $try-code-blocks will be put inside a try-catch statement. If an exception "
operator|+
literal|"is thrown while executing $try-code-blocks, the function checks the name of "
operator|+
literal|"the exception and calls $catch-code-blocks if it matches one of "
operator|+
literal|"the fully qualified Java class names specified in $java-classnames. "
operator|+
literal|"A value of \"*\" in $java-classnames will catch all java exceptions. "
operator|+
literal|"Inside the catch code block, the variable $util:exception will be bound to the "
operator|+
literal|"java class name of the exception, "
operator|+
literal|"and $util:exception-message will be bound to the message produced by the exception."
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
literal|"The list of one or more fully qualified Java class names.  An entry of '*' will catch all java exceptions."
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
literal|"The code blocks that will be put inside of a the try part of the try-catch statement."
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
literal|"The code blocks that will be will called if the catch matches one of the $java-classnames"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
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
argument_list|,
literal|"Use the XQuery 3.0 try/catch expression instead."
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
annotation|@
name|Override
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
comment|// Get exception classes
specifier|final
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
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
comment|// Try to evaluate try-code
try|try
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
specifier|final
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
comment|// Actually execute try-code
name|result
operator|=
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
expr_stmt|;
return|return
name|result
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
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|// Handle exception
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|expException
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Caught exception in util:catch: "
operator|+
name|expException
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|expException
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
name|expException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|expException
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
comment|// Iterate over all exception parameters
for|for
control|(
specifier|final
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
specifier|final
name|Item
name|currentItem
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Get value of execption argument
specifier|final
name|String
name|exClassName
init|=
name|currentItem
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|exClass
init|=
literal|null
decl_stmt|;
comment|// Get exception class, if available
if|if
condition|(
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|exClassName
argument_list|)
condition|)
block|{
name|exClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|currentItem
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// If value is '*' or if class actually matches
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|exClassName
argument_list|)
operator|||
name|exClass
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|expException
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
name|expException
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Calling exception handler to process "
operator|+
name|expException
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make exception name and message available to query
specifier|final
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
name|expException
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
name|expException
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Execute catch-code
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
specifier|final
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
name|expException
argument_list|)
throw|;
block|}
block|}
block|}
comment|// this type of exception is not caught: throw again
if|if
condition|(
name|expException
operator|instanceof
name|XPathException
condition|)
block|{
throw|throw
operator|(
name|XPathException
operator|)
name|expException
throw|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|expException
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

