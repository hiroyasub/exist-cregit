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
name|io
operator|.
name|IOException
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
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|File
argument_list|>
block|{
name|Vector
argument_list|<
name|File
argument_list|>
name|_elements
init|=
operator|new
name|Vector
argument_list|<
name|File
argument_list|>
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
name|String
name|component
parameter_list|)
block|{
if|if
condition|(
operator|(
name|component
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|component
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|component
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|key
init|=
name|f
operator|.
name|getCanonicalFile
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
name|IOException
name|e
parameter_list|)
block|{
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
name|File
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
name|component
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|key
init|=
name|component
operator|.
name|getCanonicalFile
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
name|IOException
name|e
parameter_list|)
block|{
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|cp
init|=
operator|new
name|StringBuilder
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
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
operator|(
operator|(
name|File
operator|)
operator|(
name|_elements
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|)
operator|.
name|getPath
argument_list|()
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
operator|(
operator|(
name|File
operator|)
operator|(
name|_elements
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|)
operator|)
operator|.
name|getPath
argument_list|()
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
name|int
name|cnt
init|=
name|_elements
operator|.
name|size
argument_list|()
decl_stmt|;
name|URL
index|[]
name|urls
init|=
operator|new
name|URL
index|[
name|cnt
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cnt
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|urls
index|[
name|i
index|]
operator|=
name|_elements
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
name|parent
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|parent
operator|=
name|Classpath
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|parent
operator|=
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
expr_stmt|;
block|}
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
name|File
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
block|}
end_class

end_unit

