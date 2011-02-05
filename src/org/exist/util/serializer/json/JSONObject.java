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
name|StringWriter
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
class|class
name|JSONObject
extends|extends
name|JSONNode
block|{
specifier|private
name|JSONNode
name|firstChild
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|asSimpleValue
init|=
literal|false
decl_stmt|;
specifier|public
name|JSONObject
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|OBJECT_TYPE
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JSONObject
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|asSimpleValue
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|OBJECT_TYPE
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|asSimpleValue
operator|=
name|asSimpleValue
expr_stmt|;
block|}
specifier|public
name|void
name|addObject
parameter_list|(
name|JSONNode
name|node
parameter_list|)
block|{
name|JSONNode
name|childNode
init|=
name|findChild
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|childNode
operator|==
literal|null
condition|)
block|{
name|childNode
operator|=
name|getLastChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|childNode
operator|==
literal|null
condition|)
name|firstChild
operator|=
name|node
expr_stmt|;
else|else
name|childNode
operator|.
name|setNext
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
name|childNode
operator|.
name|setNextOfSame
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JSONNode
name|findChild
parameter_list|(
name|String
name|nameToFind
parameter_list|)
block|{
name|JSONNode
name|nextNode
init|=
name|firstChild
decl_stmt|;
while|while
condition|(
name|nextNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextNode
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|nameToFind
argument_list|)
condition|)
return|return
name|nextNode
return|;
name|nextNode
operator|=
name|nextNode
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|JSONNode
name|getLastChild
parameter_list|()
block|{
name|JSONNode
name|nextNode
init|=
name|firstChild
decl_stmt|;
name|JSONNode
name|currentNode
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|nextNode
operator|!=
literal|null
condition|)
block|{
name|currentNode
operator|=
name|nextNode
expr_stmt|;
name|nextNode
operator|=
name|currentNode
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|currentNode
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|JSONNode
name|nextNode
init|=
name|firstChild
decl_stmt|;
while|while
condition|(
name|nextNode
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|nextNode
operator|=
name|nextNode
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|public
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
block|{
if|if
condition|(
operator|!
operator|(
name|isRoot
operator|||
name|asSimpleValue
operator|)
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|JSONNode
name|next
init|=
name|this
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|serializeContent
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNextOfSame
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
name|serializeContent
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serializeContent
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstChild
operator|==
literal|null
condition|)
comment|// an empty node gets a null value
name|writer
operator|.
name|write
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|firstChild
operator|.
name|getNext
argument_list|()
operator|==
literal|null
operator|&&
name|firstChild
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|VALUE_TYPE
condition|)
comment|// if there's only one child and if it is text, it is serialized as simple value
name|firstChild
operator|.
name|serialize
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
else|else
block|{
comment|// complex object
if|if
condition|(
operator|!
name|asSimpleValue
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
name|boolean
name|allowText
init|=
literal|false
decl_stmt|;
name|JSONNode
name|next
init|=
name|firstChild
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|VALUE_TYPE
condition|)
block|{
comment|// if an element has attributes and text content, the text
comment|// node is serialized as property "#text". Text in mixed content nodes
comment|// is ignored though.
if|if
condition|(
name|allowText
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"\"#text\" : "
argument_list|)
expr_stmt|;
name|next
operator|.
name|serialize
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|allowText
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
name|next
operator|.
name|serialize
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|SIMPLE_PROPERTY_TYPE
condition|)
name|allowText
operator|=
literal|true
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|asSimpleValue
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|serialize
argument_list|(
name|writer
argument_list|,
literal|false
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
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

