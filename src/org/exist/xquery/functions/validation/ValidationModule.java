begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|validation
package|;
end_package

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
name|AbstractInternalModule
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
name|FunctionDef
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

begin_comment
comment|/**  * Module function definitions for validation module.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|ValidationModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/validation"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"validation"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2005-11-17"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.0"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|GrammarTooling
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GrammarTooling
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GrammarTooling
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|GrammarTooling
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GrammarTooling
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|GrammarTooling
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxv
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Jaxv
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxv
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Jaxv
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jing
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Jing
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jing
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Jing
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxp
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Jaxp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxp
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Jaxp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxp
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Jaxp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxp
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|Jaxp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Jaxp
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|Jaxp
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Validation
operator|.
name|deprecated
index|[
literal|0
index|]
argument_list|,
name|Validation
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Validation
operator|.
name|deprecated
index|[
literal|1
index|]
argument_list|,
name|Validation
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Validation
operator|.
name|deprecated
index|[
literal|2
index|]
argument_list|,
name|Validation
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Validation
operator|.
name|deprecated
index|[
literal|3
index|]
argument_list|,
name|Validation
operator|.
name|class
argument_list|)
block|,     }
decl_stmt|;
comment|//    static {
comment|//        Arrays.sort(functions, new FunctionComparator());
comment|//    }
specifier|public
specifier|final
specifier|static
name|QName
name|EXCEPTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"exception"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|EXCEPTION_MESSAGE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"exception-message"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
name|ValidationModule
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
name|declareVariable
argument_list|(
name|EXCEPTION_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|declareVariable
argument_list|(
name|EXCEPTION_MESSAGE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.ValidationModule#getDescription()          */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for XML validation and grammars functions."
return|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.ValidationModule#getNamespaceURI()          */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.ValidationModule#getDefaultPrefix()          */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

