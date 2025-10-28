(function ($, $document) {
    "use strict";

   
    $document.on("foundation-contentloaded", function () {
        showHideHandler($(".cq-dialog-checkbox-showhide"));
    });

    
    $document.on("change", ".cq-dialog-checkbox-showhide", function () {
        showHideHandler($(this));
    });

    // Function to show/hide and enable/disable field
    function showHideHandler($checkbox) {
        $checkbox.each(function () {
            var $this = $(this);
            var target = $this.data("cqDialogCheckboxShowhideTarget");
            var $target = $(target);

            if ($this.prop("checked")) {
                $target.show();
                $target.find("input, textarea, select").prop("disabled", false);
            } else {
                $target.hide();
                $target.find("input, textarea, select").val("").prop("disabled", true);
            }
        });
    }
})(Granite.$, jQuery(document));