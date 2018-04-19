<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of DBNewsTitles
 *
 * @author oriel
 */
class DBNewsTitles extends DBConn {
    
    private $result;
    private $numRows;
    
    protected function getNewsData() {
        //Our select statement. This will retrieve the data that we want.
        $sql = "SELECT title, author, description, website_name, url FROM "
                . "news_item ORDER BY title ASC";
        
        /*
         * use the connect() method from the DBConn class to connect to database,
         * run the query, retrieve results and store in $results variable.
         */
        $result = $this->connect()->query($sql);
        
        /*
         *  variable created to record the number of rows retrieved.
         *  num_rows is a built in php function to return the count of
         *  the number of rows returned.
        */
        $numRows=$result->num_rows; 
        
        /*
         * a conditional loop to check that there are indeed rows/data that are 
         * available for processing. If rows exist then to process them and pass
         * into an associative array then returned for further processing.
         */
        if($numRows>0){
            while($row=$result->fetch_assoc()){
                $data[]=$row;
            }
            return $data;
        }
        
    }

}
