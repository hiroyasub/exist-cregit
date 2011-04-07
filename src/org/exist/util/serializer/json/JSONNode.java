begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|json
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
name|Writer
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|JSONNode
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|ANONYMOUS_OBJECT
init|=
literal|"#anonymous"
decl_stmt|;
specifier|public
specifier|static
enum|enum
name|SerializationType
block|{
name|AS_STRING
block|,
name|AS_ARRAY
block|,
name|AS_LITERAL
block|}
empty_stmt|;
specifier|public
specifier|static
enum|enum
name|Type
block|{
name|OBJECT_TYPE
block|,
name|VALUE_TYPE
block|,
name|SIMPLE_PROPERTY_TYPE
block|}
empty_stmt|;
specifier|private
name|Type
name|type
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|SerializationType
name|writeAs
init|=
name|SerializationType
operator|.
name|AS_STRING
decl_stmt|;
specifier|private
name|JSONNode
name|next
init|=
literal|null
decl_stmt|;
specifier|private
name|JSONNode
name|nextOfSame
init|=
literal|null
decl_stmt|;
specifier|public
name|JSONNode
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|serialize
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|boolean
name|isRoot
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|void
name|serializeContent
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|boolean
name|isNamed
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|!=
name|ANONYMOUS_OBJECT
return|;
block|}
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|getNextOfSame
argument_list|()
operator|!=
literal|null
operator|||
name|getSerializationType
argument_list|()
operator|==
name|SerializationType
operator|.
name|AS_ARRAY
return|;
block|}
specifier|public
name|SerializationType
name|getSerializationType
parameter_list|()
block|{
return|return
name|writeAs
return|;
block|}
specifier|public
name|void
name|setSerializationType
parameter_list|(
name|SerializationType
name|type
parameter_list|)
block|{
name|writeAs
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|JSONNode
name|getNextOfSame
parameter_list|()
block|{
return|return
name|nextOfSame
return|;
block|}
specifier|public
name|void
name|setNextOfSame
parameter_list|(
name|JSONNode
name|nextOfSame
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|nextOfSame
operator|==
literal|null
condition|)
name|this
operator|.
name|nextOfSame
operator|=
name|nextOfSame
expr_stmt|;
else|else
block|{
name|JSONNode
name|current
init|=
name|this
operator|.
name|nextOfSame
decl_stmt|;
while|while
condition|(
name|current
operator|.
name|nextOfSame
operator|!=
literal|null
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|nextOfSame
expr_stmt|;
block|}
name|current
operator|.
name|nextOfSame
operator|=
name|nextOfSame
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setNext
parameter_list|(
name|JSONNode
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
specifier|public
name|JSONNode
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

