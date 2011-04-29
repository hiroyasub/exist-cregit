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
class|class
name|JSONValue
extends|extends
name|JSONNode
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAME_VALUE
init|=
literal|"#text"
decl_stmt|;
specifier|private
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|public
name|JSONValue
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|VALUE_TYPE
argument_list|,
name|NAME_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|escape
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JSONValue
parameter_list|()
block|{
name|super
argument_list|(
name|Type
operator|.
name|VALUE_TYPE
argument_list|,
name|NAME_VALUE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addContent
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|content
operator|==
literal|null
condition|)
name|content
operator|=
name|str
expr_stmt|;
else|else
name|content
operator|+=
name|str
expr_stmt|;
block|}
annotation|@
name|Override
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
name|getNextOfSame
argument_list|()
operator|!=
literal|null
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
annotation|@
name|Override
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
name|getSerializationType
argument_list|()
operator|!=
name|SerializationType
operator|.
name|AS_LITERAL
condition|)
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
name|content
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSerializationType
argument_list|()
operator|!=
name|SerializationType
operator|.
name|AS_LITERAL
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'\n'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"\\n"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
break|break;
case|case
literal|'\t'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"\\t"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
break|break;
default|default:
name|builder
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

