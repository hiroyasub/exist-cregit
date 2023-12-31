begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|securitymanager
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
name|dom
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|FunctionReturnSequenceType
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
name|Type
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|IdFunction
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ID
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the user and group names of the account executing the XQuery. "
operator|+
literal|"If the real and effective accounts are different, then both the real "
operator|+
literal|"and effective account details are returned, otherwise only the real "
operator|+
literal|"account details are returned."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Example output when an XQuery is running setUid<id xmlns=\"http://exist-db.org/xquery/securitymanager\"><real><username>guest</username><groups><group>guest</group></groups></real><effective><username>admin</username><groups><group>dba</group></groups></effective></id>."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|IdFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|args
index|[]
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|mySignature
operator|==
name|FNS_ID
condition|)
block|{
return|return
name|functionId
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown function call: "
operator|+
name|getSignature
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a document describing the accounts of the executing process      *       * @return An in-memory document describing the accounts      */
specifier|private
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|DocumentImpl
name|functionId
parameter_list|()
throws|throws
name|XPathException
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"real"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|subjectToXml
argument_list|(
name|builder
argument_list|,
name|context
operator|.
name|getRealUser
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getRealUser
argument_list|()
operator|.
name|getId
argument_list|()
operator|!=
name|context
operator|.
name|getEffectiveUser
argument_list|()
operator|.
name|getId
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"effective"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|subjectToXml
argument_list|(
name|builder
argument_list|,
name|context
operator|.
name|getEffectiveUser
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|subjectToXml
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|Subject
name|subject
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"username"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|subject
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"groups"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|group
range|:
name|subject
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"group"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

