begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|structural
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
name|SymbolTable
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
name|AbstractIndex
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
name|IndexWorker
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
name|btree
operator|.
name|DBException
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
name|index
operator|.
name|BFile
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
name|index
operator|.
name|BTreeStore
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
name|LockException
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
specifier|public
class|class
name|NativeStructuralIndex
extends|extends
name|AbstractIndex
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NativeStructuralIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|NativeStructuralIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"structure.dbx"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FILE_KEY_IN_CONFIG
init|=
literal|"db-connection.elements"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_STRUCTURAL_CACHE_GROWTH
init|=
literal|1.25
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_STRUCTURAL_KEY_THRESHOLD
init|=
literal|0.01
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_STRUCTURAL_VALUE_THRESHOLD
init|=
literal|0.04
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|STRUCTURAL_INDEX_ID
init|=
literal|1
decl_stmt|;
comment|/** The datastore for this node index */
specifier|protected
name|BTreeStore
name|btree
decl_stmt|;
specifier|protected
name|SymbolTable
name|symbols
decl_stmt|;
specifier|public
name|NativeStructuralIndex
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|super
operator|.
name|configure
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|symbols
operator|=
name|pool
operator|.
name|getSymbols
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getDataDir
argument_list|()
argument_list|,
name|FILE_NAME
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating '"
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
try|try
block|{
name|btree
operator|=
operator|new
name|BTreeStore
argument_list|(
name|pool
argument_list|,
name|STRUCTURAL_INDEX_ID
argument_list|,
literal|false
argument_list|,
name|file
argument_list|,
name|pool
operator|.
name|getCacheManager
argument_list|()
argument_list|,
name|DEFAULT_STRUCTURAL_KEY_THRESHOLD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize structural index: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
name|btree
operator|.
name|close
argument_list|()
expr_stmt|;
name|btree
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
if|if
condition|(
name|btree
operator|==
literal|null
condition|)
return|return;
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
name|btree
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock for '"
operator|+
name|btree
operator|.
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception ? -pb
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception ? -pb
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
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|btree
operator|.
name|closeAndRemove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|NativeStructuralIndexWorker
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
literal|false
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
block|}
end_class

end_unit

