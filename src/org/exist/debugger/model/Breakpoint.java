begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|Breakpoint
block|{
comment|/** 	 * break on the given lineno in the given file 	 */
specifier|public
name|String
name|TYPE_LINE
init|=
literal|"line"
decl_stmt|;
comment|/** 	 * break on entry into new stack for function name 	 */
specifier|public
name|String
name|TYPE_CALL
init|=
literal|"call"
decl_stmt|;
comment|/** 	 * break on exit from stack for function name 	 */
specifier|public
name|String
name|TYPE_RETURN
init|=
literal|"return"
decl_stmt|;
comment|/** 	 * break on exception of the given name 	 */
specifier|public
name|String
name|TYPE_EXCEPTION
init|=
literal|"exception"
decl_stmt|;
comment|/** 	 * break when the given expression is true at the given filename and line 	 * number or just in given filename 	 */
specifier|public
name|String
name|TYPE_CONDITIONAL
init|=
literal|"conditional"
decl_stmt|;
comment|/** 	 * break on write of the variable or address defined by the expression 	 * argument 	 */
specifier|public
name|String
name|TYPE_WATCH
init|=
literal|"watch"
decl_stmt|;
specifier|public
name|String
name|getType
parameter_list|()
function_decl|;
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
comment|/** 	 * The file the breakpoint is effective in. This must be a "file://" or "dbgp:" (See 6.7 Dynamic code and virtual files) URI. 	 * @return the source URI 	 */
specifier|public
name|String
name|getFilename
parameter_list|()
function_decl|;
comment|//	public void setFilename(String filename);
comment|/** 	 * Line number on which breakpoint is effective. Line numbers are 1-based. If an implementation requires a numeric value to indicate that lineno is not set, it is suggested that -1 be used, although this is not enforced. 	 * @return line number 	 */
specifier|public
name|Integer
name|getLineno
parameter_list|()
function_decl|;
specifier|public
name|void
name|setLineno
parameter_list|(
name|Integer
name|lineno
parameter_list|)
function_decl|;
comment|/** 	 * Current state of the breakpoint. This must be one of enabled, disabled. 	 * @return current state: true = enabled, false = disabled 	 */
specifier|public
name|boolean
name|getState
parameter_list|()
function_decl|;
specifier|public
name|void
name|setState
parameter_list|(
name|boolean
name|state
parameter_list|)
function_decl|;
comment|/** 	 * Function name for call or return type breakpoints. 	 * @return function name 	 */
specifier|public
name|String
name|getFunction
parameter_list|()
function_decl|;
specifier|public
name|void
name|setFunction
parameter_list|(
name|String
name|function
parameter_list|)
function_decl|;
comment|/** 	 * Flag to define if breakpoint is temporary. A temporary breakpoint is one that is deleted after its first use. This is useful for features like "Run to Cursor". Once the debugger engine uses a temporary breakpoint, it should automatically remove the breakpoint from it's list of valid breakpoints. 	 * @return true if it's temporary 	 */
specifier|public
name|boolean
name|getTemporary
parameter_list|()
function_decl|;
specifier|public
name|void
name|setTemporary
parameter_list|(
name|boolean
name|temporary
parameter_list|)
function_decl|;
comment|/** 	 * Number of effective hits for the breakpoint in the current session. This value is maintained by the debugger engine (a.k.a. DBGP client). A breakpoint's hit count should be increment whenever it is considered to break execution (i.e. whenever debugging comes to this line). If the breakpoint is disabled then the hit count should NOT be incremented. 	 * @return number of effective hits 	 */
specifier|public
name|int
name|getHitCount
parameter_list|()
function_decl|;
specifier|public
name|void
name|setHitCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/** 	 * A numeric value used together with the hit_condition to determine if the breakpoint should pause execution or be skipped. 	 * @return numeric of hit to pause execution 	 */
specifier|public
name|int
name|getHitValue
parameter_list|()
function_decl|;
specifier|public
name|void
name|setHitValue
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
comment|/** 	 * A string indicating a condition to use to compare hit_count and hit_value. The following values are legal:  	 *>= break if hit_count is greater than or equal to hit_value [default]  	 * == break if hit_count is equal to hit_value  	 * %  break if hit_count is a multiple of hit_value 	 *   	 * @return hit condition string 	 */
specifier|public
name|String
name|getHitCondition
parameter_list|()
function_decl|;
specifier|public
name|void
name|setHitCondition
parameter_list|(
name|String
name|condition
parameter_list|)
function_decl|;
comment|/** 	 * Exception name for exception type breakpoints. 	 * @return exception name 	 */
specifier|public
name|String
name|getException
parameter_list|()
function_decl|;
specifier|public
name|void
name|setException
parameter_list|(
name|String
name|exception
parameter_list|)
function_decl|;
specifier|public
name|int
name|getId
parameter_list|()
function_decl|;
specifier|public
name|void
name|setId
parameter_list|(
name|int
name|breakpointNo
parameter_list|)
function_decl|;
specifier|public
name|String
name|toXMLString
parameter_list|()
function_decl|;
comment|//Synchronize changes
specifier|public
name|boolean
name|sync
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|boolean
name|remove
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

