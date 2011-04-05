<?php

$data_back = json_decode (stripslashes($_REQUEST['data_to_send']), true);
print $data_back->{'id_list'[0]["id"]}; 

/*
 * json_decode with the second parameter set to true turns the JSON item into a php array.

So to access the element in your example, you'de use

$data_back['id_list'][0]['id']
 * /

?>
