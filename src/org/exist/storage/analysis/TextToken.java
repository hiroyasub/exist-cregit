begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|XMLString
import|;
end_import

begin_class
specifier|public
class|class
name|TextToken
implements|implements
name|Comparable
argument_list|<
name|Object
argument_list|>
block|{
specifier|public
specifier|final
specifier|static
name|int
name|ALPHA
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ALPHANUM
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DIGIT
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EOF
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FLOAT
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|LETTER
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NUMBER
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|P
init|=
literal|7
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|WS
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|HOST
init|=
literal|9
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EMAIL
init|=
literal|10
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ACRONYM
init|=
literal|11
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|TextToken
name|WS_TOKEN
init|=
operator|new
name|TextToken
argument_list|(
name|WS
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|TextToken
name|EOF_TOKEN
init|=
operator|new
name|TextToken
argument_list|(
name|EOF
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|types
init|=
block|{
literal|"letter"
block|,
literal|"digit"
block|,
literal|"whitespace"
block|,
literal|"number"
block|,
literal|"alpha"
block|,
literal|"alphanum"
block|,
literal|"p"
block|,
literal|"float"
block|}
decl_stmt|;
specifier|private
name|int
name|end
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|start
init|=
literal|0
decl_stmt|;
specifier|private
name|CharSequence
name|tokenText
decl_stmt|;
specifier|private
name|int
name|tokenType
init|=
name|EOF
decl_stmt|;
specifier|public
name|TextToken
parameter_list|()
block|{
block|}
comment|/**      *  Constructor for the Token object      *      *@param  type  Description of the Parameter      *@param  text  Description of the Parameter      */
specifier|public
name|TextToken
parameter_list|(
name|int
name|type
parameter_list|,
name|CharSequence
name|text
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the Token object      *      *@param  type   Description of the Parameter      *@param  text   Description of the Parameter      *@param  start  Description of the Parameter      */
specifier|public
name|TextToken
parameter_list|(
name|int
name|type
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|text
argument_list|,
name|start
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the Token object      *      *@param  type   Description of the Parameter      *@param  text   Description of the Parameter      *@param  start  Description of the Parameter      *@param  end    Description of the Parameter      */
specifier|public
name|TextToken
parameter_list|(
name|int
name|type
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|tokenType
operator|=
name|type
expr_stmt|;
name|tokenText
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/**      *  Constructor for the Token object      *      *@param  type  Description of the Parameter      */
specifier|public
name|TextToken
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|tokenType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|int
name|type
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|tokenType
operator|=
name|type
expr_stmt|;
name|tokenText
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|start
expr_stmt|;
block|}
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|start
return|;
block|}
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|end
return|;
block|}
specifier|public
name|boolean
name|isAlpha
parameter_list|()
block|{
return|return
name|tokenType
operator|==
name|ALPHA
return|;
block|}
comment|/**      * Consume the next character in the current buffer by incrementing      * the end offset.      */
specifier|public
name|void
name|consumeNext
parameter_list|()
block|{
name|end
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|consume
parameter_list|(
name|TextToken
name|token
parameter_list|)
block|{
name|this
operator|.
name|end
operator|=
name|token
operator|.
name|end
expr_stmt|;
block|}
specifier|public
name|char
name|getChar
parameter_list|()
block|{
return|return
name|tokenText
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
return|;
block|}
specifier|public
name|CharSequence
name|getCharSequence
parameter_list|()
block|{
if|if
condition|(
name|start
operator|>=
name|tokenText
operator|.
name|length
argument_list|()
operator|||
name|end
operator|>
name|tokenText
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|(
literal|"start: "
operator|+
name|start
operator|+
literal|"; end="
operator|+
name|end
operator|+
literal|"; text="
operator|+
name|tokenText
argument_list|)
throw|;
return|return
name|tokenText
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
if|if
condition|(
name|start
operator|>=
name|tokenText
operator|.
name|length
argument_list|()
operator|||
name|end
operator|>
name|tokenText
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|(
literal|"start: "
operator|+
name|start
operator|+
literal|"; end="
operator|+
name|end
operator|+
literal|"; text="
operator|+
name|tokenText
argument_list|)
throw|;
if|if
condition|(
name|tokenText
operator|instanceof
name|XMLString
condition|)
return|return
operator|(
operator|(
name|XMLString
operator|)
name|tokenText
operator|)
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
return|return
name|tokenText
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|tokenType
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|tokenType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|tokenText
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|end
operator|-
name|start
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|tokenText
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|String
name|other
init|=
name|obj
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|len
operator|==
name|other
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|j
init|=
name|start
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tokenText
operator|.
name|charAt
argument_list|(
name|j
operator|++
argument_list|)
operator|!=
name|other
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|getText
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getText
argument_list|()
return|;
block|}
block|}
end_class

end_unit

