begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
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
name|Resource
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
name|CollectionScanner
block|{
specifier|public
specifier|final
specifier|static
name|Resource
index|[]
name|scan
parameter_list|(
name|Collection
name|current
parameter_list|,
name|String
name|vpath
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|List
argument_list|<
name|Resource
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
name|scan
argument_list|(
name|list
argument_list|,
name|current
argument_list|,
name|vpath
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
name|Resource
name|resources
index|[]
init|=
operator|new
name|Resource
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|Resource
index|[]
operator|)
name|list
operator|.
name|toArray
argument_list|(
name|resources
argument_list|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|void
name|scan
parameter_list|(
name|List
argument_list|<
name|Resource
argument_list|>
name|list
parameter_list|,
name|Collection
name|current
parameter_list|,
name|String
name|vpath
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
index|[]
name|resources
init|=
name|current
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|String
name|name
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
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|name
operator|=
name|vpath
operator|+
name|resources
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"checking "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|DirectoryScanner
operator|.
name|match
argument_list|(
name|pattern
argument_list|,
name|name
argument_list|)
condition|)
name|list
operator|.
name|add
argument_list|(
name|current
operator|.
name|getResource
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|subs
init|=
name|current
operator|.
name|listChildCollections
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|name
operator|=
name|vpath
operator|+
name|subs
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"checking "
operator|+
name|name
operator|+
literal|" = "
operator|+
name|pattern
argument_list|)
expr_stmt|;
if|if
condition|(
name|DirectoryScanner
operator|.
name|matchStart
argument_list|(
name|pattern
argument_list|,
name|name
argument_list|)
condition|)
comment|///TODO : use dedicated function in XmldbURI
name|scan
argument_list|(
name|list
argument_list|,
name|current
operator|.
name|getChildCollection
argument_list|(
name|subs
index|[
name|i
index|]
argument_list|)
argument_list|,
name|name
operator|+
literal|"/"
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

