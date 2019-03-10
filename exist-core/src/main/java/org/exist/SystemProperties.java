begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|lazy
operator|.
name|LazyVal
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

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|SystemProperties
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SystemProperties
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SystemProperties
name|instance
init|=
operator|new
name|SystemProperties
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LazyVal
argument_list|<
name|Properties
argument_list|>
name|properties
init|=
operator|new
name|LazyVal
argument_list|<>
argument_list|(
name|this
operator|::
name|load
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|SystemProperties
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|private
name|SystemProperties
parameter_list|()
block|{
block|}
specifier|private
name|Properties
name|load
parameter_list|()
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SystemProperties
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"system.properties"
argument_list|)
init|)
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to load system.properties from class loader: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
specifier|public
name|String
name|getSystemProperty
parameter_list|(
specifier|final
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSystemProperty
parameter_list|(
specifier|final
name|String
name|propertyName
parameter_list|,
specifier|final
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

