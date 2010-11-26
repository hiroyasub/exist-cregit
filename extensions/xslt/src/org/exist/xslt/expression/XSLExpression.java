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
name|org
operator|.
name|exist
operator|.
name|interpreter
operator|.
name|ContextAtExist
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
name|ErrorCodes
operator|.
name|ErrorCode
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
name|SequenceIterator
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|compiler
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_comment
comment|/**  * The XSL expression interface.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|XSLExpression
extends|extends
name|Names
block|{
comment|/** 	 * Clean-up setting. 	 */
specifier|public
name|void
name|setToDefaults
parameter_list|()
function_decl|;
comment|/** 	 * Collect expressions attributes' information. 	 *  	 * @param context 	 * @param attr 	 * @throws XPathException 	 */
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|ContextAtExist
name|context
parameter_list|,
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Validate structure and settings. 	 *  	 * @throws XPathException 	 */
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Report error message. 	 *  	 * @param code 	 * @throws XPathException 	 */
specifier|public
name|void
name|compileError
parameter_list|(
name|String
name|code
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Report error message. 	 *  	 * @param code 	 * @param error 	 * @throws XPathException 	 */
specifier|public
name|void
name|compileError
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|description
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Process expression. 	 *   	 * @param sequenceIterator 	 * @param context 	 * @deprecated Use {@link #process(XSLContext,SequenceIterator)} instead 	 */
specifier|public
name|void
name|process
parameter_list|(
name|SequenceIterator
name|sequenceIterator
parameter_list|,
name|XSLContext
name|context
parameter_list|)
function_decl|;
comment|/** 	 * Process expression. 	 *  	 * @param context 	 * @param sequenceIterator 	 */
specifier|public
name|void
name|process
parameter_list|(
name|XSLContext
name|context
parameter_list|,
name|SequenceIterator
name|sequenceIterator
parameter_list|)
function_decl|;
specifier|public
name|Boolean
name|getBoolean
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
function_decl|;
block|}
end_interface

end_unit

