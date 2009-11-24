begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|Comparator
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
name|List
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
comment|/**  * Abstract base class for an {@link org.exist.xquery.InternalModule}.   * Functions are defined in an array of {@link org.exist.xquery.FunctionDef}, which  * is passed to the constructor. A single implementation class  * can be registered for more than one function signature, given that the signatures differ  * in name or the number of expected arguments. It is thus possible to implement  * similar XQuery functions in one single class.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author ljo  *  */
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
specifier|public
specifier|static
class|class
name|FunctionComparator
implements|implements
name|Comparator
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|FunctionDef
name|d1
init|=
operator|(
name|FunctionDef
operator|)
name|o1
decl_stmt|;
return|return
name|d1
operator|.
name|getSignature
argument_list|()
operator|.
name|getFunctionId
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|FunctionDef
operator|)
name|o2
operator|)
operator|.
name|getSignature
argument_list|()
operator|.
name|getFunctionId
argument_list|()
argument_list|)
return|;
block|}
block|}
empty_stmt|;
specifier|protected
name|FunctionDef
index|[]
name|mFunctions
decl_stmt|;
specifier|protected
name|boolean
name|ordered
init|=
literal|false
decl_stmt|;
specifier|protected
specifier|final
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|mGlobalVariables
init|=
operator|new
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
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
name|this
argument_list|(
name|functions
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractInternalModule
parameter_list|(
name|FunctionDef
index|[]
name|functions
parameter_list|,
name|boolean
name|functionsOrdered
parameter_list|)
block|{
name|this
operator|.
name|mFunctions
operator|=
name|functions
expr_stmt|;
name|this
operator|.
name|ordered
operator|=
name|functionsOrdered
expr_stmt|;
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
comment|/**      * Module description for the documentation.      * This is a short phrase describing the module, starting with:      * A module for.       */
specifier|public
specifier|abstract
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/**      * Module release version for the documentation. Since a module can live       * in trunk a long time before it is included in a release,      * this is neccessary for the documentation.      */
specifier|public
specifier|abstract
name|String
name|getReleaseVersion
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
argument_list|<
name|FunctionSignature
argument_list|>
name|getSignaturesForFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
name|signatures
init|=
operator|new
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
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
name|arity
parameter_list|)
block|{
name|FunctionId
name|id
init|=
operator|new
name|FunctionId
argument_list|(
name|qname
argument_list|,
name|arity
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordered
condition|)
block|{
return|return
name|binarySearch
argument_list|(
name|id
argument_list|)
return|;
block|}
else|else
block|{
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
if|if
condition|(
name|id
operator|.
name|compareTo
argument_list|(
name|mFunctions
index|[
name|i
index|]
operator|.
name|getSignature
argument_list|()
operator|.
name|getFunctionId
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return
name|mFunctions
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|FunctionDef
name|binarySearch
parameter_list|(
name|FunctionId
name|id
parameter_list|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|mFunctions
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|FunctionDef
name|midVal
init|=
name|mFunctions
index|[
name|mid
index|]
decl_stmt|;
name|int
name|cmp
init|=
name|midVal
operator|.
name|getSignature
argument_list|()
operator|.
name|getFunctionId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
if|else if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
return|return
name|midVal
return|;
comment|// key found
block|}
return|return
literal|null
return|;
comment|// key not found.
block|}
specifier|public
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|getFunctionsByName
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|funcs
init|=
operator|new
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|()
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
name|sig
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
name|sig
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
block|{
name|funcs
operator|.
name|add
argument_list|(
name|sig
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|funcs
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
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
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
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|Variable
name|var
parameter_list|)
block|{
name|mGlobalVariables
operator|.
name|put
argument_list|(
name|var
operator|.
name|getQName
argument_list|()
argument_list|,
name|var
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
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isVarDeclared
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

