<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>Simple php query from within html</title>
    </head>
    <body>
        <h2> A Simple Test of PHP functionality</h2>
        <p> <mark><kbd>This page runs a single SQL query developed with php to retrieve<br>
                    information from a database and display it to the user.</kbd><mark><br>
                        <hr />
        </p>


        <?php
        // put your code here
        session_start();

        /*
         * This database password and connection information.
         * (made password and username even more generic)
         */
        $dbPassword = "password";
        $dbUserName = "username";
        $dbServer = "localhost";
        $dbName = "news_test";

        try{
            //$connection variable from instatiated mysqli class with login parameters for msql database.
            $connection = new mysqli($dbServer, $dbUserName, $dbPassword, $dbName);

            //if there is an error with connectivity this conditional statement will inform.
            if ($connection->connect_errno) {
            exit("Database Connection Failed. Reason: " . $connection->connect_error);
            }

            //prepare sql and bind


            $query = "SELECT title, author, description, website_name, url FROM news_item ORDER BY title";
            $resultObj = $connection->query($query);

            /*
             * num_rows property run against $resultObj query variable to let us know how
             * many results have been returned from the query.
             * The idea is we dont want to process any of the rows if there are not any to process.
             */
             if ($resultObj->num_rows > 0) {
             while ($singleRowFromQuery = $resultObj->fetch_assoc()) {

            // To display the result with some formatting applied.
            echo nl2br("Title: " . $singleRowFromQuery['title'] . ".\n");
            echo nl2br("Author: " . $singleRowFromQuery['author'] . ".\n");
            echo nl2br("Description: " . $singleRowFromQuery['description'] . "\n");
            echo nl2br("Website Name: " . $singleRowFromQuery['website_name'] . ".\n");
            echo "URL: " . $singleRowFromQuery['url'] . "." . PHP_EOL;
                }
            }

        } catch (Exception $ex) {}
        ?>
        <p>
            <!-- This should not be a URL below, it should be a result of a
            php query, but I couldnt get it working, so this is for demonstration
            purposes of functionality -->
            <iframe src="http://www.chicagotribune.com/news/nationworld/politics/ct-sinclair-video-trump-20180402-story.html" width=50%"
                    height="800" frameborder="1" scrolling="yes"></iframe>
        </p>

        <?php
        $resultObj->close();    // close the resultObj connection.
        $connection->close();   // close the database connection.

        ?>

    </body>
</html>
