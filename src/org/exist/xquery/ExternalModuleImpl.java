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
comment|/**  * Default implementation of an {@link org.exist.xquery.ExternalModule}.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ExternalModuleImpl
implements|implements
name|ExternalModule
block|{
specifier|private
name|String
name|mNamespaceURI
decl_stmt|;
specifier|private
name|String
name|mPrefix
decl_stmt|;
specifier|private
name|TreeMap
name|mFunctionMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|mGlobalVariables
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|mStaticVariables
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|public
name|ExternalModuleImpl
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|mNamespaceURI
operator|=
name|namespaceURI
expr_stmt|;
name|mPrefix
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|UserDefinedFunction
name|getFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
operator|(
name|UserDefinedFunction
operator|)
name|mFunctionMap
operator|.
name|get
argument_list|(
name|qname
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.ExternalModule#declareFunction(org.exist.xquery.UserDefinedFunction) 	 */
specifier|public
name|void
name|declareFunction
parameter_list|(
name|UserDefinedFunction
name|func
parameter_list|)
block|{
name|mFunctionMap
operator|.
name|put
argument_list|(
name|func
operator|.
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|func
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|mNamespaceURI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|mPrefix
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#isInternalModule() 	 */
specifier|public
name|boolean
name|isInternalModule
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
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
name|mFunctionMap
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|mFunctionMap
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|signatures
index|[
name|j
index|]
operator|=
operator|(
operator|(
name|UserDefinedFunction
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getSignature
argument_list|()
expr_stmt|;
block|}
return|return
name|signatures
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getSignatureForFunction(org.exist.dom.QName) 	 */
specifier|public
name|FunctionSignature
name|getSignatureForFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|UserDefinedFunction
name|func
init|=
operator|(
name|UserDefinedFunction
operator|)
name|mFunctionMap
operator|.
name|get
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|func
operator|!=
literal|null
condition|)
return|return
name|func
operator|.
name|getSignature
argument_list|()
return|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#declareVariable(org.exist.dom.QName, java.lang.Object) 	 */
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
name|mStaticVariables
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
name|mStaticVariables
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
specifier|public
name|void
name|declareVariable
parameter_list|(
name|QName
name|qname
parameter_list|,
name|VariableDeclaration
name|decl
parameter_list|)
throws|throws
name|XPathException
block|{
name|mGlobalVariables
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|decl
argument_list|)
expr_stmt|;
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
name|VariableDeclaration
name|decl
init|=
operator|(
name|VariableDeclaration
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
name|decl
operator|!=
literal|null
condition|)
block|{
name|decl
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Variable
operator|)
name|mStaticVariables
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

