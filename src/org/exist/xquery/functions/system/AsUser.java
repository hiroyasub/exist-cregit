begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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
name|*
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
name|Item
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
name|Sequence
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
name|SequenceType
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
name|Type
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|AsUser
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"as-user"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"A pseudo-function to execute a limited block of code as a different "
operator|+
literal|"user. The first argument is the name of the user, the second is the "
operator|+
literal|"password. If the user can be authenticated, the function will execute the "
operator|+
literal|"code block given in the third argument with the permissions of that user and"
operator|+
literal|"returns the result of the execution. Before the function completes, it switches "
operator|+
literal|"the current user back to the old user."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|AsUser
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|userSeq
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|passwdSeq
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|userSeq
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"No user specified"
argument_list|)
throw|;
name|String
name|userName
init|=
name|userSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|passwd
init|=
name|passwdSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
name|security
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|security
operator|.
name|getUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Authentication failed"
argument_list|)
throw|;
if|if
condition|(
name|user
operator|.
name|validate
argument_list|(
name|passwd
argument_list|)
condition|)
block|{
name|User
name|oldUser
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|setUser
argument_list|(
name|oldUser
argument_list|)
expr_stmt|;
block|}
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Authentication failed"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsType
argument_list|()
return|;
block|}
block|}
end_class

end_unit

