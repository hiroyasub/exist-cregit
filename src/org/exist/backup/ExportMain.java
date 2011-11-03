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
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLArgsParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOptionDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLUtil
import|;
end_import

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
name|util
operator|.
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|ExportMain
block|{
comment|// command-line options
specifier|private
specifier|final
specifier|static
name|int
name|HELP_OPT
init|=
literal|'h'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|EXPORT_OPT
init|=
literal|'x'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|OUTPUT_DIR_OPT
init|=
literal|'d'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|CONFIG_OPT
init|=
literal|'c'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|INCREMENTAL_OPT
init|=
literal|'i'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|NO_CHECK_OPT
init|=
literal|'n'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DIRECT_ACCESS_OPT
init|=
literal|'D'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|ZIP_OPT
init|=
literal|'z'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|CLOptionDescriptor
index|[]
name|OPTIONS
init|=
operator|new
name|CLOptionDescriptor
index|[]
block|{
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"help"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|HELP_OPT
argument_list|,
literal|"print help on command line options and exit."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"dir"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|OUTPUT_DIR_OPT
argument_list|,
literal|"the directory to which all output will be written."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"config"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|CONFIG_OPT
argument_list|,
literal|"the database configuration (conf.xml) file to use "
operator|+
literal|"for launching the db."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"direct"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|DIRECT_ACCESS_OPT
argument_list|,
literal|"use an (even more) direct access to the db, bypassing some "
operator|+
literal|"index structures"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"export"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|EXPORT_OPT
argument_list|,
literal|"export database contents while preserving as much data as possible"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"incremental"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|INCREMENTAL_OPT
argument_list|,
literal|"create incremental backup (use with --export|-x)"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"nocheck"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|NO_CHECK_OPT
argument_list|,
literal|"do not run a consistency check. Just export the data."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"zip"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|ZIP_OPT
argument_list|,
literal|"write output to a ZIP instead of a file system directory"
argument_list|)
block|}
decl_stmt|;
specifier|protected
specifier|static
name|BrokerPool
name|startDB
parameter_list|(
name|String
name|configFile
parameter_list|)
block|{
try|try
block|{
name|Configuration
name|config
decl_stmt|;
if|if
condition|(
name|configFile
operator|==
literal|null
condition|)
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|(
name|configFile
argument_list|,
literal|null
argument_list|)
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
name|DatabaseConfigurationException
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
catch|catch
parameter_list|(
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
name|String
index|[]
name|args
parameter_list|)
block|{
name|CLArgsParser
name|optParser
init|=
operator|new
name|CLArgsParser
argument_list|(
name|args
argument_list|,
name|OPTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|optParser
operator|.
name|getErrorString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|optParser
operator|.
name|getErrorString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|boolean
name|export
init|=
literal|false
decl_stmt|;
name|boolean
name|incremental
init|=
literal|false
decl_stmt|;
name|boolean
name|direct
init|=
literal|false
decl_stmt|;
name|boolean
name|zip
init|=
literal|false
decl_stmt|;
name|boolean
name|nocheck
init|=
literal|false
decl_stmt|;
name|String
name|exportTarget
init|=
literal|"export/"
decl_stmt|;
name|String
name|dbConfig
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|CLOption
argument_list|>
name|opts
init|=
name|optParser
operator|.
name|getArguments
argument_list|()
decl_stmt|;
for|for
control|(
name|CLOption
name|option
range|:
name|opts
control|)
block|{
switch|switch
condition|(
name|option
operator|.
name|getId
argument_list|()
condition|)
block|{
case|case
name|HELP_OPT
case|:
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: java "
operator|+
name|ExportMain
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" [options]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|CLUtil
operator|.
name|describeOptions
argument_list|(
name|OPTIONS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|OUTPUT_DIR_OPT
case|:
block|{
name|exportTarget
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|DIRECT_ACCESS_OPT
case|:
block|{
name|direct
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|CONFIG_OPT
case|:
block|{
name|dbConfig
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|EXPORT_OPT
case|:
block|{
name|export
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|INCREMENTAL_OPT
case|:
block|{
name|incremental
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|ZIP_OPT
case|:
block|{
name|zip
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|NO_CHECK_OPT
case|:
block|{
name|nocheck
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
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
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|retval
init|=
literal|0
decl_stmt|;
comment|// return value
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
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
name|nocheck
condition|)
block|{
name|ConsistencyCheck
name|checker
init|=
operator|new
name|ConsistencyCheck
argument_list|(
name|broker
argument_list|,
name|direct
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
name|errors
operator|.
name|size
argument_list|()
operator|>
literal|0
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
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|exportTarget
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
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
argument_list|()
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
argument_list|,
name|incremental
argument_list|,
literal|true
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
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
literal|2
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
literal|3
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
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
specifier|public
name|void
name|startDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|docsCount
parameter_list|)
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
name|count
operator|+
literal|" of "
operator|+
name|docsCount
operator|+
literal|']'
argument_list|)
expr_stmt|;
block|}
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
block|{
block|}
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
block|{
block|}
specifier|public
name|void
name|error
parameter_list|(
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

