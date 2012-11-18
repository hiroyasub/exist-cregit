begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqdoc
operator|.
name|ant
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
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|FileInputStream
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
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DirectoryScanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|FileSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|ant
operator|.
name|AbstractXMLDBTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|XQueryService
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
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|XQDocTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"import module namespace xqdm=\"http://exist-db.org/xquery/xqdoc\";\n"
operator|+
literal|"import module namespace xdb=\"http://exist-db.org/xquery/xmldb\";\n"
operator|+
literal|"declare namespace xqdoc=\"http://www.xqdoc.org/1.0\"\n;"
operator|+
literal|"declare variable $uri external;\n"
operator|+
literal|"declare variable $name external;\n"
operator|+
literal|"declare variable $collection external;\n"
operator|+
literal|"declare variable $data external;\n"
operator|+
literal|"let $xml :=\n"
operator|+
literal|"if ($uri) then\n"
operator|+
literal|"   xqdm:scan(xs:anyURI($uri))\n"
operator|+
literal|"else\n"
operator|+
literal|"   xqdm:scan($data, $name)\n"
operator|+
literal|"let $moduleURI := $xml//xqdoc:module/xqdoc:uri\n"
operator|+
literal|"let $docName := concat(util:hash($moduleURI, 'MD5'), '.xml')\n"
operator|+
literal|"return\n"
operator|+
literal|"   xdb:store($collection, $docName, $xml, 'application/xml')"
decl_stmt|;
specifier|private
name|String
name|moduleURI
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|createCollection
init|=
literal|false
decl_stmt|;
specifier|private
name|List
argument_list|<
name|FileSet
argument_list|>
name|fileSets
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|p
init|=
name|uri
operator|.
name|indexOf
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"invalid uri: '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|)
throw|;
name|String
name|baseURI
init|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|uri
operator|.
name|length
argument_list|()
operator|-
literal|3
condition|)
name|path
operator|=
literal|""
expr_stmt|;
else|else
name|path
operator|=
name|uri
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|3
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|createCollection
condition|)
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|root
operator|=
name|mkcol
argument_list|(
name|root
argument_list|,
name|baseURI
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|XQUERY
argument_list|)
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|root
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"uri"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|moduleURI
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|declareVariable
argument_list|(
literal|"uri"
argument_list|,
name|moduleURI
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"data"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|service
operator|.
name|execute
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|FileSet
name|fileSet
range|:
name|fileSets
control|)
block|{
name|DirectoryScanner
name|scanner
init|=
name|fileSet
operator|.
name|getDirectoryScanner
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|scanner
operator|.
name|scan
argument_list|()
expr_stmt|;
name|String
index|[]
name|files
init|=
name|scanner
operator|.
name|getIncludedFiles
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Found "
operator|+
name|files
operator|.
name|length
operator|+
literal|" files to upload.\n"
argument_list|)
expr_stmt|;
name|File
name|baseDir
init|=
name|scanner
operator|.
name|getBasedir
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Storing "
operator|+
name|files
index|[
name|i
index|]
operator|+
literal|" ...\n"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|read
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|service
operator|.
name|declareVariable
argument_list|(
literal|"name"
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"data"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|service
operator|.
name|execute
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
else|else
name|log
argument_list|(
name|msg
argument_list|,
name|e
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
else|else
name|log
argument_list|(
name|msg
argument_list|,
name|e
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setCreatecollection
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|createCollection
operator|=
name|create
expr_stmt|;
block|}
specifier|public
name|void
name|setModuleuri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|moduleURI
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|void
name|addFileset
parameter_list|(
name|FileSet
name|set
parameter_list|)
block|{
if|if
condition|(
name|fileSets
operator|==
literal|null
condition|)
name|fileSets
operator|=
operator|new
name|ArrayList
argument_list|<
name|FileSet
argument_list|>
argument_list|()
expr_stmt|;
name|fileSets
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|read
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|BuildException
block|{
try|try
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|l
decl_stmt|;
while|while
condition|(
operator|(
name|l
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"IO error while reading XQuery source: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
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
block|}
end_class

end_unit

