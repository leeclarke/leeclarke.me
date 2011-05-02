//Jquery change watcher by http://james.padolsey.com/javascript/monitoring-dom-properties/
/*sample usage:  
	$('#someDiv').watch('innerHTML', function(propName, oldVal, newVal){
			alert('Value changed');;
	});
/*

/* Creates timed watch function which checks the given dom id value for changes.*/
jQuery.fn.watch = function( id, fn ) {

	return this.each(function(){

		var self = this;

		var oldVal = self[id];
		$(self).data(
			'watch_timer',
			setInterval(function(){
				if (self[id] !== oldVal) {
					fn.call(self, id, oldVal, self[id]);
					oldVal = self[id];
				}
			}, 100)
		);

	});

	return self;
};

/* Unwatches the dom value of the given id*/ 
jQuery.fn.unwatch = function( id ) {

	return this.each(function(){
		clearInterval( $(this).data('watch_timer') );
	});

};