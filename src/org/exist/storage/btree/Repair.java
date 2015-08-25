begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
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
name|indexing
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StructuralIndex
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
name|storage
operator|.
name|NativeBroker
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
name|lock
operator|.
name|Lock
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
name|structural
operator|.
name|NativeStructuralIndexWorker
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
name|FileUtils
import|;
end_import

begin_comment
comment|/**  * Utility to rebuild any of the b+-tree based index files. Scans through all leaf pages to  * reconstruct the inner b+-tree.  */
end_comment

begin_class
specifier|public
class|class
name|Repair
block|{
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|Repair
parameter_list|()
block|{
name|startDB
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|repair
parameter_list|(
name|String
name|id
parameter_list|)
block|{
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
name|BTree
name|btree
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"collections"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|btree
operator|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getStorage
argument_list|(
name|NativeBroker
operator|.
name|COLLECTIONS_DBX_ID
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"dom"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|btree
operator|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getStorage
argument_list|(
name|NativeBroker
operator|.
name|DOM_DBX_ID
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"range"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|btree
operator|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getStorage
argument_list|(
name|NativeBroker
operator|.
name|VALUES_DBX_ID
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"structure"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|NativeStructuralIndexWorker
name|index
init|=
operator|(
name|NativeStructuralIndexWorker
operator|)
name|broker
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexName
argument_list|(
name|StructuralIndex
operator|.
name|STRUCTURAL_INDEX_ID
argument_list|)
decl_stmt|;
name|btree
operator|=
name|index
operator|.
name|getStorage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// use index id defined in conf.xml
name|Index
name|index
init|=
name|pool
operator|.
name|getIndexManager
argument_list|()
operator|.
name|getIndexByName
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|btree
operator|=
name|index
operator|.
name|getStorage
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|btree
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|console
argument_list|()
operator|.
name|printf
argument_list|(
literal|"Unkown index: %s\n"
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Lock
name|lock
init|=
name|btree
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|System
operator|.
name|console
argument_list|()
operator|.
name|printf
argument_list|(
literal|"Rebuilding %15s ..."
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|btree
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|btree
operator|.
name|rebuild
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|console
argument_list|()
operator|.
name|printf
argument_list|(
literal|"An exception occurred during repair: %s\n"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
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
block|}
block|}
specifier|private
name|void
name|startDB
parameter_list|()
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|pool
operator|.
name|shutdown
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nUsage: "
operator|+
name|Repair
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" [index-name]+\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Rebuilds the index files specified as arguments. Can be applied to"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"any of the b+-tree based indexes: collections, dom, structure, ngram-index."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The b+-tree is rebuild by scanning all leaf pages in the .dbx file."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Crash recovery uses the same operation.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Example call to rebuild all indexes:\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Repair
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" dom collections structure ngram-index"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Repair
name|repair
init|=
operator|new
name|Repair
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|repair
operator|.
name|repair
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|repair
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

