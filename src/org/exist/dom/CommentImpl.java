begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  CommentImpl.java  *  *  Created on 14. Mai 2001, 12:09  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|UnsupportedEncodingException
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
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
name|helpers
operator|.
name|AttributesImpl
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  *@author     klaus  *@created    9. Juli 2002  *@version  */
end_comment

begin_class
specifier|public
class|class
name|CommentImpl
extends|extends
name|CharacterDataImpl
implements|implements
name|Comment
block|{
comment|/**      *  Constructor for the CommentImpl object      *      *@param  gid  Description of the Parameter      */
specifier|public
name|CommentImpl
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CommentImpl object      *      *@param  gid   Description of the Parameter      *@param  data  Description of the Parameter      */
specifier|public
name|CommentImpl
parameter_list|(
name|long
name|gid
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|gid
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CommentImpl object      *      *@param  data  Description of the Parameter      */
specifier|public
name|CommentImpl
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CommentImpl object      *      *@param  data     Description of the Parameter      *@param  start    Description of the Parameter      *@param  howmany  Description of the Parameter      */
specifier|public
name|CommentImpl
parameter_list|(
name|char
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
literal|"<!-- "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|cdata
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" -->"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  contentHandler    Description of the Parameter      *@param  lexicalHandler    Description of the Parameter      *@param  first             Description of the Parameter      *@param  prefixes          Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|,
name|boolean
name|first
parameter_list|,
name|ArrayList
name|prefixes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|char
name|data
index|[]
init|=
operator|new
name|char
index|[
name|cdata
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|cdata
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|lexicalHandler
operator|.
name|comment
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
name|byte
index|[]
name|cd
decl_stmt|;
try|try
block|{
name|cd
operator|=
name|cdata
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
name|cd
operator|=
name|cdata
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|cd
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|data
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
name|Signatures
operator|.
name|Comm
operator|<<
literal|0x5
operator|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cd
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|1
argument_list|,
name|cd
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  data  Description of the Parameter      *@return       Description of the Return Value      */
specifier|public
specifier|static
name|NodeImpl
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|String
name|cdata
decl_stmt|;
try|try
block|{
name|cdata
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|1
argument_list|,
name|data
operator|.
name|length
operator|-
literal|1
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
name|cdata
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|1
argument_list|,
name|data
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|CommentImpl
name|comment
init|=
operator|new
name|CommentImpl
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|comment
operator|.
name|appendData
argument_list|(
name|cdata
argument_list|)
expr_stmt|;
return|return
name|comment
return|;
block|}
block|}
end_class

end_unit

