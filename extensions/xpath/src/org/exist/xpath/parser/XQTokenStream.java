begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*=============================================================================      Copyright 2009 Nikolay Ognyanov      Licensed under the Apache License, Version 2.0 (the "License");     you may not use this file except in compliance with the License.     You may obtain a copy of the License at          http://www.apache.org/licenses/LICENSE-2.0      Unless required by applicable law or agreed to in writing, software     distributed under the License is distributed on an "AS IS" BASIS,     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     See the License for the specific language governing permissions and     limitations under the License.  =============================================================================*/
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|parser
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
name|antlr
operator|.
name|runtime
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|runtime
operator|.
name|TokenSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|runtime
operator|.
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * A replacement for CommonTokenStream. Needed because CommonTokenStream  * is too greedy in consuming tokens from the lexer and therefore does  * not allow for switching of lexers on the fly which is done in {@link Parser}.  */
end_comment

begin_class
specifier|public
class|class
name|XQTokenStream
implements|implements
name|TokenStream
block|{
specifier|private
name|TokenSource
name|tokenSource
decl_stmt|;
specifier|private
name|int
name|channel
init|=
name|Token
operator|.
name|DEFAULT_CHANNEL
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|lastMarker
decl_stmt|;
specifier|private
name|String
index|[]
name|tokenNames
decl_stmt|;
name|boolean
name|spaceBefore
init|=
literal|false
decl_stmt|;
specifier|public
name|XQTokenStream
parameter_list|(
name|TokenSource
name|tokenSource
parameter_list|)
block|{
name|this
operator|.
name|tokenSource
operator|=
name|tokenSource
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TokenSource
name|getTokenSource
parameter_list|()
block|{
return|return
name|tokenSource
return|;
block|}
specifier|public
name|void
name|setTokenSource
parameter_list|(
name|TokenSource
name|tokenSource
parameter_list|)
block|{
name|this
operator|.
name|tokenSource
operator|=
name|tokenSource
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSourceName
parameter_list|()
block|{
return|return
name|tokenSource
operator|.
name|getSourceName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
annotation|@
name|Override
specifier|public
name|Token
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|tokens
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Token
name|LT
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|index
operator|+
name|offset
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|ensureSize
argument_list|(
name|index
operator|+
name|offset
operator|+
literal|1
argument_list|)
condition|)
return|return
name|Token
operator|.
name|EOF_TOKEN
return|;
return|return
name|tokens
operator|.
name|get
argument_list|(
name|index
operator|+
name|offset
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|ensureSize
argument_list|(
name|index
operator|+
name|offset
operator|+
literal|1
argument_list|)
condition|)
return|return
name|Token
operator|.
name|EOF_TOKEN
return|;
return|return
name|tokens
operator|.
name|get
argument_list|(
name|index
operator|+
name|offset
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|LA
parameter_list|(
name|int
name|k
parameter_list|)
block|{
return|return
name|LT
argument_list|(
name|k
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|consume
parameter_list|()
block|{
name|index
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|mark
parameter_list|()
block|{
name|lastMarker
operator|=
name|index
expr_stmt|;
return|return
name|lastMarker
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|(
name|int
name|marker
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|rewind
parameter_list|()
block|{
name|seek
argument_list|(
name|lastMarker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rewind
parameter_list|(
name|int
name|marker
parameter_list|)
block|{
name|seek
argument_list|(
name|marker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|tokens
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|stop
parameter_list|)
block|{
name|ensureSize
argument_list|(
name|stop
argument_list|)
expr_stmt|;
name|int
name|limit
init|=
name|stop
operator|<=
name|size
argument_list|()
condition|?
name|stop
else|:
name|size
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
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
name|start
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|Token
name|start
parameter_list|,
name|Token
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
operator|==
literal|null
operator|||
name|end
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|toString
argument_list|(
name|start
operator|.
name|getTokenIndex
argument_list|()
argument_list|,
name|end
operator|.
name|getTokenIndex
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
index|[]
name|getTokenNames
parameter_list|()
block|{
return|return
name|tokenNames
return|;
block|}
specifier|public
name|void
name|setTokenNames
parameter_list|(
name|String
index|[]
name|tokenNames
parameter_list|)
block|{
name|this
operator|.
name|tokenNames
operator|=
name|tokenNames
expr_stmt|;
block|}
specifier|private
name|boolean
name|ensureSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
name|tokens
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
while|while
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|<
name|size
condition|)
block|{
name|Token
name|nextToken
init|=
name|tokenSource
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
name|Token
operator|.
name|EOF_TOKEN
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nextToken
operator|.
name|getChannel
argument_list|()
operator|==
name|channel
condition|)
block|{
operator|(
operator|(
name|XQToken
operator|)
name|nextToken
operator|)
operator|.
name|spaceBefore
operator|=
name|spaceBefore
expr_stmt|;
name|spaceBefore
operator|=
literal|false
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
comment|/*                 if (tokenNames != null) {                     System.out.println(tokenNames[nextToken.getType()] + "("                             + nextToken.getType() + ") => "                             + nextToken.getText());                 }                 else {                     System.out.println("Token " + nextToken.getType() + " => "                             + nextToken.getText());                 }                 */
block|}
else|else
block|{
name|spaceBefore
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

