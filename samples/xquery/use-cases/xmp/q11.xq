

<bib>
{
        for $b in doc("bib.xml")//book[author]
        return
            <book>
                { $b/title }
                { $b/author }
            </book>
}
{
        for $b in doc("bib.xml")//book[editor]
        return
          <reference>
            { $b/title }
            {$b/editor/affiliation}
          </reference>
}
</bib> 

