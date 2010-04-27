begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|java
operator|.
name|util
operator|.
name|Date
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
name|EXistInputSource
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
name|ZipEntryInputSource
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
name|value
operator|.
name|DateTimeValue
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
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|ZipArchiveBackupDescriptor
extends|extends
name|AbstractBackupDescriptor
block|{
specifier|protected
name|ZipFile
name|archive
decl_stmt|;
specifier|protected
name|ZipEntry
name|descriptor
decl_stmt|;
specifier|protected
name|String
name|base
decl_stmt|;
specifier|public
name|ZipArchiveBackupDescriptor
parameter_list|(
name|File
name|fileArchive
parameter_list|)
throws|throws
name|ZipException
throws|,
name|IOException
throws|,
name|FileNotFoundException
block|{
name|archive
operator|=
operator|new
name|ZipFile
argument_list|(
name|fileArchive
argument_list|)
expr_stmt|;
comment|//is it full backup?
name|base
operator|=
literal|"db/"
expr_stmt|;
name|descriptor
operator|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
expr_stmt|;
if|if
condition|(
name|descriptor
operator|==
literal|null
operator|||
name|descriptor
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|base
operator|=
literal|null
expr_stmt|;
comment|//looking for highest collection
comment|//TODO: better to put some information on top?
name|ZipEntry
name|item
init|=
literal|null
decl_stmt|;
name|Enumeration
name|zipEnum
init|=
name|archive
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|zipEnum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|item
operator|=
operator|(
name|ZipEntry
operator|)
name|zipEnum
operator|.
name|nextElement
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|item
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|item
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
condition|)
block|{
if|if
condition|(
name|base
operator|==
literal|null
operator|||
name|base
operator|.
name|length
argument_list|()
operator|>
name|item
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|descriptor
operator|=
name|item
expr_stmt|;
name|base
operator|=
name|item
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
name|base
operator|=
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|base
operator|.
name|length
argument_list|()
operator|-
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|descriptor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Archive "
operator|+
name|fileArchive
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" is not a valid eXist backup archive"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|ZipArchiveBackupDescriptor
parameter_list|(
name|ZipFile
name|archive
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|this
operator|.
name|archive
operator|=
name|archive
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|descriptor
operator|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
expr_stmt|;
if|if
condition|(
name|descriptor
operator|==
literal|null
operator|||
name|descriptor
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|" is a bit corrupted ("
operator|+
name|base
operator|+
literal|" descriptor not found): not a valid eXist backup archive"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|getContent
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|EXistInputSource
name|is
init|=
name|getInputSource
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|.
name|getByteStreamLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
return|return
name|is
return|;
block|}
specifier|public
name|BackupDescriptor
name|getChildBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
name|BackupDescriptor
name|bd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|archive
argument_list|,
name|base
operator|+
name|describedItem
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// DoNothing(R)
block|}
return|return
name|bd
return|;
block|}
specifier|public
name|BackupDescriptor
name|getBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
if|if
condition|(
name|describedItem
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|describedItem
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
name|describedItem
operator|=
name|describedItem
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|describedItem
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|describedItem
operator|=
name|describedItem
operator|+
literal|'/'
expr_stmt|;
name|BackupDescriptor
name|bd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|archive
argument_list|,
name|describedItem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// DoNothing(R)
block|}
return|return
name|bd
return|;
block|}
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|()
block|{
return|return
operator|new
name|ZipEntryInputSource
argument_list|(
name|archive
argument_list|,
name|descriptor
argument_list|)
return|;
block|}
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|describedItem
argument_list|)
decl_stmt|;
name|EXistInputSource
name|retval
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ze
operator|!=
literal|null
operator|&&
operator|!
name|ze
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|retval
operator|=
operator|new
name|ZipEntryInputSource
argument_list|(
name|archive
argument_list|,
name|ze
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
return|return
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|descriptor
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|(
name|String
name|describedItem
parameter_list|,
name|boolean
name|isChildDescriptor
parameter_list|)
block|{
name|String
name|retval
init|=
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|base
operator|+
name|describedItem
decl_stmt|;
if|if
condition|(
name|isChildDescriptor
condition|)
name|retval
operator|+=
literal|"/"
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
expr_stmt|;
return|return
name|retval
return|;
block|}
specifier|public
name|Properties
name|getProperties
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|BACKUP_PROPERTIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ze
operator|!=
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|archive
operator|.
name|getInputStream
argument_list|(
name|ze
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
specifier|public
name|File
name|getParentDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getParentFile
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

