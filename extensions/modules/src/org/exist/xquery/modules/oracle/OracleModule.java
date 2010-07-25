begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|oracle
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
name|AbstractInternalModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * eXist Oracle Module Extension  *   * An extension module for the eXist Native XML Database that allows execution of  * PL/SQL Stored Procedures within an Oracle RDBMS, returning an XML representation  * of the result set. In particular, this module gives access to a<code>ResultSet</code>  * returned in an<code>OracleType.CURSOR</code>, functionality which is not provided by  * the more generic SQL extension module.<p><b>Please note</b> that this module is  * dependent on functionality contained within the SQL extension module and both modules  * must be enabled in<code>conf.xml</code> for this module to function correctly.  *   * @author Robert Walpole<robert.walpole@metoffice.gov.uk>  * @serial 2010-03-23  * @version 1.0  *   * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[])  */
end_comment

begin_class
specifier|public
class|class
name|OracleModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/oracle"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"oracle"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.5"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|ExecuteFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|ExecuteFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExecuteFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|ExecuteFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|OracleModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for executing PL/SQL stored procedures against an Oracle Database where data is given in an Oracle cursor, returning an XML representations of the result set."
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

