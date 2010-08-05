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
comment|/**  * An iterator over a sequence of unboxed int values  *   * Copied from Saxon-HE 9.2 package net.sf.saxon.regex.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IntIterator
block|{
comment|/**      * Test whether there are any more integers in the sequence      * @return true if there are more integers to come      */
specifier|public
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**      * Return the next integer in the sequence. The result is undefined unless hasNext() has been called      * and has returned true.      * @return the next integer in the sequence      */
specifier|public
name|int
name|next
parameter_list|()
function_decl|;
block|}
end_interface

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
comment|// The Initial Developer of the Original Code is Michael H. Kay.
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

