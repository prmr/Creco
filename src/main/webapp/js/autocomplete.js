function getCompletions(val) {
    $.ajax({
        url: "/autocomplete?input=" + val,
        async: false,
        success: function (response) {
            var mySplitResult;
            mySplitResult = response.split(",");
            var index = mySplitResult.length;
            var availableTags = [];
            if (index > 9) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
                availableTags.push(mySplitResult[5]);
                availableTags.push(mySplitResult[6]);
                availableTags.push(mySplitResult[7]);
                availableTags.push(mySplitResult[8]);
                availableTags.push(mySplitResult[9]);
            } else if (index == 2) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
            } else if (index == 3) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
            } else if (index == 4) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
            } else if (index == 5) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
            } else if (index == 6) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
                availableTags.push(mySplitResult[5]);
            } else if (index == 7) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
                availableTags.push(mySplitResult[5]);
                availableTags.push(mySplitResult[6]);
            } else if (index == 8) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
                availableTags.push(mySplitResult[5]);
                availableTags.push(mySplitResult[6]);
                availableTags.push(mySplitResult[7]);
            } else if (index == 9) {
                availableTags.push(mySplitResult[0]);
                availableTags.push(mySplitResult[1]);
                availableTags.push(mySplitResult[2]);
                availableTags.push(mySplitResult[3]);
                availableTags.push(mySplitResult[4]);
                availableTags.push(mySplitResult[5]);
                availableTags.push(mySplitResult[6]);
                availableTags.push(mySplitResult[7]);
                availableTags.push(mySplitResult[8]);
            }
            $("#main_search_text").autocomplete({
                source: availableTags
            });
        }
    });

}