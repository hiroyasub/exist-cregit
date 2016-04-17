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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|TerminatedException
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
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|SystemExport
import|;
end_import

begin_class
specifier|public
class|class
name|FnExport
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FnExport
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
literal|"export"
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
literal|"Export a backup of the database (admin user only)."
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
literal|"dir"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"This is an absolute path to where the backup will be written. Must be writeable by the eXist process."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|INCREMENTAL
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"incremental"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Flag to do incremental export."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|ZIP
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"zip"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Flag to do export to zip file."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|FunctionReturnSequenceType
name|RESULT
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
literal|"the export results"
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
name|INCREMENTAL
block|,
name|ZIP
block|}
argument_list|,
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
literal|"the export results"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"export-silently"
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
name|INCREMENTAL
block|,
name|ZIP
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the export results"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|EXPORT_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"export"
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
name|FnExport
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
block|{
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
block|}
specifier|final
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
name|boolean
name|incremental
init|=
literal|false
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
block|{
name|incremental
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
name|boolean
name|zip
init|=
literal|false
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
block|{
name|zip
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
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
name|EXPORT_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|SystemExport
name|export
init|=
operator|new
name|SystemExport
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|new
name|Callback
argument_list|(
name|builder
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|export
operator|.
name|export
argument_list|(
name|dirOrFile
argument_list|,
name|incremental
argument_list|,
name|zip
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
literal|"export failed with exception: "
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
name|Callback
implements|implements
name|SystemExport
operator|.
name|StatusCallback
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
comment|//        public final static QName INFO_ELEMENT = new QName("info", SystemModule.NAMESPACE_URI, SystemModule.PREFIX);
comment|//        public final static QName WARN_ELEMENT = new QName("warn", SystemModule.NAMESPACE_URI, SystemModule.PREFIX);
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
specifier|public
name|Callback
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
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|TerminatedException
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemExport
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Collection "
operator|+
name|path
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
name|path
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
name|startDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|current
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|TerminatedException
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemExport
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Document "
operator|+
name|name
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
name|name
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
parameter_list|,
name|Throwable
name|exception
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|SystemExport
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|exception
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

