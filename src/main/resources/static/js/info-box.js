$(".info-box-entity-heading").on("click", function() {
    $(this).parent().children('table').toggle();
    var infos = $(this).parent().parent().children().eq(1).children();

    for (var i = 3; i < 5; i++) {
        infos.eq(i).toggle();
    }

    var infoWrapper = $(this).parent().parent().children().eq(1);

    if ($(this).parent().parent().hasClass("info-box--hidden")) {
        infoWrapper
            .css("flex-direction", "column")
            .css("flex-wrap", "no-wrap")
            .css("justify-conent", "center");
    } else {
        infoWrapper
            .css("flex-direction", "row")
            .css("flex-wrap", "wrap")
            .css("justify-conent", "left");
    }

    $(this).parent().parent().toggleClass("info-box--hidden");
});
