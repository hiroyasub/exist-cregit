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
name|debuggee
operator|.
name|dbgp
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|Errors
block|{
comment|// 000 Command parsing errors
comment|/** 	 * no error 	 */
specifier|public
name|int
name|ERR_0
init|=
literal|0
decl_stmt|;
comment|/** 	 * parse error in command 	 */
specifier|public
name|int
name|ERR_1
init|=
literal|1
decl_stmt|;
comment|/** 	 * duplicate arguments in command 	 */
specifier|public
name|int
name|ERR_2
init|=
literal|2
decl_stmt|;
comment|/** 	 * invalid options (ie, missing a required option, invalid value for a 	 * passed option) 	 */
specifier|public
name|int
name|ERR_3
init|=
literal|3
decl_stmt|;
comment|/** 	 * Unimplemented command 	 */
specifier|public
name|int
name|ERR_4
init|=
literal|4
decl_stmt|;
comment|/** 	 * Command not available (Is used for async commands. For instance if the 	 * engine is in state "run" then only "break" and "status" are available). 	 */
specifier|public
name|int
name|ERR_5
init|=
literal|5
decl_stmt|;
comment|// 100 File related errors
comment|/** 	 * can not open file (as a reply to a "source" command if the requested 	 * source file can't be opened) 	 */
specifier|public
name|int
name|ERR_100
init|=
literal|100
decl_stmt|;
specifier|public
name|String
name|ERR_100_STR
init|=
literal|"can not open file (as a reply to a \"source\" command if the requested source file can't be opened)"
decl_stmt|;
comment|/** 	 * stream redirect failed 	 */
specifier|public
name|int
name|ERR_101
init|=
literal|101
decl_stmt|;
comment|// 200 Breakpoint, or code flow errors
comment|/** 	 * breakpoint could not be set (for some reason the breakpoint could not be 	 * set due to problems registering it) 	 */
specifier|public
name|int
name|ERR_200
init|=
literal|200
decl_stmt|;
comment|/** 	 * breakpoint type not supported (for example I don't support 'watch' yet 	 * and thus return this error) 	 */
specifier|public
name|int
name|ERR_201
init|=
literal|201
decl_stmt|;
comment|/** 	 * invalid breakpoint (the IDE tried to set a breakpoint on a line that does 	 * not exist in the file (ie "line 0" or lines past the end of the file) 	 */
specifier|public
name|int
name|ERR_202
init|=
literal|202
decl_stmt|;
comment|/** 	 * no code on breakpoint line (the IDE tried to set a breakpoint on a line 	 * which does not have any executable code. The debugger engine is NOT 	 * required to return this type if it is impossible to determine if there is 	 * code on a given location. (For example, in the PHP debugger backend this 	 * will only be returned in some special cases where the current scope falls 	 * into the scope of the breakpoint to be set)). 	 */
specifier|public
name|int
name|ERR_203
init|=
literal|203
decl_stmt|;
comment|/** 	 * Invalid breakpoint state (using an unsupported breakpoint state was 	 * attempted) 	 */
specifier|public
name|int
name|ERR_204
init|=
literal|204
decl_stmt|;
comment|/** 	 * No such breakpoint (used in breakpoint_get etc. to show that there is no 	 * breakpoint with the given ID) 	 */
specifier|public
name|int
name|ERR_205
init|=
literal|205
decl_stmt|;
comment|/** 	 * Error evaluating code (use from eval() (or perhaps property_get for a 	 * full name get)) 	 */
specifier|public
name|int
name|ERR_206
init|=
literal|206
decl_stmt|;
comment|/** 	 * Invalid expression (the expression used for a non-eval() was invalid) 	 */
specifier|public
name|int
name|ERR_207
init|=
literal|207
decl_stmt|;
comment|// 300 Data errors
comment|/** 	 * Can not get property (when the requested property to get did not exist, 	 * this is NOT used for an existing but uninitialized property, which just 	 * gets the type "uninitialised" (See: PreferredTypeNames)). 	 */
specifier|public
name|int
name|ERR_300
init|=
literal|300
decl_stmt|;
comment|/** 	 * Stack depth invalid (the -d stack depth parameter did not exist (ie, 	 * there were less stack elements than the number requested) or the 	 * parameter was< 0) 	 */
specifier|public
name|int
name|ERR_301
init|=
literal|301
decl_stmt|;
comment|/** 	 * Stack depth invalid (the -d stack depth parameter did not exist (ie, 	 * there were less stack elements than the number requested) or the 	 * parameter was< 0) 	 */
specifier|public
name|int
name|ERR_302
init|=
literal|302
decl_stmt|;
comment|// 900 Protocol errors
comment|/** 	 * Encoding not supported 	 */
specifier|public
name|int
name|ERR_900
init|=
literal|900
decl_stmt|;
specifier|public
name|String
name|ERR_900_STR
init|=
literal|"Encoding not supported"
decl_stmt|;
comment|/** 	 * An internal exception in the debugger occurred 	 */
specifier|public
name|int
name|ERR_998
init|=
literal|998
decl_stmt|;
specifier|public
name|String
name|ERR_998_STR
init|=
literal|"An internal exception in the debugger occurred"
decl_stmt|;
comment|/** 	 * Unknown error 	 */
specifier|public
name|int
name|ERR_999
init|=
literal|999
decl_stmt|;
specifier|public
name|String
name|ERR_999_STR
init|=
literal|"Unknown error"
decl_stmt|;
block|}
end_interface

end_unit

