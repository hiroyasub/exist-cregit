begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|IEntry
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|IMap
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|Map
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
name|*
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
name|value
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|function
operator|.
name|ToIntFunction
import|;
end_import

begin_comment
comment|/**  * Full implementation of the XDM map() type based on an  * immutable hash-map.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Rettter</a>  */
end_comment

begin_class
specifier|public
class|class
name|MapType
extends|extends
name|AbstractMapType
block|{
specifier|private
specifier|static
specifier|final
name|ToIntFunction
argument_list|<
name|AtomicValue
argument_list|>
name|KEY_HASH_FN
init|=
name|AtomicValue
operator|::
name|hashCode
decl_stmt|;
comment|// TODO(AR) future potential optimisation... could the class member `map` remain `linear` ?
specifier|private
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|map
decl_stmt|;
specifier|private
name|int
name|type
init|=
name|Type
operator|.
name|ANY_TYPE
decl_stmt|;
specifier|private
specifier|static
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|)
block|{
return|return
operator|new
name|Map
argument_list|<>
argument_list|(
name|KEY_HASH_FN
argument_list|,
parameter_list|(
name|k1
parameter_list|,
name|k2
parameter_list|)
lambda|->
name|keysEqual
argument_list|(
name|collator
argument_list|,
name|k1
argument_list|,
name|k2
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// if there's no collation, we'll use a hash map for better performance
name|this
operator|.
name|map
operator|=
name|newMap
argument_list|(
name|collator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|AtomicValue
name|key
parameter_list|,
specifier|final
name|Sequence
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|newMap
argument_list|(
name|collator
argument_list|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|key
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Tuple2
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|keyValues
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|collator
argument_list|,
name|keyValues
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|Iterator
argument_list|<
name|Tuple2
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|keyValues
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// bulk put
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|map
init|=
name|newMap
argument_list|(
name|collator
argument_list|)
operator|.
name|linear
argument_list|()
decl_stmt|;
name|keyValues
operator|.
name|forEachRemaining
argument_list|(
name|kv
lambda|->
name|map
operator|.
name|put
argument_list|(
name|kv
operator|.
name|_1
argument_list|,
name|kv
operator|.
name|_2
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
operator|.
name|forked
argument_list|()
expr_stmt|;
name|setKeyType
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapType
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|other
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Integer
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|isLinear
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Map must be immutable, but linear Map was provided"
argument_list|)
throw|;
block|}
name|this
operator|.
name|map
operator|=
name|other
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
else|else
block|{
name|setKeyType
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|AbstractMapType
name|other
parameter_list|)
block|{
name|setKeyType
argument_list|(
name|other
operator|.
name|key
argument_list|()
operator|!=
literal|null
condition|?
name|other
operator|.
name|key
argument_list|()
operator|.
name|getType
argument_list|()
else|:
name|Type
operator|.
name|ANY_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|MapType
condition|)
block|{
name|map
operator|=
name|map
operator|.
name|union
argument_list|(
operator|(
operator|(
name|MapType
operator|)
name|other
operator|)
operator|.
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// create a transient map
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
init|=
name|map
operator|.
name|linear
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|other
control|)
block|{
name|newMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|key
argument_list|()
argument_list|,
name|entry
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// return to immutable map
name|map
operator|=
name|newMap
operator|.
name|forked
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|AbstractMapType
name|merge
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|AbstractMapType
argument_list|>
name|others
parameter_list|)
block|{
comment|// create a transient map
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
init|=
name|map
operator|.
name|linear
argument_list|()
decl_stmt|;
name|int
name|prevType
init|=
name|type
decl_stmt|;
for|for
control|(
specifier|final
name|AbstractMapType
name|other
range|:
name|others
control|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|MapType
condition|)
block|{
comment|// MapType - optimise merge
specifier|final
name|MapType
name|otherMap
init|=
operator|(
name|MapType
operator|)
name|other
decl_stmt|;
name|newMap
operator|=
name|map
operator|.
name|union
argument_list|(
name|otherMap
operator|.
name|map
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevType
operator|!=
name|otherMap
operator|.
name|type
condition|)
block|{
name|prevType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// non MapType
for|for
control|(
specifier|final
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|other
control|)
block|{
specifier|final
name|AtomicValue
name|key
init|=
name|entry
operator|.
name|key
argument_list|()
decl_stmt|;
name|newMap
operator|=
name|newMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevType
operator|!=
name|key
operator|.
name|getType
argument_list|()
condition|)
block|{
name|prevType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// return an immutable map
return|return
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|newMap
operator|.
name|forked
argument_list|()
argument_list|,
name|prevType
argument_list|)
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|AtomicValue
name|key
parameter_list|,
specifier|final
name|Sequence
name|value
parameter_list|)
block|{
name|setKeyType
argument_list|(
name|key
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|get
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
block|{
name|key
operator|=
name|convert
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|Sequence
name|result
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractMapType
name|put
parameter_list|(
specifier|final
name|AtomicValue
name|key
parameter_list|,
specifier|final
name|Sequence
name|value
parameter_list|)
block|{
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
init|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|newMap
argument_list|,
name|type
operator|==
name|key
operator|.
name|getType
argument_list|()
condition|?
name|type
else|:
name|Type
operator|.
name|ITEM
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
block|{
name|key
operator|=
name|convert
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|map
operator|.
name|contains
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|keys
parameter_list|()
block|{
specifier|final
name|ArrayListValueSequence
name|seq
init|=
operator|new
name|ArrayListValueSequence
argument_list|(
operator|(
name|int
operator|)
name|map
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|AtomicValue
name|key
range|:
name|map
operator|.
name|keys
argument_list|()
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
specifier|public
name|AbstractMapType
name|remove
parameter_list|(
specifier|final
name|AtomicValue
index|[]
name|keysAtomicValues
parameter_list|)
block|{
comment|// create a transient map
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
init|=
name|map
operator|.
name|linear
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|AtomicValue
name|key
range|:
name|keysAtomicValues
control|)
block|{
name|newMap
operator|=
name|newMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|// return an immutable map
return|return
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|newMap
operator|.
name|forked
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|map
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|map
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|key
parameter_list|()
block|{
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
init|=
name|map
operator|.
name|nth
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
return|return
name|entry
operator|.
name|key
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|value
parameter_list|()
block|{
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
init|=
name|map
operator|.
name|nth
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
return|return
name|entry
operator|.
name|value
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|setKeyType
parameter_list|(
specifier|final
name|int
name|newType
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|type
operator|=
name|newType
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|!=
name|newType
condition|)
block|{
name|type
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setKeyType
parameter_list|(
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newMap
parameter_list|)
block|{
for|for
control|(
specifier|final
name|AtomicValue
name|newKey
range|:
name|newMap
operator|.
name|keys
argument_list|()
control|)
block|{
specifier|final
name|int
name|newType
init|=
name|newKey
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|type
operator|=
name|newType
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|!=
name|newType
condition|)
block|{
name|type
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
break|break;
comment|// done!
block|}
block|}
block|}
specifier|private
name|AtomicValue
name|convert
parameter_list|(
specifier|final
name|AtomicValue
name|key
parameter_list|)
block|{
if|if
condition|(
name|type
operator|!=
name|Type
operator|.
name|ANY_TYPE
operator|&&
name|type
operator|!=
name|Type
operator|.
name|ITEM
condition|)
block|{
try|try
block|{
return|return
name|key
operator|.
name|convertTo
argument_list|(
name|type
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getKeyType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|IMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newLinearMap
parameter_list|()
block|{
comment|// TODO(AR) see bug in bifurcan - https://github.com/lacuna/bifurcan/issues/23
comment|//return new LinearMap<K, V>();
return|return
operator|new
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
operator|.
name|linear
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|newLinearMap
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collator
name|collator
parameter_list|)
block|{
return|return
name|newMap
argument_list|(
name|collator
argument_list|)
operator|.
name|linear
argument_list|()
return|;
block|}
block|}
end_class

end_unit

