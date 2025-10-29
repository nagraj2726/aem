(function ($, $document) {
    "use strict";

    $document.on("dialog-ready", function () {
        var $pathField = $("#image-path-selector");
        var $altTextField = $("#alt-text-field").closest(".coral-Form-fieldwrapper");

        function toggleFields() {
            var pathValue = $pathField.val();

            if (pathValue && pathValue.trim() !== "") {
                $altTextField.show();
            } else {
                $altTextField.hide();
            }
        }

        // Initial check
        toggleFields();

        // When image path changes
        $pathField.on("change", toggleFields);
    });

})(jQuery, jQuery(document));
