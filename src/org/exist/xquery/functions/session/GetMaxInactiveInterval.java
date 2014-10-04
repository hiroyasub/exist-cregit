begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  ixitar@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|session
package|;
end_package

begin_comment
comment|//import org.apache.log4j.Logger;
end_comment

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|http
operator|.
name|servlets
operator|.
name|SessionWrapper
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
name|Variable
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
name|XPathUtil
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
name|JavaObjectValue
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Returns an attribute stored in the current session or an empty sequence  * if the attribute does not exist.  *   * @author Loren Cahlander  */
end_comment

begin_class
specifier|public
class|class
name|GetMaxInactiveInterval
extends|extends
name|Function
block|{
comment|//	private static final Logger logger = Logger.getLogger(GetAttribute.class);
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
literal|"get-max-inactive-interval"
argument_list|,
name|SessionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SessionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the maximum time interval, in seconds, that the servlet container "
operator|+
literal|"will keep this session open between client accesses. After this interval, "
operator|+
literal|"the servlet container will invalidate the session. The maximum time interval "
operator|+
literal|"can be set with the session:set-max-inactive-interval function. A negative time indicates "
operator|+
literal|"the session should never timeout. "
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the maximum time interval, in seconds"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetMaxInactiveInterval
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
specifier|final
name|SessionModule
name|myModule
init|=
operator|(
name|SessionModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|SessionModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|// session object is read from global variable $session
specifier|final
name|Variable
name|var
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|SessionModule
operator|.
name|SESSION_VAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
operator|||
name|var
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// throw( new XPathException( this, "Session not set" ) );
return|return
operator|(
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|context
argument_list|)
operator|)
return|;
block|}
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
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Variable $session is not bound to a Java object."
argument_list|)
operator|)
throw|;
block|}
specifier|final
name|JavaObjectValue
name|session
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
name|session
operator|.
name|getObject
argument_list|()
operator|instanceof
name|SessionWrapper
condition|)
block|{
try|try
block|{
specifier|final
name|int
name|interval
init|=
operator|(
operator|(
name|SessionWrapper
operator|)
name|session
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|getMaxInactiveInterval
argument_list|()
decl_stmt|;
return|return
operator|(
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|interval
argument_list|)
argument_list|,
name|context
argument_list|)
operator|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalStateException
name|ise
parameter_list|)
block|{
return|return
operator|(
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|context
argument_list|)
operator|)
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Type error: variable $session is not bound to a session object"
argument_list|)
operator|)
throw|;
block|}
block|}
block|}
end_class

end_unit

