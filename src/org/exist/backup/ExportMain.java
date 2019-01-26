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
name|backup
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|DBBroker
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
name|Configuration
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
name|DatabaseConfigurationException
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
name|SystemExitCodes
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|Argument
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
name|ArgumentException
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
name|CommandLineParser
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
name|ParsedArguments
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
name|getBool
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
name|getOpt
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

begin_class
specifier|public
class|class
name|ExportMain
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
literal|"print processed resources to stdout"
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
comment|/* control arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|noCheckArg
init|=
name|optionArgument
argument_list|(
literal|"-n"
argument_list|,
literal|"--nocheck"
argument_list|)
operator|.
name|description
argument_list|(
literal|"do not run a consistency check. Just export the data."
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
name|checkDocsArg
init|=
name|optionArgument
argument_list|(
literal|"-s"
argument_list|,
literal|"--check-docs"
argument_list|)
operator|.
name|description
argument_list|(
literal|"scan every document to find errors in the the nodes stored (costs time)"
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
name|directAccessArg
init|=
name|optionArgument
argument_list|(
literal|"-D"
argument_list|,
literal|"--direct"
argument_list|)
operator|.
name|description
argument_list|(
literal|"use an (even more) direct access to the db, bypassing some index structures"
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
name|exportArg
init|=
name|optionArgument
argument_list|(
literal|"-x"
argument_list|,
literal|"--export"
argument_list|)
operator|.
name|description
argument_list|(
literal|"export database contents while preserving as much data as possible"
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
name|incrementalArg
init|=
name|optionArgument
argument_list|(
literal|"-i"
argument_list|,
literal|"--incremental"
argument_list|)
operator|.
name|description
argument_list|(
literal|"create incremental backup (use with --export|-x)"
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
name|zipArg
init|=
name|optionArgument
argument_list|(
literal|"-z"
argument_list|,
literal|"--zip"
argument_list|)
operator|.
name|description
argument_list|(
literal|"write output to a ZIP instead of a file system directory"
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
comment|/* export parameters */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|configArg
init|=
name|fileArgument
argument_list|(
literal|"-c"
argument_list|,
literal|"--config"
argument_list|)
operator|.
name|description
argument_list|(
literal|"the database configuration (conf.xml) file to use for launching the db."
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
name|outputDirArg
init|=
name|fileArgument
argument_list|(
literal|"-d"
argument_list|,
literal|"--dir"
argument_list|)
operator|.
name|description
argument_list|(
literal|"the directory to which all output will be written."
argument_list|)
operator|.
name|defaultValue
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"export"
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
name|BrokerPool
name|startDB
parameter_list|(
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|configFile
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Configuration
name|config
decl_stmt|;
if|if
condition|(
name|configFile
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|(
name|configFile
operator|.
name|get
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
name|config
operator|.
name|setProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_EXPORT_ONLY
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
operator|(
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
decl||
name|EXistException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Failed to open database: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
literal|null
operator|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ParsedArguments
name|arguments
init|=
name|CommandLineParser
operator|.
name|withArguments
argument_list|(
name|noCheckArg
argument_list|,
name|checkDocsArg
argument_list|,
name|directAccessArg
argument_list|,
name|exportArg
argument_list|,
name|incrementalArg
argument_list|,
name|zipArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|configArg
argument_list|,
name|outputDirArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|helpArg
argument_list|,
name|verboseArg
argument_list|)
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|process
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessageAndUsage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|SystemExitCodes
operator|.
name|INVALID_ARGUMENT_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|process
parameter_list|(
specifier|final
name|ParsedArguments
name|arguments
parameter_list|)
block|{
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
name|boolean
name|noCheck
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|noCheckArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|checkDocs
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|checkDocsArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|direct
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|directAccessArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|export
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|exportArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|incremental
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|incrementalArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|zip
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|zipArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|dbConfig
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|configArg
argument_list|)
operator|.
name|map
argument_list|(
name|File
operator|::
name|toPath
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|exportTarget
init|=
name|arguments
operator|.
name|get
argument_list|(
name|outputDirArg
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|(
name|dbConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|SystemExitCodes
operator|.
name|CATCH_ALL_GENERAL_ERROR_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
name|int
name|retval
init|=
literal|0
decl_stmt|;
comment|// return value
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|noCheck
condition|)
block|{
specifier|final
name|ConsistencyCheck
name|checker
init|=
operator|new
name|ConsistencyCheck
argument_list|(
name|broker
argument_list|,
name|direct
argument_list|,
name|checkDocs
argument_list|)
decl_stmt|;
name|errors
operator|=
name|checker
operator|.
name|checkAll
argument_list|(
operator|new
name|CheckCallback
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|errors
operator|!=
literal|null
operator|&&
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERRORS FOUND."
argument_list|)
expr_stmt|;
name|retval
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No errors."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|export
condition|)
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|exportTarget
argument_list|)
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|exportTarget
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|exportTarget
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Output dir already exists and is a file: "
operator|+
name|exportTarget
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|SystemExitCodes
operator|.
name|INVALID_ARGUMENT_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SystemExport
name|sysexport
init|=
operator|new
name|SystemExport
argument_list|(
name|broker
argument_list|,
operator|new
name|Callback
argument_list|(
name|verbose
argument_list|)
argument_list|,
literal|null
argument_list|,
name|direct
argument_list|)
decl_stmt|;
name|sysexport
operator|.
name|export
argument_list|(
name|exportTarget
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|incremental
argument_list|,
name|zip
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Failed to retrieve database broker: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|retval
operator|=
name|SystemExitCodes
operator|.
name|NO_BROKER_EXIT_CODE
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TerminatedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARN: Export was terminated by db."
argument_list|)
expr_stmt|;
name|retval
operator|=
name|SystemExitCodes
operator|.
name|TERMINATED_EARLY_EXIT_CODE
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Failed to retrieve database data: "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|retval
operator|=
name|SystemExitCodes
operator|.
name|PERMISSION_DENIED_EXIT_CODE
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Failed to retrieve database data: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|retval
operator|=
name|SystemExitCodes
operator|.
name|IO_ERROR_EXIT_CODE
expr_stmt|;
block|}
finally|finally
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|retval
argument_list|)
expr_stmt|;
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
specifier|private
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
specifier|public
name|Callback
parameter_list|(
specifier|final
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCollection
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Entering collection "
operator|+
name|path
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|int
name|docsCount
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing document "
operator|+
name|name
operator|+
literal|" ["
operator|+
operator|(
name|count
operator|+
literal|1
operator|)
operator|+
literal|" of "
operator|+
name|docsCount
operator|+
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|exception
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|CheckCallback
implements|implements
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ConsistencyCheck
operator|.
name|ProgressCallback
block|{
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|current
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCollection
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
specifier|final
name|ErrorReport
name|error
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|error
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

