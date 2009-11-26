begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
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
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|DebuggerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|DebuggingSource
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|BreakpointImpl
implements|implements
name|Breakpoint
block|{
comment|/** 	 * breakpoint type [required] 	 */
specifier|private
name|String
name|type
decl_stmt|;
comment|/** 	 * breakpoint state [optional, defaults to "enabled"] 	 */
specifier|private
name|boolean
name|state
init|=
literal|true
decl_stmt|;
comment|/** 	 * the filename to which the breakpoint belongs [optional] 	 */
specifier|private
name|String
name|fileName
decl_stmt|;
comment|/** 	 * the line number (lineno) of the breakpoint [optional] 	 */
specifier|private
name|Integer
name|lineNo
decl_stmt|;
comment|/** 	 * function name [required for call or return breakpoint types] 	 */
specifier|private
name|String
name|function
decl_stmt|;
comment|/** 	 * EXCEPTION exception name [required for exception breakpoint types]  	 */
specifier|private
name|String
name|exception
decl_stmt|;
comment|/** 	 * hit value (hit_value) used with the hit condition to determine if should break;  	 * a value of zero indicates hit count processing is disabled for this breakpoint  	 * [optional, defaults to zero (i.e. disabled)] 	 */
specifier|private
name|int
name|hitValue
init|=
literal|0
decl_stmt|;
comment|/** 	 * hit condition string (hit_condition);  	 * see HIT_CONDITION hit_condition documentation above;  	 * BTW 'o' stands for 'operator' [optional, defaults to '>='] 	 */
specifier|private
name|String
name|hitCondition
init|=
literal|">="
decl_stmt|;
comment|/** 	 * Boolean value indicating if this breakpoint is temporary. [optional, defaults to false] 	 */
specifier|private
name|boolean
name|temporary
init|=
literal|false
decl_stmt|;
comment|/** 	 * code expression, in the language of the debugger engine. The breakpoint should activate when the evaluated code evaluates to true. [required for conditional breakpoint types] 	 */
specifier|private
name|String
name|expression
decl_stmt|;
specifier|private
name|int
name|hitCount
init|=
literal|0
decl_stmt|;
specifier|public
name|BreakpointImpl
parameter_list|()
block|{
name|type
operator|=
name|Breakpoint
operator|.
name|TYPE_LINE
expr_stmt|;
comment|//default value
block|}
specifier|public
name|String
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
specifier|public
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
specifier|public
name|String
name|getFunction
parameter_list|()
block|{
return|return
name|function
return|;
block|}
specifier|public
name|String
name|getHitCondition
parameter_list|()
block|{
return|return
name|hitCondition
return|;
block|}
specifier|public
name|int
name|getHitCount
parameter_list|()
block|{
return|return
name|hitCount
return|;
block|}
specifier|public
name|int
name|getHitValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|Integer
name|getLineno
parameter_list|()
block|{
return|return
name|lineNo
return|;
block|}
specifier|public
name|boolean
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|boolean
name|getTemporary
parameter_list|()
block|{
return|return
name|temporary
return|;
block|}
specifier|public
name|void
name|setException
parameter_list|(
name|String
name|exception
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
comment|//protected? -shabanovd
specifier|public
name|void
name|setFilename
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|filename
expr_stmt|;
block|}
specifier|public
name|void
name|setFunction
parameter_list|(
name|String
name|function
parameter_list|)
block|{
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
specifier|public
name|void
name|setHitCondition
parameter_list|(
name|String
name|condition
parameter_list|)
block|{
name|this
operator|.
name|hitCondition
operator|=
name|condition
expr_stmt|;
block|}
specifier|public
name|void
name|setHitCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|hitCount
operator|=
name|count
expr_stmt|;
block|}
specifier|public
name|void
name|setHitValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|hitValue
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|void
name|setLineno
parameter_list|(
name|Integer
name|lineno
parameter_list|)
block|{
name|this
operator|.
name|lineNo
operator|=
name|lineno
expr_stmt|;
block|}
specifier|public
name|void
name|setState
parameter_list|(
name|boolean
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
specifier|public
name|void
name|setTemporary
parameter_list|(
name|boolean
name|temporary
parameter_list|)
block|{
name|this
operator|.
name|temporary
operator|=
name|temporary
expr_stmt|;
block|}
specifier|private
name|int
name|id
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|int
name|breakpointNo
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|breakpointNo
expr_stmt|;
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|toXMLString
parameter_list|()
block|{
return|return
literal|"<breakpoint "
operator|+
literal|"id=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"type=\""
operator|+
name|type
operator|+
literal|"\" "
operator|+
literal|"state=\""
operator|+
name|state
operator|+
literal|"\" "
operator|+
literal|"filename=\""
operator|+
name|fileName
operator|+
literal|"\" "
operator|+
literal|"lineno=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|lineNo
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"function=\""
operator|+
name|function
operator|+
literal|"\" "
operator|+
literal|"exception=\""
operator|+
name|exception
operator|+
literal|"\" "
operator|+
literal|"hit_value=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|hitValue
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"hit_condition=\""
operator|+
name|hitCondition
operator|+
literal|"\" "
operator|+
literal|"hit_count=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|hitCount
argument_list|)
operator|+
literal|"\">"
operator|+
literal|"<expression>"
operator|+
name|expression
operator|+
literal|"</expression>"
operator|+
literal|"</breakpoint>"
return|;
block|}
specifier|private
name|DebuggingSource
name|debuggingSource
decl_stmt|;
specifier|public
name|void
name|setDebuggingSource
parameter_list|(
name|DebuggingSource
name|debuggingSource
parameter_list|)
block|{
name|this
operator|.
name|debuggingSource
operator|=
name|debuggingSource
expr_stmt|;
block|}
specifier|private
name|DebuggerImpl
name|getDebugger
parameter_list|()
block|{
return|return
operator|(
name|DebuggerImpl
operator|)
name|debuggingSource
operator|.
name|getDebugger
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|sync
parameter_list|()
block|{
if|if
condition|(
name|getId
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|getDebugger
argument_list|()
operator|.
name|setBreakpoint
argument_list|(
name|this
argument_list|)
return|;
block|}
if|else if
condition|(
name|getId
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|getDebugger
argument_list|()
operator|.
name|updateBreakpoint
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|//TODO: call remove breakpoint ???
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
name|getDebugger
argument_list|()
operator|.
name|removeBreakpoint
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

