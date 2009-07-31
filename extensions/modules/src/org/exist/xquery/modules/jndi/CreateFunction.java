begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension GetConnectionFunction  *  Copyright (C) 2008 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: GetConnectionFunction.java 4126 2006-09-18 21:20:17 +0000 (Mon, 18 Sep 2006) deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jndi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|BasicAttributes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

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
name|IntegerValue
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
comment|/**  * eXist JNDI Module Extension CreateFunction  *   * Create a JNDI Directory entry  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 2008-12-02  * @version 1.0  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|CreateFunction
extends|extends
name|BasicFunction
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
name|CreateFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DSML_NAMESPACE
init|=
literal|"http://www.dsml.org/DSML"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DSML_PREFIX
init|=
literal|"dsml"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"create"
argument_list|,
name|JNDIModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JNDIModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Create a JNDI Directory entry."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"directory-context"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the directory context handle from a jndi:get-dir-context() call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dn"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"attributes"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"entry attributes to be set in the"
operator|+
literal|" form<attributes><attribute name=\"\" value=\"\"/></attributes>. "
operator|+
literal|" You can also optionally specify ordered=\"true\" for an attribute."
argument_list|)
block|}
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
block|}
decl_stmt|;
comment|/** 	 * CreateFunction Constructor 	 *  	 * @param context 	The Context of the calling XQuery 	 */
specifier|public
name|CreateFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
comment|/** 	 * evaluate the call to the xquery create() function, it is really 	 * the main entry point of this class 	 *  	 * @param args				arguments from the get-connection() function call 	 * @param contextSequence 	the Context Sequence to operate on (not used here internally!) 	 * @return 					A xs:long representing a handle to the connection 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
comment|// Was context handle or DN specified?
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|!
operator|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|String
name|dn
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|ctxID
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|DirContext
name|ctx
init|=
operator|(
name|DirContext
operator|)
name|JNDIModule
operator|.
name|retrieveJNDIContext
argument_list|(
name|context
argument_list|,
name|ctxID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"jndi:create() - Invalid JNDI context handle provided: "
operator|+
name|ctxID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BasicAttributes
name|attributes
init|=
name|JNDIModule
operator|.
name|parseAttributes
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|attributes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ctx
operator|.
name|createSubcontext
argument_list|(
name|dn
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|createSubcontext
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"jndi:create() Create failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
argument_list|,
name|ne
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"jndi:create() Create failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
operator|+
name|ne
argument_list|)
operator|)
throw|;
block|}
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
end_class

end_unit

