<?php
    include 'inc/DBConn.php';
    include 'inc/DBNewsTitles.php';
    include 'inc/DBViewNewsData.php';
?>

<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>News_Items Test</title>
    </head>
    <body>
        <p>
            <h2>News Stories</h2>
        <?php
        // put your code here
        $titles =new DBViewNewsData();
        $titles->showNewsInfo()
        ?>
        </p>
        <p>
            <!-- This should not be a URL below, it should be a result of a 
            php query, but I couldnt get it working, so this is for demonstration 
            purposes of functionality. Also question if it needs to be rectified
            as Aaron suggests his intent is to how news sources and links and not
            nested rendered destination URL's-->
            <iframe src="http://www.chicagotribune.com/news/nationworld/politics/ct-sinclair-video-trump-20180402-story.html" width=50%" 
                    height="800" frameborder="1" scrolling="yes"></iframe>
        </p>
    </body>
</html>

