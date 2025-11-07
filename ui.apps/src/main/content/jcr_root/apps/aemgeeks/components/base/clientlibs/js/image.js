(function ($, $document) {
    "use strict";

    $document.on("dialog-ready", function () {
        var $imageField = $("[name='./imagePath']");
        var $altTextField = $("#alt-text-field").closest(".coral-Form-fieldwrapper");

        function toggleAltText() {
            var imageValue = $imageField.val();
            if (imageValue && imageValue.trim() !== "") {
                $altTextField.show();
            } else {
                $altTextField.hide();
            }
        }

        // Initial check
        toggleAltText();

        // On change event
        $imageField.on("change", toggleAltText);
    });

})(jQuery, jQuery(document));
