begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
package|;
end_package

begin_comment
comment|/*  * dbXML License, Version 1.0  *  *  * Copyright (c) 1999-2001 The dbXML Group, L.L.C.  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by  *        The dbXML Group (http://www.dbxml.com/)."  *    Alternately, this acknowledgment may appear in the software  *    itself, if and wherever such third-party acknowledgments normally  *    appear.  *  * 4. The names "dbXML" and "The dbXML Group" must not be used to  *    endorse or promote products derived from this software without  *    prior written permission. For written permission, please contact  *    info@dbxml.com.  *  * 5. Products derived from this software may not be called "dbXML",  *    nor may "dbXML" appear in their name, without prior written  *    permission of The dbXML Group.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE DBXML GROUP OR ITS CONTRIBUTORS  * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,  * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT  * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR  * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF  * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING  * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  *  * $Id$  */
end_comment

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|TerminatedException
import|;
end_import

begin_comment
comment|/**  * BTreeCallback is a callback interface for BTree queries.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BTreeCallback
block|{
comment|/**     * indexInfo is a callback method for index enumeration.     *     * @param value The Value being reported     * @param pointer The data pointer being reported     * @return false to cancel the enumeration     * @throws TerminatedException to be documented     */
name|boolean
name|indexInfo
parameter_list|(
name|Value
name|value
parameter_list|,
name|long
name|pointer
parameter_list|)
throws|throws
name|TerminatedException
function_decl|;
block|}
end_interface

end_unit

