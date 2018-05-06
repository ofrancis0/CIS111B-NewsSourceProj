<?php
/*
 * credentials used to connect to MySQL database
 * @var string
 */
$dbServ = "localhost";
$dbUser = "phpusr";
$dbPass = "!php#usr!";
$dbName = "news_test";

/*
 * initialized variable with default value used in conditional statement to 
 * determine if query renders results and table data is generated. 
 */
$showtable = false; 

$msg = '';  // variable for message confirming the source (topic) records were retrieved from.
$msg_records = '';  // variable for message confirming number of records retrieved.
$result;    // variable to hold results of returned query results.
$numRows;   // variable to hold results of variable used to hold count of records returned.

/*
 * variable to hold returned data from instantiated mysqli built-in function 
 * to authenticate to MySQL database.
 */
$conn = new mysqli($dbServ, $dbUser, $dbPass, $dbName);

//if there is an error with connectivity this conditional statement will inform.
if ($conn->connect_errno) {
    exit("Database Connection Failed. Reason: " . $conn->connect_error);
} else {
    //echo "Connection to Database Successful<br><br>";
    }

// Set character set from default latin1 to utf8 for successful translation of dbase characters.
$conn->set_charset("utf8");
//printf("Initial character set: %s\n", $conn->character_set_name(). "<br><br>");

/*
 * variable to store result of query used to retrieve the data 
 * from the MySQL database.
 * @var sql
 */ 
$sql = "SELECT DISTINCT article.topic FROM news_test.article WHERE article.topic " .
       "IS NOT NULL and article.topic <>'' ORDER BY article.topic";

// query to get a total count of records in database.
$sqlTot = "SELECT * FROM article ORDER BY topic ASC";

/*
 * use the connect() method from the DBConn class to connect to database,
 * run the query, retrieve results and store in $result variable.
 */
$result = $conn->query($sql); 
$recordCount = $conn->query($sqlTot);

/*
 *  variable created to record the number of rows retrieved.
 *  num_rows is a built in php function to return the count of
 *  the number of rows returned.
 */
//show number of records returned
//echo "There are " . $recordCount->num_rows . " total records in this set<br>";
echo "There are " . $result->num_rows . " distinct topics<br>"; // result was numRows
echo "<hr></hr>";


/*
 *  variable created to record the number of rows retrieved.
 *  num_rows is a built in php function to return the count of
 *  the number of rows returned.
 */
$numRows = $result->num_rows;

/*
 * a conditional loop to check that there are indeed rows/data that are 
 * available for processing. If rows exist then to process them and pass
 * into an associative array then returned for further processing,
 * also set $showtable variable boolean to true to allow creation of html 
 * table.
 */
if ($numRows > 0) {

    $topic = $_POST['topic'];
    $showtable = true;
    $msg = "Results from " . $topic;   // test the value is correct
    // second sql statement
    $sql2 = "SELECT title, sources, pubdate, url FROM article WHERE topic = '" . $topic . "'";

    // execute the second result set
    $result2 = $conn->query($sql2);

    // variable to show number of records returned
    $msg_records = "<em>" . $result2->num_rows . " records from this drop selection</em><br><br>";
}
?>


<!DOCTYPE html>
<!--
The index.php file is the web front end of the NewsApp project that hosts the 
html page that presents the news topics and selection of queried topics for
the user to interact withal

@author Oriel Francis, Chrissa LaPorte, Aaron Wile.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>Query the Dropdown from Topic Selection</title>
    </head>

    <style>

        body {
            font-family: arial, sans-serif;
            font-size: 100%;
            background-color: moccasin;
        }

        h1 {
            margin-bottom: 20px;
            text-align: center;
        }

        td, th {
            border: 1px solid #000;
            padding: 10px;
            vertical-align: top;
        }

        table {
            border-collapse: collapse;
        }
    </style>

    <body>
        <h1>Query the Topics</h1>
        <p>

<?php echo $msg_records; ?></p> 

<?php
//populate the drop down list
if ($result->num_rows > 0) {  // error check. Ensure there are rows to process.
    echo "<form action='index.php' method='post'>\n";
    echo "<select name='topic' id='topic'>\n";
    echo "<option value='none'>Select a Topic</option>\n";

    /*
     *  output data of each row
     */
    while ($row = $result->fetch_assoc()) {
        echo "<option value='" . $row["topic"] . "'>" . $row["topic"] . "</option>\n";
    }   //end while

    echo "</select><br><br>\n";
    echo "<input type='submit' value= 'Submit to retrieve selection'>\n";
    echo "</form>\n";
}   // end if
else {
    echo "Sorry, there were no results";
}   // end else

/*
 * generate result table if showtable variable is true else inform user 
 * that no results are found.
 */
if ($showtable == true) {

    //<!--second sql2 query table results -->
    if ($result2->num_rows > 0) {
        echo"<table>"
        . "<thead>"
        . "<tr>"
        . "<th>Title</>"
        . "<th>Source</>"
        . "<th>Publication Date</>"
        . "<th>URL</>"
        . "</tr>"
        . "</thead>"
        . "<tbody>\n\n";

        // processed output data of each row
        while ($row2 = $result2->fetch_assoc()) {
            echo "<tr>\n";
            echo "<td>" . $row2["title"] . "</td>\n";
            echo "<td>" . $row2["sources"] . "</td>\n";
            echo "<td>" . $row2["pubdate"] . "</td>\n";
            echo "<td><a href=" . $row2['url'] . " target=\"_blank\" >" . $row2['url'] . "</a></td>\n";
            echo "</tr>\n\n";
        }   // end while

        echo "</tbody>\n\n</table>\n";
    } // end if
    else {
        echo "Sorry, there were no results"; // output if no table results are found.
    } // end else
} // end outer if
?>

        <!-- user info output of number of records found in chosen topic -->
        <p> <?php echo $msg; ?> </p> 
        
        <!-- attribution to NewsAPI -->
        <p> <a href="https://newsapi.org" target=\"_blank\">Powered by NewsAPI.org</a></p>

    </body>
</html> 