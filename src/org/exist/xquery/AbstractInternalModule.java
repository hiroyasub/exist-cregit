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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * Abstract base class for an {@link org.exist.xquery.InternalModule}.   * The constructor expects an array of {@link org.exist.xquery.FunctionDef}.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractInternalModule
implements|implements
name|InternalModule
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractInternalModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|TreeMap
name|mFunctionMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|protected
name|FunctionDef
index|[]
name|mFunctions
decl_stmt|;
specifier|protected
name|TreeMap
name|mGlobalVariables
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|public
name|AbstractInternalModule
parameter_list|(
name|FunctionDef
index|[]
name|functions
parameter_list|)
block|{
name|mFunctions
operator|=
name|functions
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|functions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FunctionSignature
name|signature
init|=
name|functions
index|[
name|i
index|]
operator|.
name|getSignature
argument_list|()
decl_stmt|;
name|mFunctionMap
operator|.
name|put
argument_list|(
name|signature
operator|.
name|getFunctionId
argument_list|()
argument_list|,
name|functions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|AbstractInternalModule
parameter_list|()
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#isInternalModule() 	 */
specifier|public
name|boolean
name|isInternalModule
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
specifier|abstract
name|String
name|getNamespaceURI
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
specifier|public
specifier|abstract
name|String
name|getDefaultPrefix
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#listFunctions() 	 */
specifier|public
name|FunctionSignature
index|[]
name|listFunctions
parameter_list|()
block|{
name|FunctionSignature
name|signatures
index|[]
init|=
operator|new
name|FunctionSignature
index|[
name|mFunctions
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|signatures
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|signatures
index|[
name|i
index|]
operator|=
name|mFunctions
index|[
name|i
index|]
operator|.
name|getSignature
argument_list|()
expr_stmt|;
return|return
name|signatures
return|;
block|}
specifier|public
name|Iterator
name|getSignaturesForFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|ArrayList
name|signatures
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FunctionSignature
name|signature
init|=
name|mFunctions
index|[
name|i
index|]
operator|.
name|getSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|qname
argument_list|)
operator|==
literal|0
condition|)
name|signatures
operator|.
name|add
argument_list|(
name|signature
argument_list|)
expr_stmt|;
block|}
return|return
name|signatures
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getClassForFunction(org.exist.dom.QName) 	 */
specifier|public
name|FunctionDef
name|getFunctionDef
parameter_list|(
name|QName
name|qname
parameter_list|,
name|int
name|argCount
parameter_list|)
block|{
specifier|final
name|FunctionId
name|id
init|=
operator|new
name|FunctionId
argument_list|(
name|qname
argument_list|,
name|argCount
argument_list|)
decl_stmt|;
return|return
operator|(
name|FunctionDef
operator|)
name|mFunctionMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|QName
name|qname
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|val
init|=
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
operator|(
name|Variable
operator|)
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
condition|)
block|{
name|var
operator|=
operator|new
name|Variable
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|mGlobalVariables
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|var
argument_list|)
expr_stmt|;
block|}
name|var
operator|.
name|setValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|var
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#resolveVariable(org.exist.dom.QName) 	 */
specifier|public
name|Variable
name|resolveVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|(
name|Variable
operator|)
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
return|;
block|}
block|}
end_class

end_unit

