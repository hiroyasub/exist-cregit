begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id:  */
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
name|log4j
operator|.
name|xml
operator|.
name|DOMConfigurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|resolver
operator|.
name|tools
operator|.
name|CatalogResolver
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
name|IndexPaths
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
name|*
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
name|ErrorHandler
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
name|SAXParseException
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
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|util
operator|.
name|HashMap
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
name|DocumentBuilder
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
name|DocumentBuilderFactory
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Description of the Class  *  * @author Wolfgang Meier  *  */
end_comment

begin_class
specifier|public
class|class
name|Configuration
implements|implements
name|ErrorHandler
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|DocumentBuilder
name|builder
init|=
literal|null
decl_stmt|;
specifier|protected
name|HashMap
name|config
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|file
init|=
literal|null
decl_stmt|;
comment|/** 	 * Constructor for the Configuration object 	 * 	 * @param file Description of the Parameter 	 * 	 * @exception DatabaseConfigurationException Description of the Exception 	 */
specifier|public
name|Configuration
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
argument_list|(
name|file
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for the Configuration object 	 * 	 * @param file Description of the Parameter 	 * @param dbHome Description of the Parameter 	 * 	 * @exception DatabaseConfigurationException Description of the Exception 	 */
specifier|public
name|Configuration
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|dbHome
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
name|String
name|pathSep
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
operator|)
operator|&&
name|dbHome
operator|!=
literal|null
condition|)
block|{
name|file
operator|=
name|dbHome
operator|+
name|pathSep
operator|+
name|file
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"unable to read configuration. Trying to guess location ..."
argument_list|)
expr_stmt|;
comment|// fall back and try to read from home directory
if|if
condition|(
name|dbHome
operator|==
literal|null
condition|)
block|{
comment|// try to determine exist home directory
name|dbHome
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbHome
operator|==
literal|null
condition|)
name|dbHome
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dbHome
operator|!=
literal|null
condition|)
name|file
operator|=
name|dbHome
operator|+
name|pathSep
operator|+
name|file
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"giving up"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"unable to read configuration file"
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
comment|// initialize xml parser
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|builder
operator|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setErrorHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|NodeList
name|parser
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"indexer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|p
init|=
operator|(
name|Element
operator|)
name|parser
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|tmp
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"tmpDir"
argument_list|)
decl_stmt|;
name|String
name|batchLoad
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"batchLoad"
argument_list|)
decl_stmt|;
name|String
name|parseNum
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"parseNumbers"
argument_list|)
decl_stmt|;
name|String
name|indexDepth
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"index-depth"
argument_list|)
decl_stmt|;
name|String
name|stemming
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"stemming"
argument_list|)
decl_stmt|;
name|String
name|ctlDir
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"controls"
argument_list|)
decl_stmt|;
name|String
name|suppressWS
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"suppress-whitespace"
argument_list|)
decl_stmt|;
name|String
name|caseSensitive
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"caseSensitive"
argument_list|)
decl_stmt|;
name|String
name|tokenizer
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"tokenizer"
argument_list|)
decl_stmt|;
name|String
name|validation
init|=
name|p
operator|.
name|getAttribute
argument_list|(
literal|"validation"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"tmpDir"
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
if|if
condition|(
name|batchLoad
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"batchLoad"
argument_list|,
operator|new
name|Boolean
argument_list|(
name|batchLoad
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|parseNum
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.indexNumbers"
argument_list|,
operator|new
name|Boolean
argument_list|(
name|parseNum
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ctlDir
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"parser.ctlDir"
argument_list|,
name|ctlDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|stemming
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.stem"
argument_list|,
operator|new
name|Boolean
argument_list|(
name|stemming
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|caseSensitive
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.case-sensitive"
argument_list|,
operator|new
name|Boolean
argument_list|(
name|caseSensitive
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|suppressWS
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.suppress-whitespace"
argument_list|,
name|suppressWS
argument_list|)
expr_stmt|;
if|if
condition|(
name|validation
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.validation"
argument_list|,
name|validation
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenizer
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"indexer.tokenizer"
argument_list|,
name|tokenizer
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexDepth
operator|!=
literal|null
condition|)
try|try
block|{
name|int
name|depth
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|indexDepth
argument_list|)
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"indexer.index-depth"
argument_list|,
operator|new
name|Integer
argument_list|(
name|depth
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|NodeList
name|index
init|=
name|p
operator|.
name|getElementsByTagName
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|index
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|idx
init|=
operator|(
name|Element
operator|)
name|index
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|doctype
init|=
name|idx
operator|.
name|getAttribute
argument_list|(
literal|"doctype"
argument_list|)
decl_stmt|;
name|String
name|def
init|=
name|idx
operator|.
name|getAttribute
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|IndexPaths
name|paths
init|=
operator|new
name|IndexPaths
argument_list|(
name|def
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|indexAttributes
init|=
name|idx
operator|.
name|getAttribute
argument_list|(
literal|"attributes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexAttributes
operator|!=
literal|null
condition|)
name|paths
operator|.
name|setIncludeAttributes
argument_list|(
name|indexAttributes
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|indexAlphaNum
init|=
name|idx
operator|.
name|getAttribute
argument_list|(
literal|"alphanum"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexAlphaNum
operator|!=
literal|null
condition|)
name|paths
operator|.
name|setIncludeAlphaNum
argument_list|(
name|indexAlphaNum
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDepth
operator|=
name|idx
operator|.
name|getAttribute
argument_list|(
literal|"index-depth"
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexDepth
operator|!=
literal|null
condition|)
try|try
block|{
name|int
name|depth
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|indexDepth
argument_list|)
decl_stmt|;
name|paths
operator|.
name|setIndexDepth
argument_list|(
name|depth
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|NodeList
name|include
init|=
name|idx
operator|.
name|getElementsByTagName
argument_list|(
literal|"include"
argument_list|)
decl_stmt|;
name|String
name|ps
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|include
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|ps
operator|=
operator|(
operator|(
name|Element
operator|)
name|include
operator|.
name|item
argument_list|(
name|j
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|addInclude
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|put
argument_list|(
literal|"indexScheme."
operator|+
name|doctype
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|NodeList
name|exclude
init|=
name|idx
operator|.
name|getElementsByTagName
argument_list|(
literal|"exclude"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|exclude
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|ps
operator|=
operator|(
operator|(
name|Element
operator|)
name|exclude
operator|.
name|item
argument_list|(
name|j
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|addExclude
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeList
name|stopwords
init|=
name|p
operator|.
name|getElementsByTagName
argument_list|(
literal|"stopwords"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopwords
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|stopwordFile
init|=
operator|(
operator|(
name|Element
operator|)
name|stopwords
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|File
name|sf
init|=
operator|new
name|File
argument_list|(
name|stopwordFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sf
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|stopwordFile
operator|=
name|dbHome
operator|+
name|pathSep
operator|+
name|stopwordFile
expr_stmt|;
name|sf
operator|=
operator|new
name|File
argument_list|(
name|stopwordFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|sf
operator|.
name|canRead
argument_list|()
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"stopwords"
argument_list|,
name|stopwordFile
argument_list|)
expr_stmt|;
block|}
block|}
name|CatalogResolver
name|resolver
init|=
operator|new
name|CatalogResolver
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"resolver"
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
name|NodeList
name|entityResolver
init|=
name|p
operator|.
name|getElementsByTagName
argument_list|(
literal|"entity-resolver"
argument_list|)
decl_stmt|;
if|if
condition|(
name|entityResolver
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|r
init|=
operator|(
name|Element
operator|)
name|entityResolver
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NodeList
name|catalogs
init|=
name|r
operator|.
name|getElementsByTagName
argument_list|(
literal|"catalog"
argument_list|)
decl_stmt|;
name|String
name|catalog
decl_stmt|;
name|File
name|catalogFile
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|catalogs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|catalog
operator|=
operator|(
operator|(
name|Element
operator|)
name|catalogs
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
if|if
condition|(
name|pathSep
operator|.
name|equals
argument_list|(
literal|"\\"
argument_list|)
condition|)
name|catalog
operator|=
name|catalog
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'\\'
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbHome
operator|==
literal|null
condition|)
name|catalogFile
operator|=
operator|new
name|File
argument_list|(
name|catalog
argument_list|)
expr_stmt|;
else|else
name|catalogFile
operator|=
operator|new
name|File
argument_list|(
name|dbHome
operator|+
name|pathSep
operator|+
name|catalog
argument_list|)
expr_stmt|;
if|if
condition|(
name|catalogFile
operator|.
name|exists
argument_list|()
condition|)
name|resolver
operator|.
name|getCatalog
argument_list|()
operator|.
name|parseCatalog
argument_list|(
name|catalogFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|NodeList
name|dbcon
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"db-connection"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbcon
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|con
init|=
operator|(
name|Element
operator|)
name|dbcon
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|cacheSize
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"cacheSize"
argument_list|)
decl_stmt|;
name|String
name|pageSize
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"pageSize"
argument_list|)
decl_stmt|;
name|String
name|dataFiles
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"files"
argument_list|)
decl_stmt|;
name|String
name|buffers
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"buffers"
argument_list|)
decl_stmt|;
name|String
name|collBuffers
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"collection_buffers"
argument_list|)
decl_stmt|;
name|String
name|wordBuffers
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"words_buffers"
argument_list|)
decl_stmt|;
name|String
name|elementBuffers
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"elements_buffers"
argument_list|)
decl_stmt|;
name|String
name|freeMem
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"free_mem_min"
argument_list|)
decl_stmt|;
name|String
name|driver
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"driver"
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|String
name|pass
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
name|String
name|compress
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"compress"
argument_list|)
decl_stmt|;
name|String
name|mysql
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
name|String
name|service
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"serviceName"
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|con
operator|.
name|getAttribute
argument_list|(
literal|"encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|compress
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.compress"
argument_list|,
name|compress
argument_list|)
expr_stmt|;
if|if
condition|(
name|driver
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"driver"
argument_list|,
name|driver
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"url"
argument_list|,
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|pass
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
name|pass
argument_list|)
expr_stmt|;
if|if
condition|(
name|mysql
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"database"
argument_list|,
name|mysql
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"encoding"
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.serviceName"
argument_list|,
name|service
argument_list|)
expr_stmt|;
comment|// directory for database files
if|if
condition|(
name|dataFiles
operator|!=
literal|null
condition|)
block|{
name|File
name|df
init|=
operator|new
name|File
argument_list|(
name|dataFiles
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|df
operator|.
name|isAbsolute
argument_list|()
operator|)
operator|&&
name|dbHome
operator|!=
literal|null
condition|)
block|{
name|dataFiles
operator|=
name|dbHome
operator|+
name|pathSep
operator|+
name|dataFiles
expr_stmt|;
name|df
operator|=
operator|new
name|File
argument_list|(
name|dataFiles
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|df
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"cannot read data directory: "
operator|+
name|df
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.data-dir"
argument_list|,
name|df
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cacheSize
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.cache-size"
argument_list|,
operator|new
name|Integer
argument_list|(
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|buffers
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.buffers"
argument_list|,
operator|new
name|Integer
argument_list|(
name|buffers
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|pageSize
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.page-size"
argument_list|,
operator|new
name|Integer
argument_list|(
name|pageSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|collBuffers
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.collections.buffers"
argument_list|,
operator|new
name|Integer
argument_list|(
name|collBuffers
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|wordBuffers
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.words.buffers"
argument_list|,
operator|new
name|Integer
argument_list|(
name|wordBuffers
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|elementBuffers
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.elements.buffers"
argument_list|,
operator|new
name|Integer
argument_list|(
name|elementBuffers
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
if|if
condition|(
name|freeMem
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.min_free_memory"
argument_list|,
operator|new
name|Integer
argument_list|(
name|freeMem
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
name|NodeList
name|poolConf
init|=
name|con
operator|.
name|getElementsByTagName
argument_list|(
literal|"pool"
argument_list|)
decl_stmt|;
if|if
condition|(
name|poolConf
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|pool
init|=
operator|(
name|Element
operator|)
name|poolConf
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|min
init|=
name|pool
operator|.
name|getAttribute
argument_list|(
literal|"min"
argument_list|)
decl_stmt|;
name|String
name|max
init|=
name|pool
operator|.
name|getAttribute
argument_list|(
literal|"max"
argument_list|)
decl_stmt|;
name|String
name|idle
init|=
name|pool
operator|.
name|getAttribute
argument_list|(
literal|"idle"
argument_list|)
decl_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.pool.min"
argument_list|,
operator|new
name|Integer
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.pool.max"
argument_list|,
operator|new
name|Integer
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
if|if
condition|(
name|idle
operator|!=
literal|null
condition|)
try|try
block|{
name|config
operator|.
name|put
argument_list|(
literal|"db-connection.pool.idle"
argument_list|,
operator|new
name|Integer
argument_list|(
name|idle
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
name|NodeList
name|serializers
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"serializer"
argument_list|)
decl_stmt|;
name|Element
name|serializer
decl_stmt|;
if|if
condition|(
name|serializers
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|serializer
operator|=
operator|(
name|Element
operator|)
name|serializers
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|xinclude
init|=
name|serializer
operator|.
name|getAttribute
argument_list|(
literal|"enable-xinclude"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xinclude
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"serialization.enable-xinclude"
argument_list|,
name|xinclude
argument_list|)
expr_stmt|;
name|String
name|xsl
init|=
name|serializer
operator|.
name|getAttribute
argument_list|(
literal|"enable-xsl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xsl
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"serialization.enable-xsl"
argument_list|,
name|xsl
argument_list|)
expr_stmt|;
name|String
name|indent
init|=
name|serializer
operator|.
name|getAttribute
argument_list|(
literal|"indent"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indent
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"serialization.indent"
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|String
name|internalId
init|=
name|serializer
operator|.
name|getAttribute
argument_list|(
literal|"add-exist-id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|internalId
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"serialization.add-exist-id"
argument_list|,
name|internalId
argument_list|)
expr_stmt|;
name|String
name|matchTagging
init|=
name|serializer
operator|.
name|getAttribute
argument_list|(
literal|"match-tagging"
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchTagging
operator|!=
literal|null
condition|)
name|config
operator|.
name|put
argument_list|(
literal|"serialization.match-tagging"
argument_list|,
name|matchTagging
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|log4j
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"log4j:configuration"
argument_list|)
decl_stmt|;
if|if
condition|(
name|log4j
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|logRoot
init|=
operator|(
name|Element
operator|)
name|log4j
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// make files relative if dbHome != null
if|if
condition|(
name|dbHome
operator|!=
literal|null
condition|)
block|{
name|NodeList
name|params
init|=
name|logRoot
operator|.
name|getElementsByTagName
argument_list|(
literal|"param"
argument_list|)
decl_stmt|;
name|Element
name|param
decl_stmt|;
name|String
name|name
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
name|pathSep
operator|.
name|equals
argument_list|(
literal|"\\"
argument_list|)
condition|)
name|dbHome
operator|=
name|dbHome
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|param
operator|=
operator|(
name|Element
operator|)
name|params
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|name
operator|=
name|param
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|name
operator|!=
literal|null
operator|)
operator|&&
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"File"
argument_list|)
condition|)
block|{
name|path
operator|=
name|param
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
comment|//if ( pathSep.equals( "\\" ) )
comment|//    path = path.replace( '/', '\\' );
name|f
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
name|path
operator|=
name|dbHome
operator|+
literal|'/'
operator|+
name|path
expr_stmt|;
block|}
name|param
operator|.
name|setAttribute
argument_list|(
literal|"value"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|DOMConfigurator
operator|.
name|configure
argument_list|(
name|logRoot
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while reading config file: "
operator|+
name|file
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|cfg
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while reading config file: "
operator|+
name|file
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|cfg
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while reading config file: "
operator|+
name|file
argument_list|,
name|io
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|io
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Gets the integer attribute of the Configuration object 	 * 	 * @param name Description of the Parameter 	 * 	 * @return The integer value 	 */
specifier|public
name|int
name|getInteger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Object
name|obj
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|Integer
operator|)
condition|)
return|return
operator|-
literal|1
return|;
return|return
operator|(
operator|(
name|Integer
operator|)
name|obj
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/** 	 * Gets the path attribute of the Configuration object 	 * 	 * @return The path value 	 */
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/** 	 * Gets the property attribute of the Configuration object 	 * 	 * @param name Description of the Parameter 	 * 	 * @return The property value 	 */
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|config
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * Description of the Method 	 * 	 * @param name Description of the Parameter 	 * 	 * @return Description of the Return Value 	 */
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|config
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * Sets the property attribute of the Configuration object 	 * 	 * @param name The new property value 	 * @param obj The new property value 	 */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|config
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException) 	 */
specifier|public
name|void
name|error
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error occured while reading configuration file "
operator|+
literal|"[line: "
operator|+
name|exception
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|"]:"
operator|+
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException) 	 */
specifier|public
name|void
name|fatalError
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error occured while reading configuration file "
operator|+
literal|"[line: "
operator|+
name|exception
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|"]:"
operator|+
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException) 	 */
specifier|public
name|void
name|warning
parameter_list|(
name|SAXParseException
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"error occured while reading configuration file "
operator|+
literal|"[line: "
operator|+
name|exception
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|"]:"
operator|+
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

