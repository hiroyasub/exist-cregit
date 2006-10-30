begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|Project
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
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * an ant task to extract the content of a collection or resource  *  * @author peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBExtractTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|destFile
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|destDir
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|type
init|=
literal|"xml"
decl_stmt|;
specifier|private
name|boolean
name|createdirectories
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|subcollections
init|=
literal|false
decl_stmt|;
comment|/* (non-Javadoc)    * @see org.apache.tools.ant.Task#execute()    */
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
block|}
else|else
block|{
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|Collection
name|base
init|=
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
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Collection "
operator|+
name|uri
operator|+
literal|" not found."
argument_list|)
throw|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Extracting resource: "
operator|+
name|resource
operator|+
literal|" to "
operator|+
name|destFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|base
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Resource "
operator|+
name|resource
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
else|else
block|{
name|writeResource
argument_list|(
name|res
argument_list|,
name|destFile
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|extractResources
argument_list|(
name|base
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|subcollections
condition|)
block|{
name|extractSubCollections
argument_list|(
name|base
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught while executing query: "
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|failonerror
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught while writing destination file: "
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
specifier|private
name|void
name|extractResources
parameter_list|(
name|Collection
name|base
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|XMLResource
name|res
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|resources
init|=
name|base
operator|.
name|listResources
argument_list|()
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
condition|)
block|{
name|File
name|dir
init|=
name|destDir
decl_stmt|;
name|log
argument_list|(
literal|"Extracting to directory "
operator|+
name|destDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
operator|=
operator|(
name|XMLResource
operator|)
name|base
operator|.
name|getResource
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Extracting resource: "
operator|+
name|res
operator|.
name|getId
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
operator|&&
name|createdirectories
condition|)
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|writeResource
argument_list|(
name|res
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|extractSubCollections
parameter_list|(
name|Collection
name|base
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|String
index|[]
name|childCols
init|=
name|base
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
if|if
condition|(
name|childCols
operator|!=
literal|null
condition|)
block|{
name|Collection
name|col
init|=
literal|null
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
name|childCols
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|col
operator|=
name|base
operator|.
name|getChildCollection
argument_list|(
name|childCols
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Extracting collection: "
operator|+
name|col
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
name|destDir
decl_stmt|;
name|String
name|subdir
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|destDir
argument_list|,
name|path
operator|+
name|File
operator|.
name|separator
operator|+
name|childCols
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|subdir
operator|=
name|path
operator|+
name|File
operator|.
name|separator
operator|+
name|childCols
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|subdir
operator|=
name|childCols
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
operator|&&
name|createdirectories
condition|)
block|{
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|extractResources
argument_list|(
name|col
argument_list|,
name|subdir
argument_list|)
expr_stmt|;
if|if
condition|(
name|subcollections
condition|)
block|{
name|extractSubCollections
argument_list|(
name|col
argument_list|,
name|subdir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|writeResource
parameter_list|(
name|XMLResource
name|resource
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLDBException
block|{
if|if
condition|(
name|dest
operator|!=
literal|null
operator|||
operator|!
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
name|fname
init|=
name|resource
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fname
operator|.
name|endsWith
argument_list|(
literal|"."
operator|+
name|type
argument_list|)
condition|)
block|{
name|fname
operator|+=
literal|"."
operator|+
name|type
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dest
argument_list|,
name|fname
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|=
operator|new
name|FileWriter
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Writing resource "
operator|+
name|resource
operator|.
name|getId
argument_list|()
operator|+
literal|" to destination "
operator|+
name|dest
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Destionation target does not exist."
argument_list|)
throw|;
block|}
block|}
comment|/**    * @param resource    */
specifier|public
name|void
name|setResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
specifier|public
name|void
name|setDestFile
parameter_list|(
name|File
name|destFile
parameter_list|)
block|{
name|this
operator|.
name|destFile
operator|=
name|destFile
expr_stmt|;
block|}
specifier|public
name|void
name|setDestDir
parameter_list|(
name|File
name|destDir
parameter_list|)
block|{
name|this
operator|.
name|destDir
operator|=
name|destDir
expr_stmt|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
operator|!
literal|"xml"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"non-xml resource types are not supported currently"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setCreatedirectories
parameter_list|(
name|boolean
name|createdirectories
parameter_list|)
block|{
name|this
operator|.
name|createdirectories
operator|=
name|createdirectories
expr_stmt|;
block|}
specifier|public
name|void
name|setSubcollections
parameter_list|(
name|boolean
name|subcollections
parameter_list|)
block|{
name|this
operator|.
name|subcollections
operator|=
name|subcollections
expr_stmt|;
block|}
block|}
end_class

end_unit

