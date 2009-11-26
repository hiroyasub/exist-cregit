begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|DataFlavor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|Transferable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|UnsupportedFlavorException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|MimeType
import|;
end_import

begin_class
specifier|public
class|class
name|NodeTransferable
implements|implements
name|Transferable
block|{
specifier|private
specifier|static
specifier|final
name|String
name|FLAVOR_DESCRIPTION
init|=
literal|"XACML Element"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|DataFlavor
argument_list|>
name|CLASS_TO_FLAVOR
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|DataFlavor
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|CONDITION_FLAVOR
init|=
name|createFlavor
argument_list|(
name|ConditionNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|TARGET_FLAVOR
init|=
name|createFlavor
argument_list|(
name|TargetNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|RULE_FLAVOR
init|=
name|createFlavor
argument_list|(
name|RuleNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|POLICY_FLAVOR
init|=
name|createFlavor
argument_list|(
name|PolicyNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|POLICY_SET_FLAVOR
init|=
name|createFlavor
argument_list|(
name|PolicySetNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|ABSTRACT_POLICY_FLAVOR
init|=
name|createFlavor
argument_list|(
name|AbstractPolicyNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|POLICY_ELEMENT_FLAVOR
init|=
name|createFlavor
argument_list|(
name|PolicyElementNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|ROOT_FLAVOR
init|=
name|createFlavor
argument_list|(
name|RootNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|ABSTRACT_NODE_FLAVOR
init|=
name|createFlavor
argument_list|(
name|AbstractTreeNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|NODE_FLAVOR
init|=
name|createFlavor
argument_list|(
name|XACMLTreeNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|TEXT_XML_FLAVOR
init|=
operator|new
name|DataFlavor
argument_list|(
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
argument_list|,
name|FLAVOR_DESCRIPTION
operator|+
literal|" (XML)"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DataFlavor
name|APPLICATION_XML_FLAVOR
init|=
operator|new
name|DataFlavor
argument_list|(
literal|"application/xml"
argument_list|,
name|FLAVOR_DESCRIPTION
operator|+
literal|" (XML)"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|DataFlavor
name|createFlavor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
name|DataFlavor
name|ret
init|=
operator|new
name|DataFlavor
argument_list|(
name|c
argument_list|,
name|FLAVOR_DESCRIPTION
argument_list|)
decl_stmt|;
name|CLASS_TO_FLAVOR
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|private
name|Set
argument_list|<
name|DataFlavor
argument_list|>
name|supportedFlavors
decl_stmt|;
specifier|private
name|XACMLTreeNode
name|node
decl_stmt|;
specifier|public
name|NodeTransferable
parameter_list|(
name|XACMLTreeNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|supportedFlavors
operator|=
operator|new
name|LinkedHashSet
argument_list|<
name|DataFlavor
argument_list|>
argument_list|()
expr_stmt|;
name|supportedFlavors
operator|.
name|add
argument_list|(
name|TEXT_XML_FLAVOR
argument_list|)
expr_stmt|;
name|supportedFlavors
operator|.
name|add
argument_list|(
name|APPLICATION_XML_FLAVOR
argument_list|)
expr_stmt|;
name|supportedFlavors
operator|.
name|add
argument_list|(
name|DataFlavor
operator|.
name|stringFlavor
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|node
operator|.
name|getClass
argument_list|()
init|;
name|c
operator|!=
literal|null
condition|;
name|c
operator|=
name|c
operator|.
name|getSuperclass
argument_list|()
control|)
block|{
name|DataFlavor
name|flavor
init|=
name|CLASS_TO_FLAVOR
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|flavor
operator|!=
literal|null
condition|)
name|supportedFlavors
operator|.
name|add
argument_list|(
name|flavor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|DataFlavor
index|[]
name|getTransferDataFlavors
parameter_list|()
block|{
name|DataFlavor
index|[]
name|ret
init|=
operator|new
name|DataFlavor
index|[
name|supportedFlavors
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|supportedFlavors
operator|.
name|toArray
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|boolean
name|isDataFlavorSupported
parameter_list|(
name|DataFlavor
name|flavor
parameter_list|)
block|{
return|return
name|supportedFlavors
operator|.
name|contains
argument_list|(
name|flavor
argument_list|)
return|;
block|}
specifier|public
name|Object
name|getTransferData
parameter_list|(
name|DataFlavor
name|flavor
parameter_list|)
throws|throws
name|UnsupportedFlavorException
throws|,
name|IOException
block|{
if|if
condition|(
name|XACMLTreeNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|flavor
operator|.
name|getRepresentationClass
argument_list|()
argument_list|)
condition|)
return|return
name|node
return|;
if|if
condition|(
name|DataFlavor
operator|.
name|stringFlavor
operator|.
name|equals
argument_list|(
name|flavor
argument_list|)
condition|)
return|return
name|node
operator|.
name|serialize
argument_list|(
literal|true
argument_list|)
return|;
if|if
condition|(
name|TEXT_XML_FLAVOR
operator|.
name|equals
argument_list|(
name|flavor
argument_list|)
condition|)
return|return
name|serialize
argument_list|(
literal|true
argument_list|)
return|;
if|if
condition|(
name|APPLICATION_XML_FLAVOR
operator|.
name|equals
argument_list|(
name|flavor
argument_list|)
condition|)
return|return
name|serialize
argument_list|(
literal|false
argument_list|)
return|;
throw|throw
operator|new
name|UnsupportedFlavorException
argument_list|(
name|flavor
argument_list|)
throw|;
block|}
specifier|private
name|InputStream
name|serialize
parameter_list|(
name|boolean
name|indent
parameter_list|)
block|{
name|String
name|serializedString
init|=
name|node
operator|.
name|serialize
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|serializedString
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

