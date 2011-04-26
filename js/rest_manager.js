//Manages the rest requests for various feeds. Uses JSONP to do off site calls.

var feedItems = [];
var target = [];

/**
 * Makes a get JSON call with the callback indicator that forces the JsonP workaround.  
 */
function retrieveLastFMFeed(feedUrl, resultTarget) {
	target['fm'] = resultTarget;
	$.getJSON(feedUrl, parseFM);
}

function retrieveBlogFeed(feedUrl, resultTarget) {
	target['blog'] = resultTarget;
	$.getJSON(feedUrl, parseBlog);
}


/**
 * Parses and sets the lastFL data. 
 */
function parseFM(data){
	feedItems = [];
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
	$(target['fm']).html(out);
}

/**
 * Parses and sets the Blogger data. 
 */
function parseBlog(data){
	feedItems = [];
	if(data.entries) {		
		var maxCt = (data.entries.length<5)?data.entries.length:5;
		for(i = 0; i<maxCt; i++) {
			var entry = data.entries[i];
			
			var title = entry.title.value;
			var linkBack = entry.property_map.link;
			var date = entry.modified;
			feedItems.push('<li><a href="' + linkBack + '"><span id="title">' + title +'</span></a><div id="listen-date">' + parseToLocal(date)+ '</div></li>');
		}
	}
	var out = feedItems.join()
	while(out.indexOf(',') >-1) {
		out = out.replace(',',' ');
	}
	$(target['blog']).html(out);
}

function parseToLocal(dateStr) {
	var resp = "Not Currently Available";
		if(dateStr) {
			var d = new Date(Date.parse(dateStr));
			resp = d.toLocaleTimeString() + " on " + d.toLocaleDateString();
		}
	return resp;	
}
