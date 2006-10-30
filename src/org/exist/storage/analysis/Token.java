begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. Token.java Version 2.1 */
end_comment

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

begin_comment
comment|/**  * Describes the input token stream.  */
end_comment

begin_class
specifier|public
class|class
name|Token
block|{
comment|/**    * An integer that describes the kind of this token.  This numbering    * system is determined by JavaCCParser, and a table of these numbers is    * stored in the file ...Constants.java.    */
specifier|public
name|int
name|kind
decl_stmt|;
comment|/**    * beginLine and beginColumn describe the position of the first character    * of this token; endLine and endColumn describe the position of the    * last character of this token.    */
specifier|public
name|int
name|beginLine
decl_stmt|;
comment|/**    * beginLine and beginColumn describe the position of the first character    * of this token; endLine and endColumn describe the position of the    * last character of this token.    */
specifier|public
name|int
name|beginColumn
decl_stmt|;
comment|/**    * beginLine and beginColumn describe the position of the first character    * of this token; endLine and endColumn describe the position of the    * last character of this token.    */
specifier|public
name|int
name|endLine
decl_stmt|;
comment|/**    * beginLine and beginColumn describe the position of the first character    * of this token; endLine and endColumn describe the position of the    * last character of this token.    */
specifier|public
name|int
name|endColumn
decl_stmt|;
comment|/**    * The string image of the token.    */
specifier|public
name|String
name|image
decl_stmt|;
comment|/**    * A reference to the next regular (non-special) token from the input    * stream.  If this is the last token from the input stream, or if the    * token manager has not read tokens beyond this one, this field is    * set to null.  This is true only if this token is also a regular    * token.  Otherwise, see below for a description of the contents of    * this field.    */
specifier|public
name|Token
name|next
decl_stmt|;
comment|/**    * This field is used to access special tokens that occur prior to this    * token, but after the immediately preceding regular (non-special) token.    * If there are no such special tokens, this field is set to null.    * When there are more than one such special token, this field refers    * to the last of these special tokens, which in turn refers to the next    * previous special token through its specialToken field, and so on    * until the first special token (whose specialToken field is null).    * The next fields of special tokens refer to other special tokens that    * immediately follow it (without an intervening regular token).  If there    * is no such token, this field is null.    */
specifier|public
name|Token
name|specialToken
decl_stmt|;
comment|/**    * Returns the image.    */
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|image
return|;
block|}
comment|/**    * Returns a new Token object, by default. However, if you want, you    * can create and return subclass objects based on the value of ofKind.    * Simply add the cases to the switch for all those special cases.    * For example, if you have a subclass of Token called IDToken that    * you want to create if ofKind is ID, simlpy add something like :    *    *    case MyParserConstants.ID : return new IDToken();    *    * to the following switch statement. Then you can cast matchedToken    * variable to the appropriate type and use it in your lexical actions.    */
specifier|public
specifier|static
specifier|final
name|Token
name|newToken
parameter_list|(
name|int
name|ofKind
parameter_list|)
block|{
switch|switch
condition|(
name|ofKind
condition|)
block|{
default|default :
return|return
operator|new
name|Token
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

