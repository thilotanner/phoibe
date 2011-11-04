$(document).ready(function() {
    if($('#modal').length > 0) {
        $('#modal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }

    $('[modal]').click(function(e) {
        e.preventDefault();
        $('#modal').load(e.target.href, function() {
            $('#modal').modal('show');
        });
    });
});

function createAutocomplete(hiddenElement, ajaxUrl) {
    var chooserElement = $("#" + hiddenElement.attr('id') + "_chooser");
    chooserElement.autocomplete({
        source: function(request, response)
        {
            $.ajax({
                url: ajaxUrl,
                dataType: "json",
                data: {
                    search: request.term
                },
                success: function(data)
                {
                    response($.map(data, function(item)
                    {
                        return {
                            label: item.label,
                            value: item.id
                        }
                    }));
                }
            });
        },
        minLength: 1,
        focus: function(event, ui)
        {
            chooserElement.val(ui.item.label);
            return false;
        },
        select: function(event, ui)
        {
            if (ui.item) {
                hiddenElement.val(ui.item.value);
                chooserElement.val(ui.item.label);
                return false;
            }
        }
    });
}