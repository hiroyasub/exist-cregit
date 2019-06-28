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

begin_comment
comment|/**  * An annotation for indicating that no locks  * must be held on the containing object before  * a method may be called.  *  * As well as explicitly expressing intention, this annotation can be used  * with {@link EnsureLockingAspect} to compile into the code runtime checks  * which will enforce the locking policy.  *  * Typically this is used on methods within implementations of {@link org.exist.collections.Collection}  * and {@link org.exist.dom.persistent.DocumentImpl}.  * The typical use is to ensure that a container holds no locks (by URI)  * when calling the method accessors on their internal state.  *  *<pre>  * {@code  * public class MyCollectonImpl implements Collection {  *     final XmldbURI uri;  *     public MyCollectionImpl(final XmldbURI uri) {  *         this.uri = uri;  *     }  *  *     public XmldbURI getUri() {  *         return uri;  *     }  *  *     ...  *  *     @EnsureContainerUnlocked  *     public int dirtyCountDocuments() {  *         return documents.size();  *     }  * }  * }  *</pre>  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
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
block|}
argument_list|)
specifier|public
annotation_defn|@interface
name|EnsureContainerUnlocked
block|{ }
end_annotation_defn

end_unit

