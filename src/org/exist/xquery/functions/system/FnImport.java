begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012-2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|system
package|;
end_package

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
name|xmldb
operator|.
name|XmldbURI
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
name|NodeValue
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
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|SystemImport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|AbstractRestoreListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|RestoreListener
import|;
end_import

begin_class
specifier|public
class|class
name|FnImport
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FnImport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|NAME
init|=
operator|new
name|QName
argument_list|(
literal|"import"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|DESCRIPTION
init|=
literal|"Restore the database or a section of the database (admin user only)."
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|DIRorFILE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dir-or-file"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"This is either a backup directory with the backup descriptor (__contents__.xml) or a backup ZIP file."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|ADMIN_PASS
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"admin-pass"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The password for the admin user"
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|NEW_ADMIN_PASS
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"new-admin-pass"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Set the admin password to this new password."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionReturnSequenceType
name|RETURN
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the import results"
argument_list|)
decl_stmt|;
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
name|NAME
argument_list|,
name|DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DIRorFILE
block|,
name|ADMIN_PASS
block|,
name|NEW_ADMIN_PASS
block|}
argument_list|,
name|RETURN
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"import-silently"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
operator|+
literal|" Messagers from exporter reroute to logs."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DIRorFILE
block|,
name|ADMIN_PASS
block|,
name|NEW_ADMIN_PASS
block|}
argument_list|,
name|RETURN
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|IMPORT_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"import"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
name|FnImport
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
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to kill a running xquery"
argument_list|)
operator|)
throw|;
name|String
name|dirOrFile
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|adminPass
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|hasOne
argument_list|()
condition|)
name|adminPass
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|String
name|adminPassAfter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|hasOne
argument_list|()
condition|)
name|adminPassAfter
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|NAME
operator|.
name|equals
argument_list|(
name|mySignature
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|IMPORT_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|SystemImport
name|restore
init|=
operator|new
name|SystemImport
argument_list|(
name|context
operator|.
name|getDatabase
argument_list|()
argument_list|)
decl_stmt|;
name|RestoreListener
name|listener
init|=
operator|new
name|XMLRestoreListener
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|restore
operator|.
name|restore
argument_list|(
name|listener
argument_list|,
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|DBA_USER
argument_list|,
name|adminPass
argument_list|,
name|adminPassAfter
argument_list|,
operator|new
name|File
argument_list|(
name|dirOrFile
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"restore failed with exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
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
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|XMLRestoreListener
extends|extends
name|AbstractRestoreListener
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|COLLECTION_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"collection"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|RESOURCE_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"resource"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|INFO_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"info"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|WARN_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"warn"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|ERROR_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MemTreeBuilder
name|builder
decl_stmt|;
specifier|private
name|XMLRestoreListener
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemImport
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Create collection "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|COLLECTION_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restored
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemImport
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Restore resource "
operator|+
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|RESOURCE_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemImport
operator|.
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|INFO_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemImport
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|WARN_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemImport
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|ERROR_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

