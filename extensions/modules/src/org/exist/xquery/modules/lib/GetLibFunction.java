begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: RenderFunction.java 10610 2009-11-26 09:12:00Z shabanovd $  */
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
name|lib
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Base64Binary
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
comment|/**  * @author Evgeny Gazdovsky<gazdovsky@gmail.com>  */
end_comment

begin_class
specifier|public
class|class
name|GetLibFunction
extends|extends
name|LibFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-lib"
argument_list|,
name|LibModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LibModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Return a requested lib file from exist's libs"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"name of lib"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|GetLibFunction
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
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|String
name|name
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|File
name|lib
init|=
name|getLib
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|lib
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|lib
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|lib
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|Base64Binary
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

