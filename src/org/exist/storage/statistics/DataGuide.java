begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|statistics
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
name|dom
operator|.
name|SymbolTable
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
name|ElementValue
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
name|NodePath
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

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

begin_comment
comment|/**  * Collects statistics about the distribution of elements in a document or  * even the entire database. The class creates a graph structure which describes  * all possible element paths and their frequency. For example, for a TEI document, a typical  * path could be:  *  *<pre>TEI[44,63330] -> text[44,62757] -> body[44,44206] -> div[300,5584] -> p[5336,820]</pre>  *  * which means there are 44 TEI, text and body elements in the db with 300 div children and  * 5336 paragraphs below them. The second number indicates the size of the largest element,  * expressed as the number of descendant elements below the node. The largest p node in this  * distribution has 820 elements below it.  */
end_comment

begin_class
specifier|public
class|class
name|DataGuide
block|{
specifier|private
specifier|final
specifier|static
name|int
name|BYTES_PER_NODE
init|=
literal|16
decl_stmt|;
comment|// the (virtual) root of the tree whose name will always be null.
specifier|private
name|NodeStats
name|root
init|=
operator|new
name|NodeStatsRoot
argument_list|()
decl_stmt|;
specifier|public
name|DataGuide
parameter_list|()
block|{
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|root
operator|.
name|getSize
argument_list|()
return|;
block|}
comment|/**      * Add the given node path (a path like /root/childA/childB) to the data guide.      * The frequency for the target element (i.e. the last component in the path)      * is incremented by one.      *       * @param path      * @return      */
specifier|public
name|NodeStats
name|add
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Add the given node path using the frequency and size information      * given in the second argument. Used to merge two DataGuides.      *      * @param path      * @param mergeWith      * @return      */
specifier|protected
name|NodeStats
name|add
parameter_list|(
name|NodePath
name|path
parameter_list|,
name|NodeStats
name|mergeWith
parameter_list|)
block|{
name|NodeStats
name|current
init|=
name|root
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
name|path
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|QName
name|qn
init|=
name|path
operator|.
name|getComponent
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|qn
operator|.
name|getNameType
argument_list|()
operator|!=
name|ElementValue
operator|.
name|ELEMENT
condition|)
block|{
return|return
literal|null
return|;
block|}
name|current
operator|=
name|current
operator|.
name|addChild
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeWith
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|mergeStats
argument_list|(
name|mergeWith
argument_list|)
expr_stmt|;
block|}
else|else
name|current
operator|.
name|addOccurrence
argument_list|()
expr_stmt|;
return|return
name|current
return|;
block|}
comment|/**      * Merge paths and statistics from this instance into the      * other instance.      *      * @param other      * @return the other instance containing the merged graphs      */
specifier|public
name|DataGuide
name|mergeInto
parameter_list|(
name|DataGuide
name|other
parameter_list|)
block|{
name|root
operator|.
name|mergeInto
argument_list|(
name|other
argument_list|,
operator|new
name|NodePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|other
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|List
name|paths
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|root
operator|.
name|dump
argument_list|(
operator|new
name|StringBuffer
argument_list|()
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
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
name|paths
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|paths
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|FileChannel
name|fc
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nodeCount
init|=
name|root
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"childCount = "
operator|+
name|nodeCount
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|nodeCount
operator|*
name|BYTES_PER_NODE
operator|+
literal|4
argument_list|)
decl_stmt|;
name|root
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|fc
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|FileChannel
name|fc
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
operator|(
name|int
operator|)
name|fc
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|fc
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|root
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|NodeStatsRoot
extends|extends
name|NodeStats
block|{
specifier|private
name|NodeStatsRoot
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|write
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
name|buffer
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
block|{
name|buffer
operator|.
name|putInt
argument_list|(
name|children
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|read
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
name|int
name|childCount
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|childCount
operator|>
literal|0
condition|)
block|{
name|children
operator|=
operator|new
name|NodeStats
index|[
name|childCount
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|childCount
condition|;
name|i
operator|++
control|)
block|{
name|children
index|[
name|i
index|]
operator|=
operator|new
name|NodeStats
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|children
index|[
name|i
index|]
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|NodePath
name|createPath
parameter_list|(
name|String
index|[]
name|tags
parameter_list|)
block|{
name|NodePath
name|p
init|=
operator|new
name|NodePath
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
name|tags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|tag
init|=
name|tags
index|[
name|i
index|]
decl_stmt|;
name|p
operator|.
name|addComponent
argument_list|(
operator|new
name|QName
argument_list|(
name|tag
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|DataGuide
name|guide
init|=
operator|new
name|DataGuide
argument_list|()
decl_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"head"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"head"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"p"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"p"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"hi"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"hi"
block|,
literal|"hi"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"backmatter"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|guide
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DataGuide
name|guide2
init|=
operator|new
name|DataGuide
argument_list|()
decl_stmt|;
name|guide2
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"head"
block|,
literal|"hi"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide2
operator|.
name|add
argument_list|(
name|createPath
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"root"
block|,
literal|"body"
block|,
literal|"section"
block|,
literal|"p"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|guide
operator|.
name|mergeInto
argument_list|(
name|guide2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|guide2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

