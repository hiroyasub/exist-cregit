begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Parser
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
name|Collection
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
name|DocumentImpl
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
name|DBBroker
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
name|Document
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    19. August 2002  */
end_comment

begin_class
specifier|public
class|class
name|SecurityManager
block|{
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|ACL_FILE
init|=
literal|"users.xml"
decl_stmt|;
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|DBA_GROUP
init|=
literal|"dba"
decl_stmt|;
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|DBA_USER
init|=
literal|"admin"
decl_stmt|;
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_GROUP
init|=
literal|"guest"
decl_stmt|;
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_USER
init|=
literal|"guest"
decl_stmt|;
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|SecurityManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**  Description of the Field */
specifier|public
specifier|final
specifier|static
name|String
name|SYSTEM
init|=
literal|"/db/system"
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|TreeMap
name|users
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
comment|/**      *  Constructor for the SecurityManager object      *      *@param  pool       Description of the Parameter      *@param  sysBroker  Description of the Parameter      */
specifier|public
name|SecurityManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|sysBroker
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|DBBroker
name|broker
init|=
name|sysBroker
decl_stmt|;
try|try
block|{
name|Collection
name|sysCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysCollection
operator|==
literal|null
condition|)
block|{
name|sysCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|SYSTEM
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|sysCollection
argument_list|)
expr_stmt|;
block|}
name|sysCollection
operator|.
name|setPermissions
argument_list|(
literal|0777
argument_list|)
expr_stmt|;
name|Document
name|acl
init|=
name|broker
operator|.
name|getDocument
argument_list|(
name|SYSTEM
operator|+
literal|'/'
operator|+
name|ACL_FILE
argument_list|)
decl_stmt|;
name|Element
name|docElement
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
name|docElement
operator|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|docElement
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating system users"
argument_list|)
expr_stmt|;
name|User
name|user
init|=
operator|new
name|User
argument_list|(
name|DBA_USER
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|user
operator|.
name|addGroup
argument_list|(
name|DBA_GROUP
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|User
argument_list|(
name|GUEST_USER
argument_list|,
name|GUEST_USER
argument_list|,
name|GUEST_GROUP
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|save
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading acl"
argument_list|)
expr_stmt|;
name|NodeList
name|ul
init|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getElementsByTagName
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|Element
name|node
decl_stmt|;
name|User
name|user
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
name|ul
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
operator|(
name|Element
operator|)
name|ul
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|User
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading acl failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  name  Description of the Parameter      */
specifier|public
specifier|synchronized
name|void
name|deleteUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|users
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
name|users
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
argument_list|()
expr_stmt|;
name|save
argument_list|(
name|broker
argument_list|)
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
comment|/**      *  Gets the user attribute of the SecurityManager object      *      *@param  name  Description of the Parameter      *@return       The user value      */
specifier|public
specifier|synchronized
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|User
operator|)
name|users
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      *  Gets the users attribute of the SecurityManager object      *      *@return    The users value      */
specifier|public
specifier|synchronized
name|User
index|[]
name|getUsers
parameter_list|()
block|{
name|User
name|u
index|[]
init|=
operator|new
name|User
index|[
name|users
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
name|u
index|[
name|j
index|]
operator|=
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|u
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  user  Description of the Parameter      *@return       Description of the Return Value      */
specifier|public
specifier|synchronized
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|User
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|hasGroup
argument_list|(
name|DBA_GROUP
argument_list|)
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  name  Description of the Parameter      *@return       Description of the Return Value      */
specifier|public
specifier|synchronized
name|boolean
name|hasUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|users
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  broker              Description of the Parameter      *@exception  EXistException  Description of the Exception      */
specifier|public
specifier|synchronized
name|void
name|save
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing acl file"
argument_list|)
expr_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<users>"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</users>"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|()
expr_stmt|;
try|try
block|{
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|(
name|broker
argument_list|,
name|getUser
argument_list|(
name|DBA_USER
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
name|parser
operator|.
name|parse
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|SYSTEM
operator|+
literal|'/'
operator|+
name|ACL_FILE
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|doc
operator|.
name|getCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|SAXException
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
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  user  Description of the Parameter      */
specifier|public
specifier|synchronized
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
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
argument_list|()
expr_stmt|;
name|save
argument_list|(
name|broker
argument_list|)
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
block|}
end_class

end_unit

