begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|DatabaseResources
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
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLEntityResolver
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  *  Specific grammar resolver for eXist. Currently supports XSD and DTD.  *  * XML Schemas and DTD grammars are stored in collections of the database:  *  /db/system/grammar/xsd  *  /db/system/grammar/dtd  *  * The XSD's are resolved automatically using xQuery. For DTD's (hey this is  * ancient stuff, these are no xml documents) separate data management is  * required. The details are stored in  *  *  /db/system/grammar/dtd/catalog/xml  *  * Extra bonus: an xQuery generating a catalogus with DTD's and XSD's  *  *  /db/system/grammar/xq/catalog.xq  *  * @author dizzzz  * @see org.apache.xerces.xni.parser.XMLEntityResolver  *  * NOTES  * =====  *  * - Keep list called grammar id's. For first grammar the base URI must be set.  *   other grammars must be found relative, unless full path is used.  * - If not schema but folder is supplied, use this folder as startpoint search  *   grammar set  * -  *  */
end_comment

begin_class
specifier|public
class|class
name|EntityResolver
implements|implements
name|XMLEntityResolver
block|{
comment|/* Local logger  */
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|EntityResolver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DatabaseResources
name|databaseResources
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|startGrammarPath
init|=
literal|"/db"
decl_stmt|;
specifier|private
name|boolean
name|isCatalogSpecified
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isGrammarSpecified
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isGrammarSearched
init|=
literal|false
decl_stmt|;
specifier|private
name|XmldbURI
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|documentName
init|=
literal|null
decl_stmt|;
comment|/**      *  Initialize EntityResolver.      * @param pool  BrokerPool      */
specifier|public
name|EntityResolver
parameter_list|(
name|DatabaseResources
name|resources
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing EntityResolver"
argument_list|)
expr_stmt|;
name|this
operator|.
name|databaseResources
operator|=
name|resources
expr_stmt|;
try|try
block|{
name|collection
operator|=
operator|new
name|XmldbURI
argument_list|(
literal|"xmldb:exist:///db"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|isGrammarSearched
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      *  WHat can be supplied:      *      *  - path to collection   (/db/foo/bar/)      *    In this case all grammars must be searched.      *    o Grammars can be found using xquery      *    o DTD's can only be found by finding catalog files.      *      *  - path to start schema (/db/foo/bar/special.xsd)      *    The pointed grammar -if it exist- must be used      *      *  - path to catalog file (/db/foo/bar/catalog.xml)      *      * @param path  Path tp      */
specifier|public
name|void
name|setStartGrammarPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|//TODO : use XmldbURI methods !
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"xmldb:exist://"
operator|+
name|path
expr_stmt|;
block|}
if|else if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"xmldb:exist:///db"
operator|+
name|path
expr_stmt|;
block|}
comment|// TODO help...
name|startGrammarPath
operator|=
name|path
expr_stmt|;
comment|//TODO : read from mime types
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
comment|// Catalog is specified
name|logger
operator|.
name|debug
argument_list|(
literal|"Using catalog '"
operator|+
name|path
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|isCatalogSpecified
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|collection
operator|=
operator|new
name|XmldbURI
argument_list|(
name|DatabaseResources
operator|.
name|getCollectionPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error constructing collection uri of '"
operator|+
name|path
operator|+
literal|"'."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|documentName
operator|=
name|DatabaseResources
operator|.
name|getDocumentName
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|".xsd"
argument_list|)
operator|||
name|path
operator|.
name|endsWith
argument_list|(
literal|".dtd"
argument_list|)
condition|)
block|{
comment|// Grammar is specified
name|logger
operator|.
name|debug
argument_list|(
literal|"Using grammar '"
operator|+
name|path
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|isGrammarSpecified
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"cp="
operator|+
name|DatabaseResources
operator|.
name|getCollectionPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|collection
operator|=
operator|new
name|XmldbURI
argument_list|(
name|DatabaseResources
operator|.
name|getCollectionPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error constructing collection uri of '"
operator|+
name|path
operator|+
literal|"'."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|documentName
operator|=
name|DatabaseResources
operator|.
name|getDocumentName
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Entity resolver must search for grammars.
name|logger
operator|.
name|debug
argument_list|(
literal|"Searching grammars in collection '"
operator|+
name|path
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|isGrammarSearched
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|collection
operator|=
operator|new
name|XmldbURI
argument_list|(
name|DatabaseResources
operator|.
name|getCollectionPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error constructing collection uri of '"
operator|+
name|path
operator|+
literal|"'."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Oh oh
name|logger
operator|.
name|error
argument_list|(
literal|"No grammar, collection of catalog specified."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Resolve GRAMMAR specified with this GRAMMAR id      *      * @param  xrid             Grammar Identifier.      * @throws XNIException     Xerces exception, can be anything      * @throws IOException      Can be anything      * @return Inputsource containing grammar.      */
specifier|public
name|XMLInputSource
name|resolveEntity
parameter_list|(
name|XMLResourceIdentifier
name|xrid
parameter_list|)
throws|throws
name|XNIException
throws|,
name|IOException
block|{
name|XMLInputSource
name|xis
init|=
literal|null
decl_stmt|;
name|String
name|resourcePath
init|=
literal|null
decl_stmt|;
name|byte
name|grammar
index|[]
init|=
literal|null
decl_stmt|;
name|boolean
name|grammarIsBinary
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|isGrammarSpecified
condition|)
block|{
comment|/*  Get User specified grammar right away from database.              *              * At first entrance "BaseSystemId=null" and "ExpandedSystemId=path"              * At following entries BaseSystemId contains schema path and              * ExpandedSystemId contains path new schema              */
if|if
condition|(
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// First step
name|resourcePath
operator|=
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/"
operator|+
name|documentName
expr_stmt|;
block|}
else|else
block|{
comment|// subsequent steps
try|try
block|{
name|resourcePath
operator|=
operator|new
name|XmldbURI
argument_list|(
name|xrid
operator|.
name|getExpandedSystemId
argument_list|()
argument_list|)
operator|.
name|getCollectionPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|documentName
operator|.
name|endsWith
argument_list|(
literal|".xsd"
argument_list|)
condition|)
block|{
name|grammarIsBinary
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCatalogSpecified
condition|)
block|{
comment|/* Only use data in specified catalog  */
name|logger
operator|.
name|debug
argument_list|(
literal|"Resolve using catalog."
argument_list|)
expr_stmt|;
if|if
condition|(
name|xrid
operator|.
name|getNamespace
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Resolve schema namespace."
argument_list|)
expr_stmt|;
name|resourcePath
operator|=
name|databaseResources
operator|.
name|getSchemaPathFromCatalog
argument_list|(
name|collection
argument_list|,
name|documentName
argument_list|,
name|xrid
operator|.
name|getNamespace
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Resolve dtd publicId."
argument_list|)
expr_stmt|;
name|resourcePath
operator|=
name|databaseResources
operator|.
name|getDtdPathFromCatalog
argument_list|(
name|collection
argument_list|,
name|documentName
argument_list|,
name|xrid
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO remove logging for performance?
name|logger
operator|.
name|error
argument_list|(
literal|"Can only resolve namespace or publicId."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|// Search for grammar, Might be 'somewhere' in database.
if|if
condition|(
name|xrid
operator|.
name|getNamespace
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|/*****************************                  * XML Schema search                  *****************************/
name|logger
operator|.
name|debug
argument_list|(
literal|"Searching namespace '"
operator|+
name|xrid
operator|.
name|getNamespace
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|this
operator|.
name|logXMLResourceIdentifier
argument_list|(
name|xrid
argument_list|)
expr_stmt|;
name|resourcePath
operator|=
name|databaseResources
operator|.
name|getSchemaPath
argument_list|(
name|collection
argument_list|,
name|xrid
operator|.
name|getNamespace
argument_list|()
argument_list|)
expr_stmt|;
name|grammarIsBinary
operator|=
literal|false
expr_stmt|;
block|}
if|else if
condition|(
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|/*****************************                  * DTD search                  *****************************/
name|logger
operator|.
name|debug
argument_list|(
literal|"Searching publicId '"
operator|+
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|this
operator|.
name|logXMLResourceIdentifier
argument_list|(
name|xrid
argument_list|)
expr_stmt|;
name|resourcePath
operator|=
name|databaseResources
operator|.
name|getDtdPath
argument_list|(
name|collection
argument_list|,
name|xrid
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|grammarIsBinary
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// Fast escape; no logging, otherwise validation is slow!
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|resourcePath
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Resource not found in database."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Get grammar from database
name|logger
operator|.
name|debug
argument_list|(
literal|"resourcePath="
operator|+
name|resourcePath
argument_list|)
expr_stmt|;
name|grammar
operator|=
name|databaseResources
operator|.
name|getGrammar
argument_list|(
name|grammarIsBinary
argument_list|,
name|resourcePath
argument_list|)
expr_stmt|;
if|if
condition|(
name|grammar
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Grammar not retrieved from database."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Reader
name|rd
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|grammar
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO check ; is all information filled incorrect?
name|logger
operator|.
name|info
argument_list|(
literal|"publicId="
operator|+
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|+
literal|" systemId="
operator|+
literal|"xmldb:exist://"
operator|+
name|resourcePath
operator|+
literal|" baseSystemId="
operator|+
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|xis
operator|=
operator|new
name|XMLInputSource
argument_list|(
name|xrid
operator|.
name|getPublicId
argument_list|()
argument_list|,
comment|// publicId
literal|"xmldb:exist://"
operator|+
name|resourcePath
argument_list|,
comment|// systemId
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
argument_list|,
comment|// baseSystemId
name|rd
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
return|return
name|xis
return|;
block|}
specifier|private
name|void
name|logXMLResourceIdentifier
parameter_list|(
name|XMLResourceIdentifier
name|xrid
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"PublicId="
operator|+
name|xrid
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"BaseSystemId="
operator|+
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"ExpandedSystemId="
operator|+
name|xrid
operator|.
name|getExpandedSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"LiteralSystemId="
operator|+
name|xrid
operator|.
name|getLiteralSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Namespace="
operator|+
name|xrid
operator|.
name|getNamespace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

