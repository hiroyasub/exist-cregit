begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2015 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  */
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Map
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
name|source
operator|.
name|Source
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
name|ExternalModule
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
name|Module
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
name|functions
operator|.
name|inspect
operator|.
name|InspectModule
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
name|BooleanValue
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
name|StringValue
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
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ModuleInfo
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|NAMESPACE_URI_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespace-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the module"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|LOCATION_URI_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"location-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location URI of the module"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ModuleInfo
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|registeredModulesSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"registered-modules"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence containing the namespace URIs of all modules "
operator|+
literal|"currently known to the system, including built in and imported modules."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"the sequence of all of the active function modules namespace URIs"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|registeredModuleSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"is-module-registered"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a Boolean value if the module identified by the namespace URI is registered."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
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
literal|"true if the namespace URI is registered as an active function module"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|mappedModulesSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"mapped-modules"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence containing the namespace URIs of all XQuery modules "
operator|+
literal|"which are statically mapped to a source location in the configuration file. "
operator|+
literal|"This does not include any built in modules."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"the sequence of all of the active function modules namespace URIs"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|mappedModuleSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"is-module-mapped"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a Boolean value if the module statically mapped to a source location in the configuration file."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
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
literal|"true if the namespace URI is mapped as an active function module"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|mapModuleSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"map-module"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Map the module to a source location. This function is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
block|,
name|LOCATION_URI_PARAMETER
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|,
literal|"Returns an empty sequence"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|unmapModuleSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"unmap-module"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Remove relation between module namespace and source location. This function is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|,
literal|"Returns an empty sequence"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|moduleDescriptionSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-module-description"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a short description of the module identified by the namespace URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the description of the active function module identified by the namespace URI"
argument_list|)
argument_list|,
name|InspectModule
operator|.
name|FNS_INSPECT_MODULE_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|moduleInfoSig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-module-info"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an XML fragment providing additional information about the module identified by the "
operator|+
literal|"namespace URI."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the description of the active function module identified by the namespace URI"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|moduleInfoWithURISig
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-module-info"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an XML fragment providing additional information about the module identified by the "
operator|+
literal|"namespace URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAMESPACE_URI_PARAMETER
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the description of the active function module identified by the namespace URI"
argument_list|)
argument_list|,
name|InspectModule
operator|.
name|FNS_INSPECT_MODULE_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_URI_ATTR
init|=
operator|new
name|QName
argument_list|(
literal|"uri"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_PREFIX_ATTR
init|=
operator|new
name|QName
argument_list|(
literal|"prefix"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_SOURCE_ATTR
init|=
operator|new
name|QName
argument_list|(
literal|"source"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_DESC_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"description"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULES_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"modules"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
name|ModuleInfo
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
literal|"get-module-description"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"No module found matching namespace URI: "
operator|+
name|uri
argument_list|)
throw|;
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|module
operator|.
name|getDescription
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
literal|"is-module-registered"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|uri
argument_list|)
decl_stmt|;
return|return
operator|new
name|BooleanValue
argument_list|(
name|module
operator|!=
literal|null
argument_list|)
return|;
block|}
if|else if
condition|(
literal|"mapped-modules"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|ValueSequence
name|resultSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|context
operator|.
name|getMappedModuleURIs
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resultSeq
return|;
block|}
if|else if
condition|(
literal|"is-module-mapped"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
return|return
operator|new
name|BooleanValue
argument_list|(
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_STATIC_MODULE_MAP
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|uri
argument_list|)
operator|!=
literal|null
argument_list|)
return|;
block|}
if|else if
condition|(
literal|"map-module"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
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
specifier|final
name|XPathException
name|xPathException
init|=
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
literal|"' must be a DBA to call this function."
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
specifier|final
name|String
name|namespace
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|location
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moduleMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_STATIC_MODULE_MAP
argument_list|)
decl_stmt|;
name|moduleMap
operator|.
name|put
argument_list|(
name|namespace
argument_list|,
name|location
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
literal|"unmap-module"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
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
specifier|final
name|XPathException
name|xPathException
init|=
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
literal|"' must be a DBA to call this function."
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
specifier|final
name|String
name|namespace
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moduleMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_STATIC_MODULE_MAP
argument_list|)
decl_stmt|;
name|moduleMap
operator|.
name|remove
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
literal|"get-module-info"
operator|.
name|equals
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
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
name|startElement
argument_list|(
name|MODULES_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|!=
literal|null
condition|)
block|{
name|outputModule
argument_list|(
name|builder
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Module
argument_list|>
name|i
init|=
name|context
operator|.
name|getRootModules
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Module
name|module
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|outputModule
argument_list|(
name|builder
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
literal|1
argument_list|)
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
else|else
block|{
specifier|final
name|ValueSequence
name|resultSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|XQueryContext
name|tempContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Module
argument_list|>
name|i
init|=
name|tempContext
operator|.
name|getRootModules
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Module
name|module
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempContext
operator|.
name|getRepository
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
for|for
control|(
specifier|final
name|URI
name|uri
range|:
name|tempContext
operator|.
name|getRepository
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getJavaModules
argument_list|()
control|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|resultSeq
return|;
block|}
block|}
specifier|private
name|void
name|outputModule
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|,
name|Module
name|module
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|MODULE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
name|MODULE_URI_ATTR
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
name|MODULE_PREFIX_ATTR
argument_list|,
name|module
operator|.
name|getDefaultPrefix
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
specifier|final
name|Source
name|source
init|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|addAttribute
argument_list|(
name|MODULE_SOURCE_ATTR
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|MODULE_DESC_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|module
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|//<description>
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|//<module>
block|}
block|}
end_class

end_unit

