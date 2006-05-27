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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXNotRecognizedException
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
name|SAXNotSupportedException
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
name|XMLReader
import|;
end_import

begin_comment
comment|/**  *  Validate XML documents with their grammars (DTD's and Schemas).  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|Validator
block|{
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
name|Validator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO check whether this private static trick is wise to do.
comment|// These are made static to prevent expensive double initialization
comment|// of classes.
specifier|private
specifier|static
name|GrammarPool
name|grammarPool
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|DatabaseResources
name|dbResources
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|SAXParserFactory
name|saxFactory
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|BrokerPool
name|brokerPool
decl_stmt|;
comment|// Xerces feature and property names
specifier|final
specifier|static
name|String
name|FEATURE_VALIDATION
init|=
literal|"http://xml.org/sax/features/validation"
decl_stmt|;
specifier|final
specifier|static
name|String
name|FEATURE_DYNAMIC
init|=
literal|"http://apache.org/xml/features/validation/dynamic"
decl_stmt|;
specifier|final
specifier|static
name|String
name|FEATURE_SCHEMA
init|=
literal|"http://apache.org/xml/features/validation/schema"
decl_stmt|;
specifier|final
specifier|static
name|String
name|PROPERTIES_GRAMMARPOOL
init|=
literal|"http://apache.org/xml/properties/internal/grammar-pool"
decl_stmt|;
specifier|final
specifier|static
name|String
name|PROPERTIES_RESOLVER
init|=
literal|"http://apache.org/xml/properties/internal/entity-resolver"
decl_stmt|;
specifier|final
specifier|static
name|String
name|PROPERTIES_LOAD_EXT_DTD
init|=
literal|"http://apache.org/xml/features/nonvalidating/load-external-dtd"
decl_stmt|;
specifier|final
specifier|static
name|String
name|PROPERTIES_NS_PRFXS
init|=
literal|"http://xml.org/sax/features/namespace-prefixes"
decl_stmt|;
comment|/**      *  Setup Validator object with brokerpool as centre.      */
specifier|public
name|Validator
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing Validator."
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerPool
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
block|}
comment|// Check xerces version
try|try
block|{
name|String
name|version
init|=
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|XmlLibraryChecker
operator|.
name|isXercesVersionOK
argument_list|()
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Xerces version mismatch! eXist requires '"
operator|+
name|XmlLibraryChecker
operator|.
name|XERCESVERSION
operator|+
literal|"' but found '"
operator|+
name|XmlLibraryChecker
operator|.
name|getXercesVersion
argument_list|()
operator|+
literal|"'. "
operator|+
literal|"Please add correct Xerces libraries to the "
operator|+
literal|"endorsed folder of your JRE or webcontainer."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Could not determine Xerces version. "
operator|+
literal|"Please add correct Xerces libraries to the "
operator|+
literal|"endorsed folder of your JRE or webcontainer."
argument_list|)
expr_stmt|;
block|}
comment|// setup access to grammars ; be sure just one instance!
if|if
condition|(
name|dbResources
operator|==
literal|null
condition|)
block|{
name|dbResources
operator|=
operator|new
name|DatabaseResources
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
comment|//        // setup enityResolver ; be sure just one instance!
comment|//        if(enityResolver==null){
comment|//            enityResolver = new EntityResolver(dbResources);
comment|//        }
comment|// setup grammar brokerPool ; be sure just one instance!
if|if
condition|(
name|grammarPool
operator|==
literal|null
condition|)
block|{
name|grammarPool
operator|=
operator|new
name|GrammarPool
argument_list|()
expr_stmt|;
block|}
comment|// setup sax factory ; be sure just one instance!
if|if
condition|(
name|saxFactory
operator|==
literal|null
condition|)
block|{
name|saxFactory
operator|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
comment|// Enable validation stuff
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Enable validation features of xerces
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|FEATURE_VALIDATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|FEATURE_DYNAMIC
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|FEATURE_SCHEMA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|PROPERTIES_LOAD_EXT_DTD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setFeature
argument_list|(
name|PROPERTIES_NS_PRFXS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
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
catch|catch
parameter_list|(
name|SAXNotRecognizedException
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
catch|catch
parameter_list|(
name|SAXNotSupportedException
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
block|}
comment|/**      *  Validate XML data in inputstream.      *      * @param is    XML input stream.      * @return      Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
return|return
name|validate
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      *  Validate XML data in inputstream.      *      * @param is    XML input stream.      * @return      Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|String
name|grammarPath
parameter_list|)
block|{
return|return
name|validate
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|,
name|grammarPath
argument_list|)
return|;
block|}
comment|/**      *  Validate XML data from reader.      * @param reader    XML input      * @return          Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|validate
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      *  Validate XML data from reader using specified grammar.      *      *  grammar path      *      null : search all documents starting in /db      *      /db/doc/ : start search start in specified collection      *      *      /db/doc/schema/schema.xsd :start with this schema, no search needed.      *      * @return Validation report containing all validation info.      * @param grammarPath   User supplied path to grammar.      * @param reader        XML input.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|grammarPath
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Start validation."
argument_list|)
expr_stmt|;
name|EntityResolver
name|entityResolver
init|=
operator|new
name|EntityResolver
argument_list|(
name|dbResources
argument_list|)
decl_stmt|;
if|if
condition|(
name|grammarPath
operator|!=
literal|null
condition|)
block|{
name|entityResolver
operator|.
name|setStartGrammarPath
argument_list|(
name|grammarPath
argument_list|)
expr_stmt|;
block|}
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
try|try
block|{
name|InputSource
name|source
init|=
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setProperty
argument_list|(
name|PROPERTIES_GRAMMARPOOL
argument_list|,
name|grammarPool
argument_list|)
expr_stmt|;
name|XMLReader
name|xmlReader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|PROPERTIES_RESOLVER
argument_list|,
name|entityResolver
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setErrorHandler
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Parse begin."
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|xmlReader
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|long
name|stop
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|report
operator|.
name|setValidationDuration
argument_list|(
name|stop
operator|-
name|start
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Parse end."
operator|+
literal|"Validation performed in "
operator|+
operator|(
name|stop
operator|-
name|start
operator|)
operator|+
literal|" msec."
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|report
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Parse errors \n"
operator|+
name|report
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
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
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
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
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|report
return|;
block|}
comment|/**      *  Get access to internal DatabaseResources.      * @return Internally used DatabaseResources.      */
specifier|public
name|DatabaseResources
name|getDatabaseResources
parameter_list|()
block|{
return|return
name|dbResources
return|;
block|}
comment|//    /**
comment|//     *  Get access to internal XMLEntityResolver.
comment|//     * @return Internally used XMLEntityResolver.
comment|//     */
comment|//    public XMLEntityResolver getXMLEntityResolver(){
comment|//        return entityResolver;
comment|//    }
comment|/**      *  Get access to internal GrammarPool.      * @return Internally used GrammarPool.      */
specifier|public
name|GrammarPool
name|getGrammarPool
parameter_list|()
block|{
return|return
name|grammarPool
return|;
block|}
specifier|public
name|void
name|setGrammarPool
parameter_list|(
name|GrammarPool
name|gp
parameter_list|)
block|{
name|grammarPool
operator|=
name|gp
expr_stmt|;
block|}
block|}
end_class

end_unit

