xquery version "1.0";
import module namespace style = "http://www.danmccreary.com/library" at "../../../modules/style.xqm";
declare namespace xs="http://www.w3.org/2001/XMLSchema";

declare namespace functx = "http://www.functx.com"; 
declare function functx:camel-case-to-words($arg as xs:string?, $delim as xs:string ) as xs:string{
   concat(substring($arg,1,1),
             replace(substring($arg,2),'(\p{Lu})',
                        concat($delim, '$1')))
} ;

let $title := 'Create a List of Element and Attribute Names'
let $file-path := concat($style:web-path-to-app, '/schemas/mods-3-4.xsd')
let $schema := doc($file-path)/xs:schema
let $simple-element-names := $schema//xs:simpleType/@name
let $complex-element-names := $schema//xs:complexType/@name
let $element-names := $schema//xs:element/@name
let $attribute-names := $schema//xs:attribute/@name
let $both := distinct-values(($element-names, $simple-element-names, $complex-element-names, $attribute-names))
let $sorted :=
   for $item in $both
   order by $item
   return $item

let $content :=
<code-table>
   <description>List of element and attribute names and labels generated by ../schemas/get-element-attribute-names.xq.</description>
  <file>{$file-path}</file>
   <code-table-name>element-attribute-names</code-table-name>
   <items>{
      for $name in $sorted
      let $words := functx:camel-case-to-words($name, ' ')
      let $first-letter := upper-case(substring($words, 1, 1))
      let $rest-of-word := substring($words, 2)
      let $label := concat($first-letter, $rest-of-word )
      return
      <item>
         <label>{$label}</label>
         <value>{string($name)}</value>
      </item>
   }</items>
</code-table>

return $content
