begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|PoolableObjectFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|impl
operator|.
name|StackObjectPool
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
name|Namespaces
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
name|storage
operator|.
name|BrokerPoolService
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
name|BrokerPoolServiceException
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
name|GrammarPool
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|DefaultHandler2
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Maintains a pool of XMLReader objects. The pool is available through  * {@link BrokerPool#getParserPool()}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLReaderPool
extends|extends
name|StackObjectPool
argument_list|<
name|XMLReader
argument_list|>
implements|implements
name|BrokerPoolService
block|{
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
name|XMLReaderPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|DefaultHandler2
name|DUMMY_HANDLER
init|=
operator|new
name|DefaultHandler2
argument_list|()
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
comment|/**      * Constructs an XML Reader Pool.      *      * @param factory the object factory      * @param maxIdle the max idle time for a reader      * @param initIdleCapacity the initial capacity      */
specifier|public
name|XMLReaderPool
parameter_list|(
specifier|final
name|PoolableObjectFactory
argument_list|<
name|XMLReader
argument_list|>
name|factory
parameter_list|,
specifier|final
name|int
name|maxIdle
parameter_list|,
specifier|final
name|int
name|initIdleCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|maxIdle
argument_list|,
name|initIdleCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|XMLReader
name|borrowXMLReader
parameter_list|()
block|{
try|try
block|{
specifier|final
name|XMLReader
name|reader
init|=
name|super
operator|.
name|borrowObject
argument_list|()
decl_stmt|;
name|setParserConfigFeatures
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
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
name|IllegalStateException
argument_list|(
literal|"error while returning XMLReader: "
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
block|}
comment|/**      * Sets any features for the parser which were defined in conf.xml      */
specifier|private
name|void
name|setParserConfigFeatures
parameter_list|(
specifier|final
name|XMLReader
name|xmlReader
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserFeatures
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|XmlParser
operator|.
name|XML_PARSER_FEATURES_PROPERTY
argument_list|)
decl_stmt|;
if|if
condition|(
name|parserFeatures
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|feature
range|:
name|parserFeatures
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|feature
operator|.
name|getKey
argument_list|()
argument_list|,
name|feature
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|XMLReader
name|borrowObject
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|borrowXMLReader
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|returnXMLReader
parameter_list|(
name|XMLReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|reader
operator|.
name|setContentHandler
argument_list|(
name|DUMMY_HANDLER
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setErrorHandler
argument_list|(
name|DUMMY_HANDLER
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|DUMMY_HANDLER
argument_list|)
expr_stmt|;
comment|// DIZZZ; workaround Xerces bug. Cached DTDs cause for problems during validation parsing.
specifier|final
name|GrammarPool
name|grammarPool
init|=
operator|(
name|GrammarPool
operator|)
name|getReaderProperty
argument_list|(
name|reader
argument_list|,
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_INTERNAL_GRAMMARPOOL
argument_list|)
decl_stmt|;
if|if
condition|(
name|grammarPool
operator|!=
literal|null
condition|)
block|{
name|grammarPool
operator|.
name|clearDTDs
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|returnObject
argument_list|(
name|reader
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
name|IllegalStateException
argument_list|(
literal|"error while returning XMLReader: "
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
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|returnObject
parameter_list|(
name|XMLReader
name|obj
parameter_list|)
throws|throws
name|Exception
block|{
name|returnXMLReader
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Object
name|getReaderProperty
parameter_list|(
name|XMLReader
name|xmlReader
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|object
init|=
literal|null
decl_stmt|;
try|try
block|{
name|object
operator|=
name|xmlReader
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXNotRecognizedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"SAXNotRecognizedException: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXNotSupportedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"SAXNotSupportedException:"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|object
return|;
block|}
comment|// just used for config properties
specifier|public
interface|interface
name|XmlParser
block|{
name|String
name|XML_PARSER_ELEMENT
init|=
literal|"xml"
decl_stmt|;
name|String
name|XML_PARSER_FEATURES_ELEMENT
init|=
literal|"features"
decl_stmt|;
name|String
name|XML_PARSER_FEATURES_PROPERTY
init|=
literal|"parser.xml-parser.features"
decl_stmt|;
block|}
block|}
end_class

end_unit

