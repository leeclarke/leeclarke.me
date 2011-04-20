<?php

class FeedController {
	
	/**
	 * Returns a JSON string object to the browser when hitting the root of the domain
	 *
	 * @url GET /
	 */
	public function test()
	{
			return "Hello World";
	}
	
	/**
	 * Returns a JSON string object to the browser when hitting the root of the domain
	 *
	 * @url GET /lastfm
	 */
	public function getLastFm()
	{
			return "Hello Last FM";
	}
}
