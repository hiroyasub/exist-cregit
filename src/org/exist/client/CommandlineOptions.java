begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-03 Wolfgang M.  * Meier meier@ifs.tu-darmstadt.de http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id$  */
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
name|util
operator|.
name|ArrayList
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
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOptionDescriptor
import|;
end_import

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|CommandlineOptions
block|{
specifier|final
specifier|static
name|int
name|HELP_OPT
init|=
literal|'h'
decl_stmt|;
specifier|final
specifier|static
name|int
name|QUIET_OPT
init|=
literal|'q'
decl_stmt|;
specifier|final
specifier|static
name|int
name|USER_OPT
init|=
literal|'u'
decl_stmt|;
specifier|final
specifier|static
name|int
name|PASS_OPT
init|=
literal|'P'
decl_stmt|;
specifier|final
specifier|static
name|int
name|LOCAL_OPT
init|=
literal|'l'
decl_stmt|;
specifier|final
specifier|static
name|int
name|CONFIG_OPT
init|=
literal|'C'
decl_stmt|;
specifier|final
specifier|static
name|int
name|PARSE_OPT
init|=
literal|'p'
decl_stmt|;
specifier|final
specifier|static
name|int
name|COLLECTION_OPT
init|=
literal|'c'
decl_stmt|;
specifier|final
specifier|static
name|int
name|RESOURCE_OPT
init|=
literal|'f'
decl_stmt|;
specifier|final
specifier|static
name|int
name|REMOVE_OPT
init|=
literal|'r'
decl_stmt|;
specifier|final
specifier|static
name|int
name|GET_OPT
init|=
literal|'g'
decl_stmt|;
specifier|final
specifier|static
name|int
name|MKCOL_OPT
init|=
literal|'m'
decl_stmt|;
specifier|final
specifier|static
name|int
name|RMCOL_OPT
init|=
literal|'R'
decl_stmt|;
specifier|final
specifier|static
name|int
name|OPTION_OPT
init|=
literal|'o'
decl_stmt|;
specifier|final
specifier|static
name|int
name|FIND_OPT
init|=
literal|'x'
decl_stmt|;
specifier|final
specifier|static
name|int
name|RESULTS_OPT
init|=
literal|'n'
decl_stmt|;
specifier|final
specifier|static
name|int
name|VERBOSE_OPT
init|=
literal|'v'
decl_stmt|;
specifier|final
specifier|static
name|int
name|QUERY_FILE_OPT
init|=
literal|'F'
decl_stmt|;
specifier|final
specifier|static
name|int
name|XUPDATE_OPT
init|=
literal|'X'
decl_stmt|;
specifier|final
specifier|static
name|int
name|THREADS_OPT
init|=
literal|'t'
decl_stmt|;
specifier|final
specifier|static
name|int
name|RECURSE_DIRS_OPT
init|=
literal|'d'
decl_stmt|;
specifier|final
specifier|static
name|int
name|NO_GUI_OPT
init|=
literal|'s'
decl_stmt|;
specifier|final
specifier|static
name|int
name|TRACE_QUERIES_OPT
init|=
literal|'T'
decl_stmt|;
specifier|final
specifier|static
name|int
name|OUTPUT_FILE_OPT
init|=
literal|'O'
decl_stmt|;
specifier|final
specifier|static
name|int
name|REINDEX_OPT
init|=
literal|'i'
decl_stmt|;
specifier|final
specifier|static
name|int
name|QUERY_GUI_OPT
init|=
literal|'Q'
decl_stmt|;
specifier|final
specifier|static
name|CLOptionDescriptor
name|OPTIONS
index|[]
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
literal|"quiet"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|QUIET_OPT
argument_list|,
literal|"be quiet. Just print errors."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"verbose"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|VERBOSE_OPT
argument_list|,
literal|"be verbose. Display progress information on put."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"user"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|USER_OPT
argument_list|,
literal|"set username."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"password"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|PASS_OPT
argument_list|,
literal|"specify password."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"local"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|LOCAL_OPT
argument_list|,
literal|"launch a local database instance. Otherwise client will connect to "
operator|+
literal|"URI specified in client.properties."
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
literal|"specify alternate configuration file. Implies -l."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"parse"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_OPTIONAL
argument_list|,
name|PARSE_OPT
argument_list|,
literal|"store files or directories given as extra args on command line."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"remove"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|REMOVE_OPT
argument_list|,
literal|"remove a document."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"collection"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|COLLECTION_OPT
argument_list|,
literal|"set target collection."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"resource"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|RESOURCE_OPT
argument_list|,
literal|"specify a resource contained in the current collection. "
operator|+
literal|"Use in conjunction with -u to specify the resource to "
operator|+
literal|"update."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"get"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|GET_OPT
argument_list|,
literal|"retrieve a document."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"mkcol"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|MKCOL_OPT
argument_list|,
literal|"create a collection (and any missing parent collection). Implies -c."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"rmcol"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|RMCOL_OPT
argument_list|,
literal|"remove entire collection"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"xpath"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_OPTIONAL
argument_list|,
name|FIND_OPT
argument_list|,
literal|"execute XPath query given as argument. Without argument reads query from stdin."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"howmany"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|RESULTS_OPT
argument_list|,
literal|"max. number of query results to be displayed."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"output"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|OUTPUT_FILE_OPT
argument_list|,
literal|"write output of command into given file (use with -x, -g)."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"option"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENTS_REQUIRED_2
operator||
name|CLOptionDescriptor
operator|.
name|DUPLICATES_ALLOWED
argument_list|,
name|OPTION_OPT
argument_list|,
literal|"specify extra options: property=value. For available properties see "
operator|+
literal|"client.properties."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"file"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|QUERY_FILE_OPT
argument_list|,
literal|"load queries from file and execute in random order."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"threads"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|THREADS_OPT
argument_list|,
literal|"number of parallel threads to test with (use with -f)."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"recurse-dirs"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|RECURSE_DIRS_OPT
argument_list|,
literal|"recurse into subdirectories during index?"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"xupdate"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|XUPDATE_OPT
argument_list|,
literal|"process xupdate commands. Commands are read from the "
operator|+
literal|"file specified in the argument."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"no-gui"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|NO_GUI_OPT
argument_list|,
literal|"don't start client with GUI. Just use the shell."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"trace"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|TRACE_QUERIES_OPT
argument_list|,
literal|"log queries to the file specified by the argument (for debugging)."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"reindex"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|REINDEX_OPT
argument_list|,
literal|"reindex the collection specified in the collection argument -c"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"query"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|QUERY_GUI_OPT
argument_list|,
literal|"directly open the query gui"
argument_list|)
block|}
decl_stmt|;
name|boolean
name|needPasswd
init|=
literal|false
decl_stmt|;
name|boolean
name|passwdSpecified
init|=
literal|false
decl_stmt|;
name|boolean
name|interactive
init|=
literal|true
decl_stmt|;
name|boolean
name|foundCollection
init|=
literal|false
decl_stmt|;
name|boolean
name|openQueryGui
init|=
literal|false
decl_stmt|;
name|boolean
name|doStore
init|=
literal|false
decl_stmt|;
name|boolean
name|doReindex
init|=
literal|false
decl_stmt|;
name|String
name|optionRemove
init|=
literal|null
decl_stmt|;
name|String
name|optionGet
init|=
literal|null
decl_stmt|;
name|String
name|optionMkcol
init|=
literal|null
decl_stmt|;
name|String
name|optionRmcol
init|=
literal|null
decl_stmt|;
name|String
name|optionXpath
init|=
literal|null
decl_stmt|;
name|String
name|optionQueryFile
init|=
literal|null
decl_stmt|;
name|String
name|optionXUpdate
init|=
literal|null
decl_stmt|;
name|String
name|optionResource
init|=
literal|null
decl_stmt|;
name|String
name|optionOutputFile
init|=
literal|null
decl_stmt|;
name|List
name|optionalArgs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

