begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|regex
package|;
end_package

begin_comment
comment|/**  * Thrown when an syntactically incorrect regular expression is detected.  *   * Copied from Saxon-HE 9.2 package net.sf.saxon.regex without change.  */
end_comment

begin_class
specifier|public
class|class
name|RegexSyntaxException
extends|extends
name|Exception
block|{
specifier|private
specifier|final
name|int
name|position
decl_stmt|;
comment|/**      * Represents an unknown position within a string containing a regular expression.      */
specifier|public
specifier|static
specifier|final
name|int
name|UNKNOWN_POSITION
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|RegexSyntaxException
parameter_list|(
name|String
name|detail
parameter_list|)
block|{
name|this
argument_list|(
name|detail
argument_list|,
name|UNKNOWN_POSITION
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RegexSyntaxException
parameter_list|(
name|String
name|detail
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|super
argument_list|(
name|detail
argument_list|)
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
comment|/**      * Returns the index into the regular expression where the error was detected      * or<code>UNKNOWN_POSITION</code> if this is unknown.      *      * @return the index into the regular expression where the error was detected,      * or<code>UNKNOWNN_POSITION</code> if this is unknown      */
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
block|}
end_class

begin_comment
comment|//
end_comment

begin_comment
comment|// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License. You may obtain a copy of the
end_comment

begin_comment
comment|// License at http://www.mozilla.org/MPL/
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Software distributed under the License is distributed on an "AS IS" basis,
end_comment

begin_comment
comment|// WITHOUT WARRANTY OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing rights and limitations under the License.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Original Code is: all this file.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Initial Developer of the Original Code is Michael H. Kay
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Contributor(s): none.
end_comment

begin_comment
comment|//
end_comment

end_unit

