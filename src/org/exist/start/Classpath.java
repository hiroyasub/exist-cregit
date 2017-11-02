begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// ========================================================================
end_comment

begin_comment
comment|// Copyright (c) 2002 Mort Bay Consulting (Australia) Pty. Ltd.
end_comment

begin_comment
comment|// $Id$
end_comment

begin_comment
comment|// ========================================================================
end_comment

begin_comment
comment|//Modified for eXist-db
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|start
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|InvalidPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Class to handle CLASSPATH construction  * @author Jan Hlavatï¿½  */
end_comment

begin_class
specifier|public
class|class
name|Classpath
implements|implements
name|Iterable
argument_list|<
name|Path
argument_list|>
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Classpath
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|_elements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Classpath
parameter_list|()
block|{
block|}
specifier|public
name|Classpath
parameter_list|(
specifier|final
name|String
name|initial
parameter_list|)
block|{
name|addClasspath
argument_list|(
name|initial
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|addComponent
parameter_list|(
specifier|final
name|String
name|component
parameter_list|)
block|{
if|if
condition|(
name|component
operator|!=
literal|null
operator|&&
name|component
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
specifier|final
name|Path
name|p
init|=
name|Paths
operator|.
name|get
argument_list|(
name|component
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|key
init|=
name|p
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|_elements
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|_elements
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|addComponent
parameter_list|(
specifier|final
name|Path
name|component
parameter_list|)
block|{
if|if
condition|(
name|component
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|component
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|key
init|=
name|component
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|_elements
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|_elements
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|addClasspath
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringTokenizer
name|t
init|=
operator|new
name|StringTokenizer
argument_list|(
name|s
argument_list|,
name|File
operator|.
name|pathSeparator
argument_list|)
decl_stmt|;
while|while
condition|(
name|t
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|addComponent
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|cp
init|=
operator|new
name|StringBuilder
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cnt
init|=
name|_elements
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|cnt
operator|>=
literal|1
condition|)
block|{
name|cp
operator|.
name|append
argument_list|(
name|_elements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|cp
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparatorChar
argument_list|)
expr_stmt|;
name|cp
operator|.
name|append
argument_list|(
name|_elements
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cp
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|EXistClassLoader
name|getClassLoader
parameter_list|(
name|ClassLoader
name|parent
parameter_list|)
block|{
specifier|final
name|URL
name|urls
index|[]
init|=
name|_elements
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Path
operator|::
name|toUri
argument_list|)
operator|.
name|map
argument_list|(
name|u
lambda|->
block|{
try|try
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|u
operator|.
name|toURL
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedURLException
name|e
parameter_list|)
block|{
return|return
name|Optional
operator|.
expr|<
name|URL
operator|>
name|empty
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|filter
argument_list|(
name|ou
lambda|->
name|ou
operator|.
name|isPresent
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|Optional
operator|::
name|get
argument_list|)
operator|.
name|toArray
argument_list|(
name|sz
lambda|->
operator|new
name|URL
index|[
name|sz
index|]
argument_list|)
decl_stmt|;
comment|// try and ensure we have a classloader
name|parent
operator|=
name|or
argument_list|(
name|or
argument_list|(
name|or
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|parent
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|Optional
operator|.
name|ofNullable
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|Optional
operator|.
name|ofNullable
argument_list|(
name|Classpath
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|Optional
operator|.
name|ofNullable
argument_list|(
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|EXistClassLoader
argument_list|(
name|urls
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Path
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|_elements
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Copied from {@link org.exist.util.function.OptionalUtil#or(Optional, Supplier)}      * as org.exist.start is compiled into a separate Jar and doesn't have      * the rest of eXist available on the classpath      */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Optional
argument_list|<
name|T
argument_list|>
name|or
parameter_list|(
specifier|final
name|Optional
argument_list|<
name|T
argument_list|>
name|left
parameter_list|,
specifier|final
name|Supplier
argument_list|<
name|Optional
argument_list|<
name|T
argument_list|>
argument_list|>
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|left
return|;
block|}
else|else
block|{
return|return
name|right
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

