(:  This example demonstrates the use of higher-order functions.
    It uses the library module sequences.xq, which is found in 
    src/org/exist/xquery/lib and is loaded through the Java class 
    loader.
    
    Higher-order functions are functions which take other functions
    as arguments. Three examples are shown here: apply, map and filter.
    
    These functions are the XQuery equivalents of functions known from 
    the standard library of the Scheme programming language.
:)

import module namespace seq="http://exist-db.org/xquery/sequences" 
at "resource:org/exist/xquery/lib/sequences.xq";

declare namespace f="urn:my-functions";

declare function f:plus($a as xs:integer, $b as xs:integer) {
	$a + $b
};

declare function f:product($a as xs:integer, $b as xs:integer) {
	$a * $b
};

declare function f:remove($item as xs:integer) as xs:boolean {
	$item gt 10
};

declare function f:table-row($a, $b) {
	<tr>
		<td>{$a}</td>
		<td>{$b}</td>
	</tr>
};

<body>
	<p>The sum of numbers 1 to 4 is:</p>
	<p>{seq:apply(util:function("f:plus", 2), 1 to 4)}</p>

	<p>The product of numbers 1 to 4 is:</p>
	<p>{seq:apply(util:function("f:product", 2), 1 to 4)}</p>

	<p>Adding the elements of sequences (4, 7, 6) and (10, 15, 8) element-wise:</p> 
	<p>{seq:map(util:function("f:plus", 2), (4, 7, 6), (10, 15, 8))}</p>

	<p>Generating a table from the elements of sequences: (1, 2, 3) and
	("Franz", "Klaus", "Erwin"):</p>

	<table>
		{seq:map(util:function("f:table-row", 2), 1 to 3, ("Franz", "Klaus", "Erwin"))}
	</table>

	<p>Filter out all items greater than 10:</p>
	<p>{seq:filter(util:function("f:remove", 1), (8, 15, 7, 19, 22, 5))}</p>
</body>
