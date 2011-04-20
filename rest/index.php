<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL | E_STRICT);


spl_autoload_register(array(new REST_Autoloader(), 'autoload'));

/**
 * SimplePie Autoloader class.
 *
 * @package SimplePie
 */
class REST_Autoloader
{
	/**
	 * Constructor.
	 */
	public function __construct()
	{
		$this->path = dirname(__FILE__);
	}

	/**
	 * Autoloader.
	 *
	 * @param string $class The name of the class to attempt to load.
	 */
	public function autoload($class)
	{
		// see if this request should be handled by this autoloader
		//if (strpos($class, 'SimplePie') !== 0) {
		//	return;
		//}

		$filename = $this->path . DIRECTORY_SEPARATOR . str_replace('_', DIRECTORY_SEPARATOR, $class) . '.php';
		include $filename;
	}
}




$mode = 'debug'; // 'debug' or 'production'
$server = new RestServer($mode);
// $server->refreshCache(); // uncomment momentarily to clear the cache if classes change in production mode

$server->addClass('TestController');
$server->addClass('FeedController', '/feeds'); // adds this as a base to all the URLs in this class

$server->handle();
