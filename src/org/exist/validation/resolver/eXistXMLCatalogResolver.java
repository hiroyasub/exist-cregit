begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|resolver
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|apache
operator|.
name|xerces
operator|.
name|util
operator|.
name|XMLCatalogResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|XMLResourceIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|XNIException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLInputSource
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
name|ls
operator|.
name|LSInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Wrapper around xerces2's  *<a href="http://xerces.apache.org/xerces2-j/javadocs/xerces2/org/apache/xerces/util/XMLCatalogResolver.html"  *>XMLCatalogresolver</a>  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|eXistXMLCatalogResolver
extends|extends
name|XMLCatalogResolver
block|{
specifier|public
name|eXistXMLCatalogResolver
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|eXistXMLCatalogResolver
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
index|[]
name|catalogs
parameter_list|)
block|{
name|super
argument_list|(
name|catalogs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing using catalogs"
argument_list|)
expr_stmt|;
block|}
name|eXistXMLCatalogResolver
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
index|[]
name|catalogs
parameter_list|,
name|boolean
name|preferPublic
parameter_list|)
block|{
name|super
argument_list|(
name|catalogs
argument_list|,
name|preferPublic
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing using catalogs, preferPublic="
operator|+
name|preferPublic
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|eXistXMLCatalogResolver
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Constructs a catalog resolver with the given list of entry files.      *      * @param catalogs List of Strings      *<p>      *                 TODO: check for non-String and NULL values.      */
specifier|public
name|void
name|setCatalogs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|catalogs
parameter_list|)
block|{
if|if
condition|(
name|catalogs
operator|!=
literal|null
operator|&&
name|catalogs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|String
index|[]
name|allCatalogs
init|=
operator|new
name|String
index|[
name|catalogs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|catalogs
control|)
block|{
name|allCatalogs
index|[
name|counter
index|]
operator|=
name|element
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
name|super
operator|.
name|setCatalogList
argument_list|(
name|allCatalogs
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#resolveEntity(String, String)      */
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving publicId='"
operator|+
name|publicId
operator|+
literal|"', systemId='"
operator|+
name|systemId
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|InputSource
name|retValue
init|=
name|super
operator|.
name|resolveEntity
argument_list|(
name|publicId
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
if|if
condition|(
name|retValue
operator|==
literal|null
condition|)
block|{
name|retValue
operator|=
name|resolveEntityFallback
argument_list|(
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolved "
operator|+
operator|(
name|retValue
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|retValue
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PublicId='"
operator|+
name|retValue
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' SystemId="
operator|+
name|retValue
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retValue
return|;
block|}
comment|/**      * moved from Collection.resolveEntity() revision 6144      */
specifier|private
name|InputSource
name|resolveEntityFallback
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
comment|//if resolution failed and publicId == null,
comment|// try to make absolute file names relative and retry
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolve failed, fallback scenario"
argument_list|)
expr_stmt|;
if|if
condition|(
name|publicId
operator|!=
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|systemId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|url
operator|.
name|getProtocol
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|path
init|=
name|url
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return
name|resolveEntity
argument_list|(
literal|null
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|InputSource
argument_list|(
name|url
operator|.
name|openStream
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#resolveResource(String, String, String, String, String)      */
specifier|public
name|LSInput
name|resolveResource
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|namespaceURI
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|,
name|String
name|baseURI
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving type='"
operator|+
name|type
operator|+
literal|"', namespaceURI='"
operator|+
name|namespaceURI
operator|+
literal|"', publicId='"
operator|+
name|publicId
operator|+
literal|"', systemId='"
operator|+
name|systemId
operator|+
literal|"', baseURI='"
operator|+
name|baseURI
operator|+
literal|"'"
argument_list|)
expr_stmt|;
specifier|final
name|LSInput
name|retValue
init|=
name|super
operator|.
name|resolveResource
argument_list|(
name|type
argument_list|,
name|namespaceURI
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|,
name|baseURI
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolved "
operator|+
operator|(
name|retValue
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|retValue
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PublicId='"
operator|+
name|retValue
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' SystemId='"
operator|+
name|retValue
operator|.
name|getSystemId
argument_list|()
operator|+
literal|"' BaseURI='"
operator|+
name|retValue
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|retValue
return|;
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#resolveEntity(String, String, String, String)      */
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving name='"
operator|+
name|name
operator|+
literal|"', publicId='"
operator|+
name|publicId
operator|+
literal|"', baseURI='"
operator|+
name|baseURI
operator|+
literal|"', systemId='"
operator|+
name|systemId
operator|+
literal|"'"
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|retValue
init|=
name|super
operator|.
name|resolveEntity
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|baseURI
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolved "
operator|+
operator|(
name|retValue
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|retValue
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PublicId='"
operator|+
name|retValue
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' SystemId='"
operator|+
name|retValue
operator|.
name|getSystemId
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|retValue
return|;
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#resolveIdentifier(XMLResourceIdentifier)      */
specifier|public
name|String
name|resolveIdentifier
parameter_list|(
name|XMLResourceIdentifier
name|xri
parameter_list|)
throws|throws
name|IOException
throws|,
name|XNIException
block|{
if|if
condition|(
name|xri
operator|.
name|getExpandedSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getLiteralSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getNamespace
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getPublicId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// quick fail
return|return
literal|null
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving XMLResourceIdentifier: "
operator|+
name|getXriDetails
argument_list|(
name|xri
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|retValue
init|=
name|super
operator|.
name|resolveIdentifier
argument_list|(
name|xri
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolved "
operator|+
operator|(
name|retValue
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|retValue
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Identifier='"
operator|+
name|retValue
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|retValue
return|;
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#resolveEntity(XMLResourceIdentifier)      */
specifier|public
name|XMLInputSource
name|resolveEntity
parameter_list|(
name|XMLResourceIdentifier
name|xri
parameter_list|)
throws|throws
name|XNIException
throws|,
name|IOException
block|{
if|if
condition|(
name|xri
operator|.
name|getExpandedSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getLiteralSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getNamespace
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getPublicId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// quick fail
return|return
literal|null
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving XMLResourceIdentifier: "
operator|+
name|getXriDetails
argument_list|(
name|xri
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|XMLInputSource
name|retValue
init|=
name|super
operator|.
name|resolveEntity
argument_list|(
name|xri
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolved "
operator|+
operator|(
name|retValue
operator|!=
literal|null
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|retValue
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PublicId='"
operator|+
name|retValue
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' SystemId='"
operator|+
name|retValue
operator|.
name|getSystemId
argument_list|()
operator|+
literal|"' BaseSystemId="
operator|+
name|retValue
operator|.
name|getBaseSystemId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retValue
return|;
block|}
comment|/**      * @see org.apache.xerces.util.XMLCatalogResolver#getExternalSubset(String, String)      */
specifier|public
name|InputSource
name|getExternalSubset
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|baseURI
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"name='"
operator|+
name|name
operator|+
literal|"' baseURI='"
operator|+
name|baseURI
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getExternalSubset
argument_list|(
name|name
argument_list|,
name|baseURI
argument_list|)
return|;
block|}
specifier|private
name|String
name|getXriDetails
parameter_list|(
name|XMLResourceIdentifier
name|xrid
parameter_list|)
block|{
return|return
literal|"PublicId='"
operator|+
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"BaseSystemId='"
operator|+
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"ExpandedSystemId='"
operator|+
name|xrid
operator|.
name|getExpandedSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"LiteralSystemId='"
operator|+
name|xrid
operator|.
name|getLiteralSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"Namespace='"
operator|+
name|xrid
operator|.
name|getNamespace
argument_list|()
operator|+
literal|"' "
return|;
block|}
block|}
end_class

end_unit

