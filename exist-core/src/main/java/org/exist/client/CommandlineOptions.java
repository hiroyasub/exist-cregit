begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
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
name|net
operator|.
name|URISyntaxException
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
name|util
operator|.
name|*
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
name|util
operator|.
name|URIUtils
import|;
end_import

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ArgumentUtil
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|Arguments
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Command Line Options for the {@link InteractiveClient}  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|CommandlineOptions
block|{
comment|/* general arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|?
argument_list|>
name|helpArg
init|=
name|helpArgument
argument_list|(
literal|"-h"
argument_list|,
literal|"--help"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|quietArg
init|=
name|optionArgument
argument_list|(
literal|"-q"
argument_list|,
literal|"--quiet"
argument_list|)
operator|.
name|description
argument_list|(
literal|"be quiet. Just print errors."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|verboseArg
init|=
name|optionArgument
argument_list|(
literal|"-v"
argument_list|,
literal|"--verbose"
argument_list|)
operator|.
name|description
argument_list|(
literal|"be verbose. Display progress information on put."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|outputFileArg
init|=
name|fileArgument
argument_list|(
literal|"-O"
argument_list|,
literal|"--output"
argument_list|)
operator|.
name|description
argument_list|(
literal|"write output of command into given file (use with -x, -g)."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|optionArg
init|=
name|stringArgument
argument_list|(
literal|"-o"
argument_list|,
literal|"--option"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify extra options: property=value. For available properties see client.properties."
argument_list|)
operator|.
name|asKeyValuesWithKeyParser
argument_list|(
name|StringParsers
operator|.
name|stringParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* database connection arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|userArg
init|=
name|stringArgument
argument_list|(
literal|"-u"
argument_list|,
literal|"--user"
argument_list|)
operator|.
name|description
argument_list|(
literal|"set username."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|null
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|passwordArg
init|=
name|stringArgument
argument_list|(
literal|"-P"
argument_list|,
literal|"--password"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify password."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|useSslArg
init|=
name|optionArgument
argument_list|(
literal|"-S"
argument_list|,
literal|"--use-ssl"
argument_list|)
operator|.
name|description
argument_list|(
literal|"Use SSL by default for remote connections"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|embeddedArg
init|=
name|optionArgument
argument_list|(
literal|"-l"
argument_list|,
literal|"--local"
argument_list|)
operator|.
name|description
argument_list|(
literal|"launch a local database instance. Otherwise client will connect to URI specified in client.properties."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|embeddedConfigArg
init|=
name|fileArgument
argument_list|(
literal|"-C"
argument_list|,
literal|"--config"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify alternate configuration file. Implies -l."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|noEmbeddedModeArg
init|=
name|optionArgument
argument_list|(
literal|"-N"
argument_list|,
literal|"--no-embedded-mode"
argument_list|)
operator|.
name|description
argument_list|(
literal|"do not make embedded mode available"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* gui arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|noGuiArg
init|=
name|optionArgument
argument_list|(
literal|"-s"
argument_list|,
literal|"--no-gui"
argument_list|)
operator|.
name|description
argument_list|(
literal|"don't start client with GUI. Just use the shell."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|guiQueryDialogArg
init|=
name|optionArgument
argument_list|(
literal|"-Q"
argument_list|,
literal|"--query"
argument_list|)
operator|.
name|description
argument_list|(
literal|"directly open the query gui"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* mk/rm/set collection arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|mkColArg
init|=
name|stringArgument
argument_list|(
literal|"-m"
argument_list|,
literal|"--mkcol"
argument_list|)
operator|.
name|description
argument_list|(
literal|"create a collection (and any missing parent collection). Implies -c."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|rmColArg
init|=
name|stringArgument
argument_list|(
literal|"-R"
argument_list|,
literal|"--rmcol"
argument_list|)
operator|.
name|description
argument_list|(
literal|"remove entire collection"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|setColArg
init|=
name|stringArgument
argument_list|(
literal|"-c"
argument_list|,
literal|"--collection"
argument_list|)
operator|.
name|description
argument_list|(
literal|"set target collection."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* put/get/rm document arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|List
argument_list|<
name|File
argument_list|>
argument_list|>
name|parseDocsArg
init|=
name|fileArgument
argument_list|(
literal|"-p"
argument_list|,
literal|"--parse"
argument_list|)
operator|.
name|description
argument_list|(
literal|"store files or directories given as extra args on command line."
argument_list|)
operator|.
name|variableArity
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|getDocArg
init|=
name|stringArgument
argument_list|(
literal|"-g"
argument_list|,
literal|"--get"
argument_list|)
operator|.
name|description
argument_list|(
literal|"retrieve a document."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|rmDocArg
init|=
name|stringArgument
argument_list|(
literal|"-r"
argument_list|,
literal|"--remove"
argument_list|)
operator|.
name|description
argument_list|(
literal|"remove a document."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* query arguments */
specifier|public
specifier|static
specifier|final
name|String
name|XPATH_STDIN
init|=
literal|"<<STDIN"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|xpathArg
init|=
name|stringArgument
argument_list|(
literal|"-x"
argument_list|,
literal|"--xpath"
argument_list|)
operator|.
name|description
argument_list|(
literal|"execute XPath query given as argument. Without argument reads query from stdin."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|List
argument_list|<
name|File
argument_list|>
argument_list|>
name|loadQueriesArg
init|=
name|fileArgument
argument_list|(
literal|"-F"
argument_list|,
literal|"--file"
argument_list|)
operator|.
name|description
argument_list|(
literal|"load queries from file and execute in random order."
argument_list|)
operator|.
name|variableArity
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Integer
argument_list|>
name|howManyResultsArg
init|=
name|integerArgument
argument_list|(
literal|"-n"
argument_list|,
literal|"--howmany"
argument_list|)
operator|.
name|description
argument_list|(
literal|"max. number of query results to be displayed."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|traceQueriesArg
init|=
name|fileArgument
argument_list|(
literal|"-T"
argument_list|,
literal|"--trace"
argument_list|)
operator|.
name|description
argument_list|(
literal|"log queries to the file specified by the argument (for debugging)."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* xupdate arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|setDocArg
init|=
name|stringArgument
argument_list|(
literal|"-f"
argument_list|,
literal|"--resource"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify a resource contained in the current collection. Use in conjunction with --xupdate to specify the resource to update."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|xupdateArg
init|=
name|fileArgument
argument_list|(
literal|"-X"
argument_list|,
literal|"--xupdate"
argument_list|)
operator|.
name|description
argument_list|(
literal|"process XUpdate commands. Commands are read from the file specified in the argument."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* reindex arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|reindexArg
init|=
name|optionArgument
argument_list|(
literal|"-i"
argument_list|,
literal|"--reindex"
argument_list|)
operator|.
name|description
argument_list|(
literal|"reindex the collection specified in the collection argument --collection"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|reindexRecurseDirsArg
init|=
name|optionArgument
argument_list|(
literal|"-d"
argument_list|,
literal|"--recurse-dirs"
argument_list|)
operator|.
name|description
argument_list|(
literal|"recurse into subdirectories during index?"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|optUri
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|argument
parameter_list|)
throws|throws
name|URISyntaxException
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|uriStr
init|=
name|getOpt
argument_list|(
name|parsedArguments
argument_list|,
name|argument
argument_list|)
decl_stmt|;
if|if
condition|(
name|uriStr
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|URIUtils
operator|.
name|encodeXmldbUriFor
argument_list|(
name|uriStr
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
name|CommandlineOptions
name|parse
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ArgumentException
throws|,
name|URISyntaxException
block|{
specifier|final
name|ParsedArguments
name|arguments
init|=
name|CommandLineParser
operator|.
name|withArguments
argument_list|(
name|userArg
argument_list|,
name|passwordArg
argument_list|,
name|useSslArg
argument_list|,
name|embeddedArg
argument_list|,
name|embeddedConfigArg
argument_list|,
name|noEmbeddedModeArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|noGuiArg
argument_list|,
name|guiQueryDialogArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|mkColArg
argument_list|,
name|rmColArg
argument_list|,
name|setColArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|parseDocsArg
argument_list|,
name|getDocArg
argument_list|,
name|rmDocArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|xpathArg
argument_list|,
name|loadQueriesArg
argument_list|,
name|howManyResultsArg
argument_list|,
name|traceQueriesArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|setDocArg
argument_list|,
name|xupdateArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|reindexArg
argument_list|,
name|reindexRecurseDirsArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|helpArg
argument_list|,
name|quietArg
argument_list|,
name|verboseArg
argument_list|,
name|outputFileArg
argument_list|,
name|optionArg
argument_list|)
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|quiet
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|quietArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|verbose
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|verboseArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|outputFile
init|=
name|getPathOpt
argument_list|(
name|arguments
argument_list|,
name|outputFileArg
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
name|arguments
operator|.
name|get
argument_list|(
name|optionArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|username
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|userArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|password
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|passwordArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|useSSL
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|useSslArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|embedded
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|embeddedArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|embeddedConfig
init|=
name|getPathOpt
argument_list|(
name|arguments
argument_list|,
name|embeddedConfigArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|noEmbeddedMode
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|noEmbeddedModeArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|startGUI
init|=
operator|!
name|getBool
argument_list|(
name|arguments
argument_list|,
name|noGuiArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|openQueryGUI
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|guiQueryDialogArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|mkCol
init|=
name|optUri
argument_list|(
name|arguments
argument_list|,
name|mkColArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|rmCol
init|=
name|optUri
argument_list|(
name|arguments
argument_list|,
name|rmColArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|setCol
init|=
name|optUri
argument_list|(
name|arguments
argument_list|,
name|setColArg
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|parseDocs
init|=
name|getPathsOpt
argument_list|(
name|arguments
argument_list|,
name|parseDocsArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|getDoc
init|=
name|optUri
argument_list|(
name|arguments
argument_list|,
name|getDocArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|rmDoc
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|rmDocArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|maybeXpath
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|xpathArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|xpath
decl_stmt|;
if|if
condition|(
name|maybeXpath
operator|.
name|isPresent
argument_list|()
condition|)
block|{
if|if
condition|(
name|maybeXpath
operator|.
name|get
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|xpath
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|XPATH_STDIN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xpath
operator|=
name|maybeXpath
expr_stmt|;
block|}
block|}
else|else
block|{
name|xpath
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|queryFiles
init|=
name|getPathsOpt
argument_list|(
name|arguments
argument_list|,
name|loadQueriesArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|howManyResults
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|howManyResultsArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|traceQueriesFile
init|=
name|getPathOpt
argument_list|(
name|arguments
argument_list|,
name|traceQueriesArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|setDoc
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|setDocArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|xupdateFile
init|=
name|getPathOpt
argument_list|(
name|arguments
argument_list|,
name|xupdateArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|reindex
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|reindexArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|reindexRecurse
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|reindexRecurseDirsArg
argument_list|)
decl_stmt|;
return|return
operator|new
name|CommandlineOptions
argument_list|(
name|quiet
argument_list|,
name|verbose
argument_list|,
name|outputFile
argument_list|,
name|options
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|useSSL
argument_list|,
name|embedded
argument_list|,
name|embeddedConfig
argument_list|,
name|noEmbeddedMode
argument_list|,
name|startGUI
argument_list|,
name|openQueryGUI
argument_list|,
name|mkCol
argument_list|,
name|rmCol
argument_list|,
name|setCol
argument_list|,
name|parseDocs
argument_list|,
name|getDoc
argument_list|,
name|rmDoc
argument_list|,
name|xpath
argument_list|,
name|queryFiles
argument_list|,
name|howManyResults
argument_list|,
name|traceQueriesFile
argument_list|,
name|setDoc
argument_list|,
name|xupdateFile
argument_list|,
name|reindex
argument_list|,
name|reindexRecurse
argument_list|)
return|;
block|}
specifier|public
name|CommandlineOptions
parameter_list|(
name|boolean
name|quiet
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|Optional
argument_list|<
name|Path
argument_list|>
name|outputFile
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|username
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|password
parameter_list|,
name|boolean
name|useSSL
parameter_list|,
name|boolean
name|embedded
parameter_list|,
name|Optional
argument_list|<
name|Path
argument_list|>
name|embeddedConfig
parameter_list|,
name|boolean
name|noEmbeddedMode
parameter_list|,
name|boolean
name|startGUI
parameter_list|,
name|boolean
name|openQueryGUI
parameter_list|,
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|mkCol
parameter_list|,
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|rmCol
parameter_list|,
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|setCol
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|parseDocs
parameter_list|,
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|getDoc
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|rmDoc
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|xpath
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|queryFiles
parameter_list|,
name|Optional
argument_list|<
name|Integer
argument_list|>
name|howManyResults
parameter_list|,
name|Optional
argument_list|<
name|Path
argument_list|>
name|traceQueriesFile
parameter_list|,
name|Optional
argument_list|<
name|String
argument_list|>
name|setDoc
parameter_list|,
name|Optional
argument_list|<
name|Path
argument_list|>
name|xupdateFile
parameter_list|,
name|boolean
name|reindex
parameter_list|,
name|boolean
name|reindexRecurse
parameter_list|)
block|{
name|this
operator|.
name|quiet
operator|=
name|quiet
expr_stmt|;
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
name|this
operator|.
name|outputFile
operator|=
name|outputFile
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|useSSL
operator|=
name|useSSL
expr_stmt|;
name|this
operator|.
name|embedded
operator|=
name|embedded
expr_stmt|;
name|this
operator|.
name|embeddedConfig
operator|=
name|embeddedConfig
expr_stmt|;
name|this
operator|.
name|noEmbeddedMode
operator|=
name|noEmbeddedMode
expr_stmt|;
name|this
operator|.
name|startGUI
operator|=
name|startGUI
expr_stmt|;
name|this
operator|.
name|openQueryGUI
operator|=
name|openQueryGUI
expr_stmt|;
name|this
operator|.
name|mkCol
operator|=
name|mkCol
expr_stmt|;
name|this
operator|.
name|rmCol
operator|=
name|rmCol
expr_stmt|;
name|this
operator|.
name|setCol
operator|=
name|setCol
expr_stmt|;
name|this
operator|.
name|parseDocs
operator|=
name|parseDocs
expr_stmt|;
name|this
operator|.
name|getDoc
operator|=
name|getDoc
expr_stmt|;
name|this
operator|.
name|rmDoc
operator|=
name|rmDoc
expr_stmt|;
name|this
operator|.
name|xpath
operator|=
name|xpath
expr_stmt|;
name|this
operator|.
name|queryFiles
operator|=
name|queryFiles
expr_stmt|;
name|this
operator|.
name|howManyResults
operator|=
name|howManyResults
expr_stmt|;
name|this
operator|.
name|traceQueriesFile
operator|=
name|traceQueriesFile
expr_stmt|;
name|this
operator|.
name|setDoc
operator|=
name|setDoc
expr_stmt|;
name|this
operator|.
name|xupdateFile
operator|=
name|xupdateFile
expr_stmt|;
name|this
operator|.
name|reindex
operator|=
name|reindex
expr_stmt|;
name|this
operator|.
name|reindexRecurse
operator|=
name|reindexRecurse
expr_stmt|;
block|}
specifier|final
name|boolean
name|quiet
decl_stmt|;
specifier|final
name|boolean
name|verbose
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|outputFile
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|username
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|password
decl_stmt|;
specifier|final
name|boolean
name|useSSL
decl_stmt|;
specifier|final
name|boolean
name|embedded
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|embeddedConfig
decl_stmt|;
specifier|final
name|boolean
name|noEmbeddedMode
decl_stmt|;
specifier|final
name|boolean
name|startGUI
decl_stmt|;
specifier|final
name|boolean
name|openQueryGUI
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|mkCol
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|rmCol
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|setCol
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|parseDocs
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|XmldbURI
argument_list|>
name|getDoc
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|rmDoc
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|xpath
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|queryFiles
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|howManyResults
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|traceQueriesFile
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|setDoc
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|xupdateFile
decl_stmt|;
specifier|final
name|boolean
name|reindex
decl_stmt|;
specifier|final
name|boolean
name|reindexRecurse
decl_stmt|;
block|}
end_class

end_unit

