xquery version "1.0";

import module namespace xdb="http://exist-db.org/xquery/xmldb";
import module namespace xdiff="http://exist-db.org/xquery/xmldiff"
at "java:org.exist.xquery.modules.xmldiff.XmlDiffModule";

declare namespace t="http://exist-db.org/xquery/testing";

declare option exist:serialize "indent=no";

declare variable $t:doc external;

declare function t:setup-action($action) {
    typeswitch ($action)
        case element(create-collection) return
            xdb:create-collection($action/@parent, $action/@name)
        case element(store) return
			let $data :=
				if ($action/*) then
					$action/*[1]
				else
					$action/string()
			return
            	xdb:store($action/@collection, $action/@name, $data, "text/xml")
        case element(remove-collection) return
            xdb:remove($action/@collection)
        default return
            ()
};

declare function t:setup($setup as element(setup)) {
    for $action in $setup/*
    return
        t:setup-action($action)
};

declare function t:tearDown($tearDown as element(tearDown)) {
    for $action in $tearDown/*
    return
        t:setup-action($action)
};

declare function t:run-test($test as element(test), $count as xs:integer) {
		
	let $context :=
	    string-join(
        	for $var in $test/../variable
        	return
        	    concat("declare variable $", $var/@name, " := ", util:serialize($var/*, ()), ";"),
            ""
        )
    let $output := 
		util:catch("*", 
			util:eval(concat($context, $test/code/string())),
			<error>Compilation error: {$util:exception-message}</error>
		)
    let $expected := 
        if ($test/@output eq 'text') then 
            data($test/expected)
        else $test/expected/node()
    let $OK :=
        if ($test/@output eq 'text') then
            normalize-space(string-join(for $x in $output return string($x),' ')) eq normalize-space($expected)
        else
            xdiff:compare($output, $expected)                              
            (: deep-equal($output, $expected) :)
    return
        <test n="{$count}" pass="{$OK}">
        {
            if (not($OK)) then
                ($test/task, $test/expected, <result>{$output}</result>)
            else ()
        }
        </test>
};

declare function t:run-testSet($set as element(TestSet)) {
    let $null := t:setup($set/setup)
    let $result :=
        <TestSet>
        {
            for $test at $p in $set/test
            return
                t:run-test($test, $p)
        }
        </TestSet>
    let $null := t:tearDown($set/tearDown)
    return $result
};

t:run-testSet(doc($doc)/TestSet)
