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
name|functions
operator|.
name|request
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|StaticContext
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
name|Variable
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|XPathUtil
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
name|xpath
operator|.
name|value
operator|.
name|JavaObjectValue
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
name|xpath
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
name|xpath
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
name|xpath
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
name|RequestParameter
extends|extends
name|Function
block|{
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
literal|"request-parameter"
argument_list|,
name|REQUEST_FUNCTION_NS
argument_list|,
literal|"request"
argument_list|)
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|RequestParameter
parameter_list|(
name|StaticContext
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Function#eval(org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
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
comment|// request object is read from global variable $request
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
literal|"request"
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|JAVA_OBJECT
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Variable $request is not bound to an Java object."
argument_list|)
throw|;
comment|// get parameters
name|String
name|param
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
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|defValue
init|=
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
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|JavaObjectValue
name|value
init|=
operator|(
name|JavaObjectValue
operator|)
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getObject
argument_list|()
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Request
condition|)
return|return
name|cocoonRequestParam
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Request
operator|)
name|value
operator|.
name|getObject
argument_list|()
argument_list|,
name|param
argument_list|,
name|defValue
argument_list|)
return|;
if|else if
condition|(
name|value
operator|.
name|getObject
argument_list|()
operator|instanceof
name|HttpServletRequest
condition|)
return|return
name|httpRequestParam
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|value
operator|.
name|getObject
argument_list|()
argument_list|,
name|param
argument_list|,
name|defValue
argument_list|)
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Variable $request is not bound to a Request object."
argument_list|)
throw|;
block|}
specifier|public
name|Sequence
name|cocoonRequestParam
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Request
name|request
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|defValue
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|values
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
operator|||
name|values
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|new
name|StringValue
argument_list|(
name|defValue
argument_list|)
return|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
return|;
else|else
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|httpRequestParam
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|defValue
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|values
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
operator|||
name|values
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|new
name|StringValue
argument_list|(
name|defValue
argument_list|)
return|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
return|;
else|else
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
end_class

end_unit

