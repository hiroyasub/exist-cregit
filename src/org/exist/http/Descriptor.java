begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-06 Wolfgang M.  * Meier meier@ifs.tu-darmstadt.de http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileWriter
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|memtree
operator|.
name|SAXAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ConfigurationHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|SingleInstanceConfiguration
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
name|Document
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
name|Element
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
name|NodeList
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
comment|/** Webapplication Descriptor  *   * Class representation of an XQuery Web Application Descriptor file  * with some helper functions for performing Descriptor related actions  * Uses the Singleton design pattern.  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-03-19  * @version 1.71  */
end_comment

begin_comment
comment|// TODO: doLogRequestInReplayLog() - add the facility to log HTTP PUT requests, may need changes to HttpServletRequestWrapper
end_comment

begin_comment
comment|// TODO: doLogRequestInReplayLog() - add the facility to log HTTP POST form file uploads, may need changes to HttpServletRequestWrapper
end_comment

begin_class
specifier|public
class|class
name|Descriptor
implements|implements
name|ErrorHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SYSTEM_LINE_SEPARATOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|//References
specifier|private
specifier|static
name|Descriptor
name|singletonRef
decl_stmt|;
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
name|Descriptor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Logger
comment|/** descriptor file (descriptor.xml) */
specifier|private
specifier|final
specifier|static
name|String
name|file
init|=
literal|"descriptor.xml"
decl_stmt|;
comment|//Data
specifier|private
name|BufferedWriter
name|bufWriteReplayLog
init|=
literal|null
decl_stmt|;
comment|//Should a replay log of requests be created
specifier|private
name|boolean
name|requestsFiltered
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|allowSourceList
index|[]
init|=
literal|null
decl_stmt|;
comment|//Array of xql files to allow source to be viewed
specifier|private
name|String
name|mapList
index|[]
index|[]
init|=
literal|null
decl_stmt|;
comment|//Array of Mappings
comment|/** 	 * Descriptor Constructor 	 *  	 * Class has a Singleton design pattern 	 * to get an instance, call getDescriptorSingleton() 	 */
specifier|private
name|Descriptor
parameter_list|()
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// First, try to read Descriptor from file. Guess the location if necessary
comment|// from the home folder.
specifier|final
name|Path
name|f
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Giving up unable to read descriptor file from "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading Descriptor from file "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
comment|// otherise, secondly
comment|// try to read the Descriptor from a file within the classpath
name|is
operator|=
name|Descriptor
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading Descriptor from classloader in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Giving up unable to read descriptor.xml file from classloader in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// initialize xml parser
comment|// we use eXist's in-memory DOM implementation to work
comment|// around a bug in Xerces
specifier|final
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
specifier|final
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
specifier|final
name|Document
name|doc
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
comment|//load<xquery-app> attribue settings
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"request-replay-log"
argument_list|)
argument_list|)
condition|)
block|{
specifier|final
name|File
name|logFile
init|=
operator|new
name|File
argument_list|(
literal|"request-replay-log.txt"
argument_list|)
decl_stmt|;
name|bufWriteReplayLog
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|logFile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|attr
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"filtered"
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|!=
literal|null
condition|)
name|requestsFiltered
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
comment|//load<allow-source> settings
specifier|final
name|NodeList
name|allowsourcexqueries
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"allow-source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowsourcexqueries
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|configureAllowSourceXQuery
argument_list|(
operator|(
name|Element
operator|)
name|allowsourcexqueries
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//load<maps> settings
specifier|final
name|NodeList
name|maps
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"maps"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maps
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|configureMaps
argument_list|(
operator|(
name|Element
operator|)
name|maps
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while reading descriptor file: "
operator|+
name|file
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
name|cfg
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while reading descriptor file: "
operator|+
name|file
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while reading descriptor file: "
operator|+
name|file
argument_list|,
name|io
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Returns a refernce to this (Descriptor) Singleton class      *       * @return The Descriptor object reference      */
specifier|public
specifier|static
specifier|synchronized
name|Descriptor
name|getDescriptorSingleton
parameter_list|()
block|{
if|if
condition|(
name|singletonRef
operator|==
literal|null
condition|)
block|{
name|singletonRef
operator|=
operator|new
name|Descriptor
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|singletonRef
operator|)
return|;
block|}
comment|/**      * loads<allow-source> settings from the descriptor.xml file      *      * @param	allowsourcexqueries	The<allow-source> DOM Element from the descriptor.xml file      */
specifier|private
name|void
name|configureAllowSourceXQuery
parameter_list|(
name|Element
name|allowsourcexqueries
parameter_list|)
block|{
comment|//Get the xquery element(s)
specifier|final
name|NodeList
name|nlXQuery
init|=
name|allowsourcexqueries
operator|.
name|getElementsByTagName
argument_list|(
literal|"xquery"
argument_list|)
decl_stmt|;
comment|//Setup the hashmap to hold the xquery elements
name|allowSourceList
operator|=
operator|new
name|String
index|[
name|nlXQuery
operator|.
name|getLength
argument_list|()
index|]
expr_stmt|;
name|Element
name|elem
init|=
literal|null
decl_stmt|;
comment|//temporary holds xquery elements
comment|//Iterate through the xquery elements
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nlXQuery
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|nlXQuery
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|//<xquery>
name|String
name|path
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
comment|//@path
comment|//must be a path to allow source for
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error element 'xquery' requires an attribute 'path'"
argument_list|)
expr_stmt|;
return|return;
block|}
name|path
operator|=
name|path
operator|.
name|replaceAll
argument_list|(
literal|"\\$\\{WEBAPP_HOME\\}"
argument_list|,
name|SingleInstanceConfiguration
operator|.
name|getWebappHome
argument_list|()
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|//store the path
name|allowSourceList
index|[
name|i
index|]
operator|=
name|path
expr_stmt|;
block|}
block|}
comment|/**      * loads<maps> settings from the descriptor.xml file      *      * @param	maps	The<maps> DOM Element from the descriptor.xml file      */
specifier|private
name|void
name|configureMaps
parameter_list|(
name|Element
name|maps
parameter_list|)
block|{
comment|//TODO: add pattern support for mappings, as an alternative to path - deliriumsky
comment|//Get the map element(s)
specifier|final
name|NodeList
name|nlMap
init|=
name|maps
operator|.
name|getElementsByTagName
argument_list|(
literal|"map"
argument_list|)
decl_stmt|;
comment|//Setup the hashmap to hold the map elements
name|mapList
operator|=
operator|new
name|String
index|[
name|nlMap
operator|.
name|getLength
argument_list|()
index|]
index|[
literal|2
index|]
expr_stmt|;
name|Element
name|elem
init|=
literal|null
decl_stmt|;
comment|//temporary holds map elements
comment|//Iterate through the map elements
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nlMap
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|nlMap
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|//<map>
name|String
name|path
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
comment|//@path
comment|//String pattern = elem.getAttribute("pattern");//@pattern
name|String
name|view
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"view"
argument_list|)
decl_stmt|;
comment|//@view
comment|//must be a path or a pattern to map from
if|if
condition|(
name|path
operator|==
literal|null
comment|/*&& pattern == null*/
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error element 'map' requires an attribute 'path' or an attribute 'pattern'"
argument_list|)
expr_stmt|;
return|return;
block|}
name|path
operator|=
name|path
operator|.
name|replaceAll
argument_list|(
literal|"\\$\\{WEBAPP_HOME\\}"
argument_list|,
name|SingleInstanceConfiguration
operator|.
name|getWebappHome
argument_list|()
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|//must be a view to map to
if|if
condition|(
name|view
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error element 'map' requires an attribute 'view'"
argument_list|)
expr_stmt|;
return|return;
block|}
name|view
operator|=
name|view
operator|.
name|replaceAll
argument_list|(
literal|"\\$\\{WEBAPP_HOME\\}"
argument_list|,
name|SingleInstanceConfiguration
operator|.
name|getWebappHome
argument_list|()
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
comment|//store what to map from
comment|/* if(path != null)             {*/
comment|//store the path
name|mapList
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|path
expr_stmt|;
comment|/*}             else             {             	//store the pattern             	mapList[i][0] = pattern;             }*/
comment|//store what to map to
name|mapList
index|[
name|i
index|]
index|[
literal|1
index|]
operator|=
name|view
expr_stmt|;
block|}
block|}
comment|/** 	 * Determines whether it is permissible to show the source of an XQuery. 	 * Takes a path such as that from RESTServer.doGet() as an argument, 	 * if it finds a matching allowsourcexquery path in the descriptor then it returns true else it returns false 	 *    	 * @param path		The path of the XQuery (e.g. /db/MyCollection/query.xql) 	 * @return			The boolean value true or false indicating whether it is permissible to show the source 	 */
specifier|public
name|boolean
name|allowSource
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|allowSourceList
operator|!=
literal|null
condition|)
block|{
comment|//Iterate through the xqueries that source viewing is allowed for
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allowSourceList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// DWES: this helps a lot. quickfix not the final solution
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|//does the path match the<allow-source><xquery path=""/></allow-source> path
if|if
condition|(
operator|(
name|allowSourceList
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|)
operator|||
operator|(
name|path
operator|.
name|indexOf
argument_list|(
name|allowSourceList
index|[
name|i
index|]
argument_list|)
operator|>
operator|-
literal|1
operator|)
condition|)
block|{
comment|//yes, return true
return|return
operator|(
literal|true
operator|)
return|;
block|}
block|}
block|}
return|return
operator|(
literal|false
operator|)
return|;
block|}
comment|/** 	 * Map's one XQuery or Collection path to another 	 * Takes a path such as that from RESTServer.doGet() as an argument, 	 * if it finds a matching map path then it returns the map view else it returns the passed in path 	 *    	 * @param path		The path of the XQuery or Collection (e.g. /db/MyCollection/query.xql or /db/MyCollection) to map from 	 * @return			The path of the XQuery or Collection (e.g. /db/MyCollection/query.xql or /db/MyCollection) to map to 	 */
specifier|public
name|String
name|mapPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|mapList
operator|==
literal|null
condition|)
comment|//has a list of mappings been specified?
block|{
return|return
operator|(
name|path
operator|)
return|;
block|}
comment|//Iterate through the mappings
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mapList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//does the path or the path/ match the map path
if|if
condition|(
name|mapList
index|[
name|i
index|]
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
operator|(
name|mapList
index|[
name|i
index|]
index|[
literal|0
index|]
operator|+
literal|"/"
operator|)
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|//return the view
return|return
operator|(
name|mapList
index|[
name|i
index|]
index|[
literal|1
index|]
operator|)
return|;
block|}
block|}
comment|//no match return the original path
return|return
operator|(
name|path
operator|)
return|;
block|}
specifier|public
name|boolean
name|requestsFiltered
parameter_list|()
block|{
return|return
name|requestsFiltered
return|;
block|}
comment|/** 	 * Determines whether it is permissible to Log Requests 	 *  	 * Enabled by descriptor.xml<xquery-app request-replay-log="true"> 	 *    	 * @return			The boolean value true or false indicating whether it is permissible to Log Requests   	 */
specifier|public
name|boolean
name|allowRequestLogging
parameter_list|()
block|{
if|if
condition|(
name|bufWriteReplayLog
operator|==
literal|null
condition|)
block|{
return|return
operator|(
literal|false
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
literal|true
operator|)
return|;
block|}
block|}
comment|/** 	 * Logs HTTP Request's in a log file suitable for replaying to eXist later  	 * Takes a HttpServletRequest or a HttpServletRequestWrapper as an argument for logging. 	 *  	 * Enabled by descriptor.xml<xquery-app request-replay-log="true"> 	 *    	 * @param request		The HttpServletRequest to log. 	 * For Simple HTTP POST Requests - EXistServlet/XQueryServlet - POST parameters (e.g. form data) will only be logged if a HttpServletRequestWrapper is used instead of HttpServletRequest! POST Uploaded files are not yet supported! 	 * For XML-RPC Requests - RpcServlet - HttpServletRequestWrapper must be used, otherwise the content of the Request will be lost! 	 * For Cocoon Requests  - 	 */
specifier|public
specifier|synchronized
name|void
name|doLogRequestInReplayLog
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
comment|//Only log if set by the user in descriptor.xml<xquery-app request-replay-log="true">
if|if
condition|(
name|bufWriteReplayLog
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|//Log the Request
try|try
block|{
comment|//Store the date and time
name|bufWriteReplayLog
operator|.
name|write
argument_list|(
literal|"Date: "
argument_list|)
expr_stmt|;
specifier|final
name|SimpleDateFormat
name|formatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd/MM/yyyy HH:mm:ss"
argument_list|)
decl_stmt|;
name|bufWriteReplayLog
operator|.
name|write
argument_list|(
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bufWriteReplayLog
operator|.
name|write
argument_list|(
name|SYSTEM_LINE_SEPARATOR
argument_list|)
expr_stmt|;
comment|//Store the request string excluding the first line
specifier|final
name|String
name|requestAsString
init|=
name|request
operator|.
name|toString
argument_list|()
decl_stmt|;
name|bufWriteReplayLog
operator|.
name|write
argument_list|(
name|requestAsString
operator|.
name|substring
argument_list|(
name|requestAsString
operator|.
name|indexOf
argument_list|(
name|SYSTEM_LINE_SEPARATOR
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//End of record indicator
name|bufWriteReplayLog
operator|.
name|write
argument_list|(
name|SYSTEM_LINE_SEPARATOR
argument_list|)
expr_stmt|;
comment|//flush the buffer to file
name|bufWriteReplayLog
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not write request replay log: "
operator|+
name|ioe
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|/**      * Thows a CloneNotSupportedException as this class uses a Singleton design pattern      *       * @return Will never return anything!      */
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
comment|//Class is a Singleton, dont allow cloning
throw|throw
operator|new
name|CloneNotSupportedException
argument_list|()
throw|;
block|}
comment|/**      * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)      */
annotation|@
name|Override
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error occurred while reading descriptor file [line: "
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
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)      */
annotation|@
name|Override
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error occurred while reading descriptor file [line: "
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
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
comment|/**       * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)      */
annotation|@
name|Override
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
name|LOG
operator|.
name|error
argument_list|(
literal|"error occurred while reading descriptor file [line: "
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
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

