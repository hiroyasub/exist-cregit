xquery version "1.0";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace math="java:java.lang.Math";

declare function local:random($max as xs:integer) 
as empty()
{
    let $r := ceiling(math:random() * $max) cast as xs:integer
    return (
        request:set-session-attribute("random", $r),
        request:set-session-attribute("guesses", 0)
    )
};

declare function local:guess($guess as xs:integer,
$rand as xs:integer) as element()
{
    let $count := request:get-session-attribute("guesses") + 1
    return (
        request:set-session-attribute("guesses", $count),
        if ($guess lt $rand) then
            <p>Your number is too small!</p>
        else if ($guess gt $rand) then
            <p>Your number is too large!</p>
        else ( 
            <p>Congratulations! You guessed the right number with
            {$count} tries. Try again!</p>,
            local:random(100)
        )
    )
};

declare function local:main() as node()?
{
    request:create-session(),
    let $rand := request:get-session-attribute("random"),
        $guess := request:request-parameter("guess", ())
    return
        if ($rand) then local:guess($guess, $rand)
        else local:random(100)
};

<html>
    <head><title>Number Guessing</title></head>
    <body>
        <form action="{request:encode-url(request:request-uri())}">
            <table border="0">
                <tr>
                    <th colspan="2">
                        Guess a number
                    </th>
                </tr>
                <tr>
                    <td>Number:</td>
                    <td><input type="text" name="guess"
                        size="3"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="left">
                        <input type="submit"/>
                    </td>
                </tr>
            </table> 
        </form>
        { local:main() }
    </body>
</html>
