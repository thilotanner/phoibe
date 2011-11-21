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
            if($(e.srcElement).attr('target')) {
                $('#modal').attr('target', $(e.srcElement).attr('target'));
            }
            $('#modal').modal('show');
        });
    });

    $("a[rel=popover]")
        .popover({
            offset: 10,
            html: true,
            placement: 'above',
            content: function() {
                $.ajax({
                    url: $(this).attr('data-content'),
                    async: false,
                    complete: function(data){
                        text = data.responseText;
                    }
                });
                return text;
            }
        })
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

function submitModal(modalElement, ajaxUrl) {
    element = $('#' + modalElement);
    form  = $('#' + modalElement + ' form');
    data = form.serialize();

    $.ajax({
    type: "POST",
    url: ajaxUrl,
    data: data,
    complete: function(data) {
        if(data.status == 200) {
            var object = jQuery.parseJSON(data.responseText);
            $('#' + element.attr('target')).val(object.id);
            $('#' + element.attr('target') + '_chooser').val(object.label);
            element.modal('hide');
        } else {
            element.html(data.responseText);
        }
    }
    });
}