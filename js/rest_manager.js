//Manages the rest requests for various feeds. Uses JSONP to do off site calls.

var feedItems = [];
var target;

/**
 * Makes a get JSON call with the callback indicator that forces the JsonP workaround.  
 */
function retrieveRestFeed(feedUrl, resultTarget) {
	target = resultTarget;
	$.getJSON(feedUrl, parseFM);
	
}

/**
 * Parses and sets the lastFL data. 
 */
function parseFM(data){
	if(data.entries) {		
		for(i = 0; i<data.entries.length; i++) {
			var entry = data.entries[i];
			var title = entry.title.value;
			var date = entry.property_map.modified;
			feedItems.push('<li><span id="title">' + title +'</span><div id="listen-date">' + parseToLocal(date)+ '</div></li>');
		}
	}
	var out = feedItems.join()
	while(out.indexOf(',') >-1) {
		out = out.replace(',',' ');
	}
	$(target).html(out);
}


function parseToLocal(dateStr) {
	var resp = "Not Currently Available";
		if(dateStr) {
			var d = new Date(Date.parse(dateStr));
			resp = d.toLocaleTimeString() + " on " + d.toLocaleDateString();
		}
	return resp;	
}
