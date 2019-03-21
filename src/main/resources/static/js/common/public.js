function constructArgs(target) {
    var inputs = target.find('input');
    var args = {};
    inputs.forEach(function (i,index) {
        if(i.val()!=''){
            args[i.attr('name')]=i.val();
        }
    })
    return args;
}