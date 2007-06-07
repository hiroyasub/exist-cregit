begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Holds context information and execution hints for XQuery expressions.  * Instances of this class are passed to {@link Expression#analyze(AnalyzeContextInfo)}  * during the analysis phase of the query.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|AnalyzeContextInfo
block|{
specifier|private
name|Expression
name|parent
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|flags
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|contextId
init|=
name|Expression
operator|.
name|NO_CONTEXT_ID
decl_stmt|;
specifier|private
name|int
name|staticType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
specifier|private
name|Expression
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|public
name|AnalyzeContextInfo
parameter_list|()
block|{
block|}
comment|/** 	 * Create a new AnalyzeContextInfo using the given parent and flags. 	 *  	 * @param parent the parent expression which calls this method      * @param flags int value containing a set of flags. See the constants defined      * in this class. 	 */
specifier|public
name|AnalyzeContextInfo
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/** 	 * Create a new object as a clone of other. 	 *  	 * @param other 	 */
specifier|public
name|AnalyzeContextInfo
parameter_list|(
name|AnalyzeContextInfo
name|other
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|other
operator|.
name|parent
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|other
operator|.
name|flags
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|other
operator|.
name|contextId
expr_stmt|;
name|this
operator|.
name|contextStep
operator|=
name|other
operator|.
name|contextStep
expr_stmt|;
name|this
operator|.
name|staticType
operator|=
name|other
operator|.
name|staticType
expr_stmt|;
block|}
comment|/** 	 * Returns the current context id. The context id is used 	 * to keep track of the context node set within a predicate 	 * expression or where-clause. The id identifies the ancestor  	 * expression to which the context applies. 	 *  	 * @return  current context id. 	 */
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|contextId
return|;
block|}
specifier|public
name|void
name|setContextId
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
block|}
comment|/** 	 * Returns the processing flags. Every expression may pass 	 * execution hints to its child expressions, encoded as bit flags.  	 *  	 * @return processing flags 	 */
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
comment|/** 	 * Sets the processing flags to be passed to a child expression. 	 *  	 * @param flags 	 */
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
specifier|public
name|void
name|addFlag
parameter_list|(
name|int
name|flag
parameter_list|)
block|{
name|flags
operator||=
name|flag
expr_stmt|;
block|}
specifier|public
name|void
name|removeFlag
parameter_list|(
name|int
name|flag
parameter_list|)
block|{
name|flags
operator|&=
operator|~
name|flag
expr_stmt|;
block|}
comment|/** 	 * Returns the parent of the current expression. 	 */
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|Expression
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|int
name|getStaticType
parameter_list|()
block|{
return|return
name|staticType
return|;
block|}
specifier|public
name|void
name|setStaticType
parameter_list|(
name|int
name|staticType
parameter_list|)
block|{
name|this
operator|.
name|staticType
operator|=
name|staticType
expr_stmt|;
block|}
specifier|public
name|void
name|setContextStep
parameter_list|(
name|Expression
name|step
parameter_list|)
block|{
name|this
operator|.
name|contextStep
operator|=
name|step
expr_stmt|;
block|}
specifier|public
name|Expression
name|getContextStep
parameter_list|()
block|{
return|return
name|contextStep
return|;
block|}
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
literal|"ID: "
argument_list|)
operator|.
name|append
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" Type: "
argument_list|)
operator|.
name|append
argument_list|(
name|Type
operator|.
name|getTypeName
argument_list|(
name|staticType
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" Flags: "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|Expression
operator|.
name|SINGLE_STEP_EXECUTION
operator|)
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"single "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|Expression
operator|.
name|IN_PREDICATE
operator|)
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"in-predicate "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|Expression
operator|.
name|IN_WHERE_CLAUSE
operator|)
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"in-where-clause "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|Expression
operator|.
name|IN_UPDATE
operator|)
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"in-update "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|Expression
operator|.
name|DOT_TEST
operator|)
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"dot-test "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|flags
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

