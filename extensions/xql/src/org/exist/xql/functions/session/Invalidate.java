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
name|BasicFunction
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
name|Type
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Invalidate
extends|extends
name|BasicFunction
block|{
comment|//	private static final Logger logger = Logger.getLogger(Invalidate.class);
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
literal|"invalidate"
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
literal|"Invalidate (remove) the current HTTP session if present"
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|Invalidate
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
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
comment|//Always called as "invalidate") because the translation is made at compile time
if|if
condition|(
operator|!
name|isCalledAs
argument_list|(
literal|"invalidate"
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|SessionModule
operator|.
name|SESSION_VAR
operator|+
literal|" not set"
argument_list|)
throw|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|SessionModule
operator|.
name|SESSION_VAR
operator|+
literal|" is not bound to a Java object."
argument_list|)
throw|;
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
name|SessionWrapper
condition|)
block|{
name|SessionWrapper
name|session
init|=
operator|(
name|SessionWrapper
operator|)
name|value
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|session
operator|.
name|invalidate
argument_list|()
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|SessionModule
operator|.
name|SESSION_VAR
operator|+
literal|" is not bound to a session object"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

