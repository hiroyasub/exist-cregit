begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|actions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|AbstractAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Connection
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|modules
operator|.
name|XUpdateQueryService
import|;
end_import

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: wolf  * Date: 05.02.2007  * Time: 12:21:01  * To change this template use File | Settings | File Templates.  */
end_comment

begin_class
specifier|public
class|class
name|XUpdateAction
extends|extends
name|AbstractAction
block|{
specifier|private
name|String
name|collectionPath
decl_stmt|;
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|xupdate
decl_stmt|;
specifier|private
name|int
name|modifications
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Runner
name|runner
parameter_list|,
name|Action
name|parent
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|configure
argument_list|(
name|runner
argument_list|,
name|parent
argument_list|,
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"collection"
argument_list|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
name|StoreFromFile
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" requires an attribute 'collection'"
argument_list|)
throw|;
name|collectionPath
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"resource"
argument_list|)
condition|)
name|resource
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"resource"
argument_list|)
expr_stmt|;
name|xupdate
operator|=
name|getContent
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|EXistException
block|{
name|Collection
name|collection
init|=
name|connection
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"collection "
operator|+
name|collectionPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|modifications
operator|=
operator|(
name|int
operator|)
name|service
operator|.
name|update
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|modifications
operator|=
operator|(
name|int
operator|)
name|service
operator|.
name|updateResource
argument_list|(
name|resource
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getLastResult
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|modifications
argument_list|)
return|;
block|}
block|}
end_class

end_unit

