function constructArgs(target) {
    var inputs = target.find('input');
    var args = {};
    $.each(inputs, function (i, n) {
        if (n.value != '') {
            args[n.name] = n.value;
        }
    })
    return args;
}