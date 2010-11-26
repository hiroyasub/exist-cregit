begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AnalyzeContextInfo
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
name|Expression
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
name|xslt
operator|.
name|XSLContext
import|;
end_import

begin_comment
comment|/**  * The xsl:attribute, xsl:comment, xsl:processing-instruction, xsl:namespace,   * and xsl:value-of elements create nodes that cannot have children.   * Specifically, the   * xsl:attribute instruction creates an attribute node,   * xsl:comment creates a comment node,   * xsl:processing-instruction creates a processing instruction node,   * xsl:namespace creates a namespace node,   * and xsl:value-of creates a text node.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SimpleConstructor
extends|extends
name|XSLPathExpr
block|{
specifier|protected
name|boolean
name|newDocumentContext
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|sequenceItSelf
init|=
literal|false
decl_stmt|;
specifier|public
name|SimpleConstructor
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|newDocumentContext
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_NODE_CONSTRUCTOR
operator|)
operator|==
literal|0
expr_stmt|;
name|sequenceItSelf
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|NON_STREAMABLE
operator|)
operator|==
literal|0
expr_stmt|;
comment|//        if (!newDocumentContext) {
comment|//        	for (Iterator<Expression> i = steps.iterator(); i.hasNext();) {
comment|//        		if (i.next() instanceof Text) {
comment|//					i.remove();
comment|//				}
comment|//        	}
comment|//        }
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

