begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|lock
operator|.
name|Lock
operator|.
name|LockType
import|;
end_import

begin_comment
comment|/**  * An annotation for indicating that certain locks  * must be held on parameters to a method or return types.  *  * As well as explicitly expressing intention, this annotation can be used  * with {@link EnsureLockingAspect} to compile into the code runtime checks  * which will enforce the locking policy.  *  * Typically this is used with parameters of type {@link org.exist.collections.Collection}  * and {@link org.exist.dom.persistent.DocumentImpl}. If this annotation is  * used on an {@link org.exist.xmldb.XmldbURI} then a {@code type} value must  * also be provided to indicate the type of the lock identified by the uri.  *  * For example we may indicate that Collection parameters to methods  * must already be locked appropriately before the method is called:  *<pre>  * public Result copyCollection(  *          {@code @EnsureLocked(mode=LockMode.READ_LOCK)} final Collection srcCollection,  *          {@code @EnsureLocked(mode=LockMode.WRITE_LOCK)} final Collection destCollection) {  *  *    ...  *  * }  *</pre>  *  * We may also indicate that objects returned from a function must have gained an appropriate  * lock for the calling thread:  *  *<pre>  * public {@code @EnsureLocked(mode=LockMode.READ_LOCK)} Collection openCollection(final XmldbURI uri, final LockMode lockMode) {  *  *    ...  *  * }  * }</pre>  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|value
operator|=
block|{
name|ElementType
operator|.
name|METHOD
block|,
name|ElementType
operator|.
name|PARAMETER
block|}
argument_list|)
specifier|public
annotation_defn|@interface
name|EnsureLocked
block|{
comment|/**      * Specifies the mode of the held lock.      *      * {@link LockMode#NO_LOCK} is used as the default, to allow {@code modeParam}      * to be used instead.      *      * If neither {@code mode} or {@code modeParam} are specified, and there is not a      * single {@link Lock.LockMode} type parameter that can be used      * then an IllegalArgumentException will be generated if {@link EnsureLockingAspect}      * detects this situation.      * @return to be documented      */
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|()
default|default
name|LockMode
operator|.
name|NO_LOCK
function_decl|;
comment|/**      * Specifies that the mode of the held lock is informed      * by a parameter to the method.      *      * The value of this attribute is the (zero-based) index      * of the parameter within the method signature.      * @return to be documented      */
name|short
name|modeParam
parameter_list|()
default|default
name|NO_MODE_PARAM
function_decl|;
name|short
name|NO_MODE_PARAM
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * The type of the lock.      *      * Only needed if the annotation is not placed on a      * {@link org.exist.collections.Collection} or {@link org.exist.dom.persistent.DocumentImpl}      * parameter or return type.      * @return to be documented      */
name|Lock
operator|.
name|LockType
name|type
parameter_list|()
default|default
name|LockType
operator|.
name|UNKNOWN
function_decl|;
block|}
end_annotation_defn

end_unit

