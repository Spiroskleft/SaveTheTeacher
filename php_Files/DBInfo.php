<?php
 // 1- connect to db
$host="127.0.0.1";
$user="root";
$password="";
$database="teacherDB";
$connect=  mysqli_connect($host, $user, $password, $database);
if(mysqli_connect_errno())
{ die("cannot connect to database field:". mysqli_connect_error());   }
?>