<?php

/**
 * Description of DBConn
 * Class to run the method which provides authentication and details for logging
 * into the news_test database.
 *
 * @author oriel
 */
class DBConn {

    private $dbServ;    // variable to hold the name of database server
    private $dbUser;    // variable to hold the name of the MySQL database user.
    private $dbPass;    // variable to hold the password of the MySQL user.
    private $dbName;    // variable to hold the name of the database on the server to access.

    /*
     * php method to authenticate and establish a connection with the data, 
     * and return the authentication results to method calling variable.
     */
    protected function connect() {

        $this->dbServ = "localhost";
        $this->dbUser = "phpusr";
        $this->dbPass = "!php#usr!";
        $this->dbName = "news_test";

        $conn = new mysqli($this->dbServ,$this->dbUser,$this->dbPass,$this->dbName);
        
        return $conn;
    }

}
