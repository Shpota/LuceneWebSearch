function submitSearchForm(pageNum) {
	var input = document.getElementById('page_num_input');
	input.value = pageNum;
	document.getElementById('search_from').submit();
}
