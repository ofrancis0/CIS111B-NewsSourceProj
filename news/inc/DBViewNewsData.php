<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ViewNewsData
 *
 * @author oriel
 */
class DBViewNewsData extends DBNewsTitles {

    // method to process queried data and present in formatted manner.
    public function showNewsInfo() {

        $datas = $this->getNewsData(); // variable to save data from method called in extended class.
        
        /*
         * loop to process the data, assign to temporary variable and use temp variable
         * to output values found in associated keys.
         */
        foreach ($datas as $data) {
            // To display the result with some formatting applied.
            echo nl2br("<strong><mark>Title: </mark></strong>" . $data['title'] . ".\n");
            echo nl2br("<strong><mark>Author: </mark></strong>" . $data['author'] . ".\n");
            echo nl2br("<strong><mark>Description: </mark></strong>" . $data['description'] . "\n");
            echo nl2br("<strong><mark>Website Name: </mark></strong>" . $data['website_name'] . ".\n");
            echo "<strong><mark>URL:</mark></strong> <a href=" . $data['url'] . " target=\"_blank\" >" . $data['url'] . "</a>." . PHP_EOL;
        }
    }

}
