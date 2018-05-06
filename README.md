# CIS111B-NewsSourceProj
Final Project: News App

Group composed of Aaron Wile, Chrissa LaPorte, and Oriel Francis. 

A web-based app that presents a range of articles on different topics from different political orientations.
The goal of the web app is to track trending news stories, consolidating links from a variety of news sources for the same story or topic. 
The idea is to easily vary the source of users’ news, with the hopes of both making users’ own news biases and “opposition” bias more visible.

Components of the project:
* A news scraper, coded in Java (NewsScaper class (main), NewsAPIConnector class, Article class)
* A MySQL database (SQLReadWriter class; see also SQL folder for create table scripts and test scripts)
* A webpage, coded in PHP, which connects with the database using mysqli (index.php)
