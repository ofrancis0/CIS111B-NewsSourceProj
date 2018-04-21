<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of DbQuery
 *
 * @author oriel
 */
class DbQuery {
    //put your code here
    //prepare sql and bind          
            $query = "SELECT title, author, description, website_name, url FROM news_item ORDER BY title asc";
            $resultObj = $connection->query($query);
            
            /*
             * num_rows property run against $resultObj query variable to let us know how
             * many results have been returned from the query.
             * The idea is we dont want to process any of the rows if there are not any to process.
             */
             if ($resultObj->num_rows > 0) {
             while ($singleRowFromQuery = $resultObj->fetch_assoc()) {
}
             }
}