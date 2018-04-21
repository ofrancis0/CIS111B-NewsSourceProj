<?php
// put your code here
//include 'inc/DBConn.php';
//include 'inc/DBSelectDrop.php';
//include 'inc/DBNewsTitles.php';
//include 'inc/DBViewNewsData.php';

$dbServ = "localhost";
$dbUser = "phpusr";
$dbPass = "!php#usr!";
$dbName = "news_test";

//$showtable = false;

$msg = '';
$msg_records = '';

$result;
//$result2='';
$numRows;

$conn = new mysqli($dbServ, $dbUser, $dbPass, $dbName);

//if there is an error with connectivity this conditional statement will inform.
if ($conn->connect_errno) {
    exit("Database Connection Failed. Reason: " . $conn->connect_error);
} else {
    echo "Connection to Database Successful<br><br>";
    //return $conn;
}


//function getDropData() {
//Our select statement. This will retrieve the data that we want.
//$sql = "SELECT title, author, description, website_name, url FROM "
//        . "news_item ORDER BY title ASC";
$sql = "SELECT DISTINCT topic FROM dummy_article ORDER BY topic ASC";
$sqlTot = "SELECT * FROM dummy_article ORDER BY topic ASC";


/*
 * use the connect() method from the DBConn class to connect to database,
 * run the query, retrieve results and store in $results variable.
 */
$result = $conn->query($sql); // conn was this->connect().
$recordCount = $conn->query($sqlTot);

/*
 *  variable created to record the number of rows retrieved.
 *  num_rows is a built in php function to return the count of
 *  the number of rows returned.
 */
//$numRows=$result->num_rows; 
//show number of records returned
echo "There are " . $recordCount->num_rows . " total records in this set<br>";
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
 * into an associative array then returned for further processing.
 */
if ($numRows > 0) {

    $topic = $_POST['topic'];
    $showtable = true;
    $msg = "Results from " . $topic;   // test the value is correct
    // second sql statement
    $sql2 = "SELECT title, sources, pubdate, url FROM dummy_article WHERE topic = '" . $topic . "'";

    // execute the second result set
    $result2 = $conn->query($sql2);

    // Show number of records returned
    $msg_records = "<em>" . $result2->num_rows . " records from this drop selection</em><br><br>";
}
?>


<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <meta charset="UTF-16">
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
    echo "<option value='none'>Please Select a Topic</option>\n";

    // output data of each row
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
// display query if showtable variable is true.
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

        // output data of each row
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
        echo "Sorry, there were no results";
    } // end else
} // end outer if
?>

        <p> <?php echo $msg; ?> </p>

    </body>
</html> 