<?php
//Connect to our MySQL database using the PDO extension.
$pdo = new PDO('mysql:host=localhost;dbname=news_test', 'phpusr', '!php#usr!');
 
//Our select statement. This will retrieve the data that we want.
$sql = "SELECT title, author, description, website_name, url FROM news_item ORDER BY title asc";
 
//Prepare the select statement.
$stmt = $pdo->prepare($sql);
 
//Execute the statement.
$stmt->execute();
 
//Retrieve the rows using fetchAll.
$results = $stmt->fetchAll();
 
?>
 
<select>
    <?php foreach($results as $title): ?>
        <option value="<?= $title['title']; ?>"></option>
    <?php endforeach; ?>
</select>